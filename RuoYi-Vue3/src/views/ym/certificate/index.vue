<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="查询码" prop="verificationCode">
        <el-input v-model="queryParams.verificationCode" placeholder="请输入查询码" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="产品类型" prop="productType">
        <el-select v-model="queryParams.productType" placeholder="请选择产品类型" clearable style="width: 160px">
          <el-option v-for="item in productTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
          <el-option v-for="item in certStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="批次" prop="batchName">
        <el-input v-model="queryParams.batchName" placeholder="批次名称" clearable style="width: 180px" @keyup.enter="handleQuery" />
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
        <el-button type="info" plain icon="List" @click="goBatchManage" v-hasPermi="['ym:batch:list']">批次管理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Link" :disabled="single" @click="handleBindRange" v-hasPermi="['ym:certificate:edit']">批量绑码</el-button>
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
      <el-table-column label="代表查询码" align="center" prop="verificationCode" width="150" />
      <el-table-column label="产品类型" align="center" prop="productType" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.productType === 'rice' ? 'warning' : 'success'" effect="plain">
            {{ formatProductType(scope.row.productType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="已绑码数" align="center" prop="boundCodeCount" width="90" />
      <el-table-column label="批次" align="center" prop="batchName" min-width="120" show-overflow-tooltip />
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
      <el-table-column label="操作" width="220" align="center" fixed="right" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Link" @click="handleBindRange(scope.row)" v-hasPermi="['ym:certificate:edit']">绑码</el-button>
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

    <el-dialog :title="title" v-model="open" width="640px" append-to-body>
      <el-form ref="certificateRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="产品类型" prop="productType">
          <el-radio-group v-model="form.productType" @change="onProductTypeChange">
            <el-radio v-for="item in productTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="所属批次" prop="batchId">
          <el-select v-model="form.batchId" placeholder="请先选产品类型，再选对应批次" filterable style="width: 100%" @change="onBatchChange">
            <el-option
              v-for="item in filteredBatchOptions"
              :key="item.id"
              :label="item.batchNo + ' · ' + item.name"
              :value="item.id"
            />
          </el-select>
          <p class="form-tip">厂家、仓库、收割/加工时间等在「批次管理」中维护</p>
        </el-form-item>
        <el-form-item v-if="!isEdit" label="绑定查询码" prop="verificationCodeIds">
          <el-select
            v-model="form.verificationCodeIds"
            multiple
            filterable
            collapse-tags
            collapse-tags-tooltip
            placeholder="可多选未绑定查询码"
            style="width: 100%"
          >
            <el-option
              v-for="item in codeOptions"
              :key="item.id"
              :label="formatCodeOptionLabel(item)"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="isEdit" label="代表查询码">
          <el-input :model-value="form.verificationCode" disabled />
        </el-form-item>
        <el-form-item label="产地" prop="origin">
          <el-input v-model="form.origin" placeholder="选择批次后自动带出" disabled />
        </el-form-item>

        <template v-if="form.productType !== 'rice'">
          <el-form-item label="基地详情链接" prop="baseDetailUrl">
            <el-input
              v-model="form.baseDetailUrl"
              placeholder="扫码页「查看该基地详情」跳转地址，可不填"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
          <el-form-item label="包装日期" prop="packDate">
            <el-date-picker v-model="form.packDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
          </el-form-item>
        </template>

        <el-form-item label="见证律师" prop="lawyerName">
          <el-select v-model="form.lawyerName" placeholder="请选择见证律师" filterable style="width: 100%">
            <el-option v-for="item in lawyerOptions" :key="item.id" :label="item.realName" :value="item.realName" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option v-for="item in formStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
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

    <el-dialog title="批量绑定查询码" v-model="bindOpen" width="520px" append-to-body>
      <el-form ref="bindRef" :model="bindForm" :rules="bindRules" label-width="110px">
        <el-form-item label="见证书">
          <span>#{{ bindForm.certificateId }} {{ bindForm.certLabel }}</span>
        </el-form-item>
        <el-form-item label="查询码" prop="verificationCodeIds">
          <el-select
            v-model="bindForm.verificationCodeIds"
            multiple
            filterable
            collapse-tags
            collapse-tags-tooltip
            placeholder="选择未绑定查询码"
            style="width: 100%"
          >
            <el-option
              v-for="item in codeOptions"
              :key="item.id"
              :label="formatCodeOptionLabel(item)"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitBindRange">绑 定</el-button>
        <el-button @click="bindOpen = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="YmCertificate">
import { listCertificate, getCertificate, addCertificate, updateCertificate, delCertificate, bindCertificateRange } from '@/api/ym/certificate'
import { listUnusedVerificationCode } from '@/api/ym/verificationCode'
import { listOrigin } from '@/api/ym/origin'
import { listLawyer } from '@/api/ym/lawyer'
import { listBatch } from '@/api/ym/batch'
import { loadEnabledProductOptions, productLabel } from '@/utils/ymProduct'
import { findYmMenuPath } from '@/utils/ymRoute'
import { parseTime } from '@/utils/ruoyi'

const { proxy } = getCurrentInstance()
const router = useRouter()

const codeOptions = ref([])
const originOptions = ref([])
const lawyerOptions = ref([])
const batchOptions = ref([])
const isEdit = ref(false)

const productTypeOptions = ref([])
const certStatusOptions = [
  { label: '未使用', value: 0 },
  { label: '未激活', value: 1 },
  { label: '已激活', value: 2 },
  { label: '已删除', value: 3 }
]
const formStatusOptions = certStatusOptions.filter(item => item.value !== 0)

const certificateList = ref([])
const open = ref(false)
const bindOpen = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')
const selectedRows = ref([])

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    verificationCode: undefined,
    productType: undefined,
    status: undefined,
    batchName: undefined,
    origin: undefined,
    lawyerName: undefined
  },
  rules: {
    productType: [{ required: true, message: '请选择产品类型', trigger: 'change' }],
    batchId: [{ required: true, message: '请选择所属批次', trigger: 'change' }],
    verificationCodeIds: [{
      validator: (_r, v, cb) => {
        if (!isEdit.value && (!v || !v.length)) cb(new Error('请选择至少一个查询码'))
        else cb()
      },
      trigger: 'change'
    }],
    packDate: [{
      validator: (_r, v, cb) => {
        if (form.value.productType !== 'rice' && !v) cb(new Error('请选择包装日期'))
        else cb()
      },
      trigger: 'change'
    }],
    lawyerName: [{ required: true, message: '请选择见证律师', trigger: 'change' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }],
    certImage: [{ required: true, message: '请上传见证书图片', trigger: 'change' }],
    certFile: [{ required: true, message: '请上传见证书 PDF', trigger: 'change' }]
  },
  bindForm: {
    certificateId: undefined,
    certLabel: '',
    verificationCodeIds: []
  },
  bindRules: {
    verificationCodeIds: [{
      type: 'array',
      required: true,
      min: 1,
      message: '请选择要绑定的查询码',
      trigger: 'change'
    }]
  }
})

const { queryParams, form, rules, bindForm, bindRules } = toRefs(data)

const filteredBatchOptions = computed(() => {
  const type = form.value.productType || 'yangmei'
  return batchOptions.value.filter(b => (b.productType || 'yangmei') === type)
})

function goBatchManage() {
  const path = findYmMenuPath(router, 'batch')
  if (!path) {
    proxy.$modal.msgWarning('未找到批次管理菜单，请重新登录或检查「见证业务」权限')
    return
  }
  router.push(path)
}

function fileUrl(path) {
  if (!path) return ''
  if (/^https?:\/\//i.test(path)) return path
  return import.meta.env.VITE_APP_BASE_API + path
}

function formatProductType(type) {
  return productLabel(productTypeOptions.value, type) || type || '-'
}

/** 下拉展示：查询码 · 产品 · 创建时间 */
function formatCodeOptionLabel(item) {
  if (!item) return ''
  const code = item.code || ''
  const product = formatProductType(item.productType)
  const created = item.createdAt ? parseTime(item.createdAt) : ''
  return [code, product, created].filter(Boolean).join(' · ')
}

function loadProductTypeOptions() {
  return loadEnabledProductOptions().then(list => {
    productTypeOptions.value = list
    if (!list.length) {
      productTypeOptions.value = [
        { label: '杨梅', value: 'yangmei' },
        { label: '五常大米', value: 'rice' }
      ]
    }
  })
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

function loadOriginOptions(productType, keepCurrentOrigin) {
  const type = productType || 'yangmei'
  return listOrigin({ status: 1, productType: type, pageNum: 1, pageSize: 500 }).then(originRes => {
    originOptions.value = originRes.rows || []
    if (keepCurrentOrigin && form.value.origin
        && !originOptions.value.some(item => item.name === form.value.origin)) {
      originOptions.value.unshift({ id: -1, name: form.value.origin, productType: type })
    }
  })
}

function loadFormOptions(currentVerificationCodeId) {
  const productType = form.value.productType || 'yangmei'
  return Promise.all([
    listUnusedVerificationCode({ productType }),
    listOrigin({ status: 1, productType, pageNum: 1, pageSize: 500 }),
    listLawyer({ status: 1, pageNum: 1, pageSize: 500 }),
    listBatch({ status: 1, pageNum: 1, pageSize: 500 })
  ]).then(([codeRes, originRes, lawyerRes, batchRes]) => {
    codeOptions.value = codeRes.data || []
    if (currentVerificationCodeId && !codeOptions.value.some(item => item.id === currentVerificationCodeId)) {
      codeOptions.value.unshift({ id: currentVerificationCodeId, code: form.value.verificationCode })
    }
    originOptions.value = originRes.rows || []
    if (form.value.origin && !originOptions.value.some(item => item.name === form.value.origin)) {
      originOptions.value.unshift({ id: -1, name: form.value.origin, productType })
    }
    lawyerOptions.value = lawyerRes.rows || []
    batchOptions.value = batchRes.rows || []
  })
}

function onProductTypeChange() {
  form.value.batchId = undefined
  form.value.origin = undefined
  form.value.verificationCodeIds = []
  proxy.$refs.certificateRef?.clearValidate?.(['batchId', 'packDate'])
  loadFormOptions()
}

function onBatchChange(batchId) {
  const batch = batchOptions.value.find(b => b.id === batchId)
  if (!batch) return
  if (batch.productType) form.value.productType = batch.productType
  form.value.origin = batch.origin || form.value.origin
  if (!form.value.lawyerName && batch.lawyerName) form.value.lawyerName = batch.lawyerName
}

function reset() {
  form.value = {
    id: undefined,
    productType: 'yangmei',
    batchId: undefined,
    verificationCodeId: undefined,
    verificationCodeIds: [],
    verificationCode: undefined,
    origin: undefined,
    processor: undefined,
    warehouse: undefined,
    harvestDate: undefined,
    processDate: undefined,
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

function buildCertPayload() {
  const isRice = form.value.productType === 'rice'
  return {
    id: form.value.id,
    productType: form.value.productType || 'yangmei',
    batchId: form.value.batchId,
    baseDetailUrl: isRice ? undefined : ((form.value.baseDetailUrl || '').trim() || undefined),
    packDate: isRice ? undefined : form.value.packDate,
    lawyerName: form.value.lawyerName,
    certImage: form.value.certImage,
    certFile: form.value.certFile,
    status: form.value.status,
    verificationCodeIds: form.value.verificationCodeIds
  }
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
  selectedRows.value = selection
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
    if (!form.value.productType) form.value.productType = 'yangmei'
    if (!form.value.verificationCodeId && form.value.code) {
      form.value.verificationCodeId = form.value.code
    }
    return loadFormOptions(form.value.verificationCodeId)
  }).then(() => {
    open.value = true
    title.value = '修改见证'
  })
}

function handleBindRange(row) {
  const target = row && row.id ? row : selectedRows.value[0]
  if (!target) {
    proxy.$modal.msgWarning('请先选择一条见证书')
    return
  }
  bindForm.value = {
    certificateId: target.id,
    certLabel: (target.batchName || '') + ' / ' + (target.verificationCode || ''),
    verificationCodeIds: []
  }
  const productType = target.productType || 'yangmei'
  listUnusedVerificationCode({ productType }).then(res => {
    codeOptions.value = res.data || []
    bindOpen.value = true
  })
}

function submitBindRange() {
  proxy.$refs.bindRef.validate(valid => {
    if (!valid) return
    bindCertificateRange({
      certificateId: bindForm.value.certificateId,
      verificationCodeIds: bindForm.value.verificationCodeIds
    }).then(res => {
      proxy.$modal.msgSuccess('成功绑定 ' + (res.data?.boundCount || 0) + ' 个查询码')
      bindOpen.value = false
      getList()
    })
  })
}

function submitForm() {
  proxy.$refs['certificateRef'].validate(valid => {
    if (!valid) return
    const payload = buildCertPayload()
    if (isEdit.value) {
      updateCertificate(payload).then(() => {
        proxy.$modal.msgSuccess('修改成功')
        open.value = false
        getList()
      })
      return
    }
    addCertificate(payload).then(() => {
      proxy.$modal.msgSuccess('提交见证成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const delIds = row.id || ids.value
  proxy.$modal.confirm('是否确认删除编号为"' + delIds + '"的数据？删除后将解除码绑定。').then(() => {
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
  loadProductTypeOptions()
  applyRouteQuery()
  getList()
})
</script>

<style scoped lang="scss">
.cert-file-form-item :deep(.el-form-item__content) {
  display: block;
}
.form-tip {
  margin: 6px 0 0;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  line-height: 1.4;
}
</style>
