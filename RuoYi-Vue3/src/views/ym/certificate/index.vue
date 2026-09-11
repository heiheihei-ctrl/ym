<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="查询码" prop="verificationCode">
        <el-input v-model="queryParams.verificationCode" placeholder="请输入查询码" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
          <el-option v-for="item in certStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="产地" prop="origin">
        <el-input v-model="queryParams.origin" placeholder="产地来源" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="见证律师" prop="lawyerName">
        <el-input v-model="queryParams.lawyerName" placeholder="律师姓名" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ym:certificate:add']">提交见证</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['ym:certificate:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['ym:certificate:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="certificateList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="id" width="70" />
      <el-table-column label="查询码" align="center" prop="verificationCode" width="160" />
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="scope">
          <el-tag :type="certStatusTag(scope.row.status)">{{ formatCertStatus(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="产地" align="center" prop="origin" min-width="120" show-overflow-tooltip />
      <el-table-column label="采摘日期" align="center" prop="pickDate" width="110">
        <template #default="scope">
          <span>{{ parseTime(scope.row.pickDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="包装日期" align="center" prop="packDate" width="110">
        <template #default="scope">
          <span>{{ parseTime(scope.row.packDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="见证律师" align="center" prop="lawyerName" width="100" />
      <el-table-column label="见证书PDF" align="center" prop="certFile" width="110">
        <template #default="scope">
          <el-link v-if="scope.row.certFile" type="primary" :href="fileUrl(scope.row.certFile)" target="_blank">查看PDF</el-link>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="审核人" align="center" prop="auditBy" width="90" />
      <el-table-column label="创建时间" align="center" prop="createdAt" width="170">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createdAt) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" fixed="right" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['ym:certificate:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['ym:certificate:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" v-model="open" width="560px" append-to-body>
      <el-form ref="certificateRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="查询码" prop="verificationCodeId">
          <el-select
            v-model="form.verificationCodeId"
            placeholder="请选择未绑定的查询码"
            filterable
            style="width: 100%"
            :disabled="isEdit"
          >
            <el-option
              v-for="item in codeOptions"
              :key="item.id"
              :label="item.code"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="产地来源" prop="origin">
          <el-select v-model="form.origin" placeholder="请选择产地来源" filterable style="width: 100%">
            <el-option v-for="item in originOptions" :key="item.id" :label="item.name" :value="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="基地详情链接" prop="baseDetailUrl">
          <el-input
            v-model="form.baseDetailUrl"
            placeholder="扫码页「查看该基地详情」跳转地址，可不填"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="采摘日期" prop="pickDate">
              <el-date-picker
                v-model="form.pickDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="包装日期" prop="packDate">
              <el-date-picker
                v-model="form.packDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="见证律师" prop="lawyerName">
          <el-select v-model="form.lawyerName" placeholder="请选择见证律师" filterable style="width: 100%">
            <el-option
              v-for="item in lawyerOptions"
              :key="item.id"
              :label="item.realName"
              :value="item.realName"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option
              v-for="item in formStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="见证书图片" prop="certImage">
          <image-upload v-model="form.certImage" :limit="1" :file-size="5" :file-type="['png', 'jpg', 'jpeg', 'gif']" />
        </el-form-item>
        <el-form-item label="见证书PDF" prop="certFile" class="cert-file-form-item">
          <file-upload v-model="form.certFile" :limit="1" :file-size="20" :file-type="['pdf']" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">{{ isEdit ? '确 定' : '提交见证' }}</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="YmCertificate">
import { listCertificate, getCertificate, addCertificate, updateCertificate, delCertificate } from '@/api/ym/certificate'
import { listUnusedVerificationCode } from '@/api/ym/verificationCode'
import { listOrigin } from '@/api/ym/origin'
import { listLawyer } from '@/api/ym/lawyer'

const { proxy } = getCurrentInstance()

const codeOptions = ref([])
const originOptions = ref([])
const lawyerOptions = ref([])
const isEdit = ref(false)

const certStatusOptions = [
  { label: '未使用', value: 0 },
  { label: '未激活', value: 1 },
  { label: '已激活', value: 2 },
  { label: '已删除', value: 3 }
]

/** 表单可选状态（证书记录不含「未使用」） */
const formStatusOptions = certStatusOptions.filter(item => item.value !== 0)

const certificateList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    verificationCode: undefined,
    status: undefined,
    origin: undefined,
    lawyerName: undefined
  },
  rules: {
    verificationCodeId: [{ required: true, message: '请选择未绑定的查询码', trigger: 'change' }],
    origin: [{ required: true, message: '请选择产地来源', trigger: 'change' }],
    pickDate: [{ required: true, message: '请选择采摘日期', trigger: 'change' }],
    packDate: [{ required: true, message: '请选择包装日期', trigger: 'change' }],
    lawyerName: [{ required: true, message: '请选择见证律师', trigger: 'change' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }],
    certImage: [{ required: true, message: '请上传见证书图片', trigger: 'change' }],
    certFile: [{ required: true, message: '请上传见证书 PDF', trigger: 'change' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function fileUrl(path) {
  if (!path) return ''
  if (/^https?:\/\//i.test(path)) return path
  return import.meta.env.VITE_APP_BASE_API + path
}

function formatCertStatus(status) {
  const item = certStatusOptions.find(o => o.value === status)
  return item ? item.label : status
}

function certStatusTag(status) {
  const map = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }
  return map[status] || ''
}

function getList() {
  loading.value = true
  listCertificate(queryParams.value).then(response => {
    certificateList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function loadFormOptions(currentVerificationCodeId) {
  const codePromise = listUnusedVerificationCode()
  const originPromise = listOrigin({ status: 1, pageNum: 1, pageSize: 500 })
  const lawyerPromise = listLawyer({ status: 1, pageNum: 1, pageSize: 500 })
  return Promise.all([codePromise, originPromise, lawyerPromise]).then(([codeRes, originRes, lawyerRes]) => {
    codeOptions.value = codeRes.data || []
    if (currentVerificationCodeId && !codeOptions.value.some(item => item.id === currentVerificationCodeId)) {
      codeOptions.value.unshift({ id: currentVerificationCodeId, code: form.value.verificationCode })
    }
    originOptions.value = originRes.rows || []
    lawyerOptions.value = lawyerRes.rows || []
  })
}

function reset() {
  form.value = {
    id: undefined,
    verificationCodeId: undefined,
    verificationCode: undefined,
    origin: undefined,
    pickDate: undefined,
    packDate: undefined,
    lawyerName: undefined,
    status: 1,
    baseDetailUrl: undefined,
    certImage: undefined,
    certFile: undefined
  }
  proxy.resetForm('certificateRef')
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  isEdit.value = false
  form.value.status = 1
  loadFormOptions().then(() => {
    open.value = true
    title.value = '提交见证'
  })
}

function handleUpdate(row) {
  reset()
  isEdit.value = true
  const id = row.id || ids.value
  getCertificate(id).then(response => {
    form.value = response.data
    if (!form.value.verificationCodeId && form.value.code) {
      form.value.verificationCodeId = form.value.code
    }
    return loadFormOptions(form.value.verificationCodeId)
  }).then(() => {
    open.value = true
    title.value = '修改见证'
  })
}

function submitForm() {
  proxy.$refs['certificateRef'].validate(valid => {
    if (!valid) {
      return
    }
    if (isEdit.value) {
      updateCertificate({
        id: form.value.id,
        origin: form.value.origin,
        baseDetailUrl: (form.value.baseDetailUrl || '').trim(),
        pickDate: form.value.pickDate,
        packDate: form.value.packDate,
        lawyerName: form.value.lawyerName,
        certImage: form.value.certImage,
        certFile: form.value.certFile,
        status: form.value.status
      }).then(() => {
        proxy.$modal.msgSuccess('修改成功')
        open.value = false
        getList()
      })
      return
    }
    addCertificate({
      verificationCodeId: form.value.verificationCodeId,
      origin: form.value.origin,
      baseDetailUrl: (form.value.baseDetailUrl || '').trim() || undefined,
      pickDate: form.value.pickDate,
      packDate: form.value.packDate,
      lawyerName: form.value.lawyerName,
      certImage: form.value.certImage,
      certFile: form.value.certFile,
      status: form.value.status
    }).then(() => {
      proxy.$modal.msgSuccess('提交见证成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const delIds = row.id || ids.value
  proxy.$modal.confirm('是否确认删除编号为"' + delIds + '"的数据？').then(() => {
    return delCertificate(delIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

function applyRouteQuery() {
  const q = route.query
  if (q.status !== undefined && q.status !== '') {
    const status = Number(q.status)
    if (!Number.isNaN(status)) {
      queryParams.value.status = status
    }
  }
  if (q.action === 'add') {
    nextTick(() => handleAdd())
  }
}

const route = useRoute()

onMounted(() => {
  applyRouteQuery()
  getList()
})
</script>

<style scoped lang="scss">
.cert-file-form-item :deep(.el-form-item__content) {
  display: block;
}
</style>
