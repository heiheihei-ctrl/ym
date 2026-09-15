<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="产品类型" prop="productType">
        <el-select v-model="queryParams.productType" placeholder="全部" clearable style="width: 140px">
          <el-option v-for="item in productTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="批次号" prop="batchNo">
        <el-input v-model="queryParams.batchNo" placeholder="批次号" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="批次名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="批次名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 140px">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button plain icon="Back" @click="goCertificate">返回见证书</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ym:batch:add']">新建批次</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['ym:batch:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['ym:batch:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="id" width="70" />
      <el-table-column label="产品" align="center" prop="productType" width="100">
        <template #default="scope">
          {{ formatProductType(scope.row.productType) }}
        </template>
      </el-table-column>
      <el-table-column label="批次号" align="center" prop="batchNo" width="140" />
      <el-table-column label="批次名称" align="left" prop="name" min-width="140" show-overflow-tooltip />
      <el-table-column label="产地" align="left" prop="origin" min-width="120" show-overflow-tooltip />
      <el-table-column label="关键日期" align="center" width="120">
        <template #default="scope">
          <span v-if="scope.row.productType === 'rice'">{{ parseTime(scope.row.harvestDate, '{y}-{m}-{d}') || '-' }}</span>
          <span v-else>{{ parseTime(scope.row.pickDate, '{y}-{m}-{d}') || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="跟采律师" align="center" prop="lawyerName" width="100" />
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">{{ scope.row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['ym:batch:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['ym:batch:remove']">删除</el-button>
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

    <el-dialog :title="title" v-model="open" width="620px" append-to-body>
      <el-form ref="batchRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="产品类型" prop="productType">
          <el-radio-group v-model="form.productType" @change="onProductTypeChange">
            <el-radio v-for="item in productTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="批次号" prop="batchNo">
          <el-input v-model="form.batchNo" placeholder="如 B202605001" maxlength="64" />
        </el-form-item>
        <el-form-item label="批次名称" prop="name">
          <el-input v-model="form.name" placeholder="批次名称" maxlength="200" />
        </el-form-item>
        <el-form-item label="产地" prop="origin">
          <el-select v-model="form.origin" placeholder="请选择产地" filterable style="width: 100%">
            <el-option v-for="item in filteredOriginOptions" :key="item.id" :label="item.name" :value="item.name" />
          </el-select>
        </el-form-item>

        <template v-if="form.productType === 'rice'">
          <el-form-item label="加工包装厂家" prop="processor">
            <el-input v-model="form.processor" placeholder="请输入加工包装厂家" maxlength="200" />
          </el-form-item>
          <el-form-item label="仓库" prop="warehouse">
            <el-input v-model="form.warehouse" placeholder="请输入仓库" maxlength="200" />
          </el-form-item>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="收割时间" prop="harvestDate">
                <el-date-picker v-model="form.harvestDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="加工时间" prop="processDate">
                <el-date-picker v-model="form.processDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <template v-else>
          <el-form-item label="采摘日期" prop="pickDate">
            <el-date-picker v-model="form.pickDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
          </el-form-item>
        </template>

        <el-form-item label="溯源视频" prop="videoUrl">
          <file-upload v-model="form.videoUrl" :limit="1" :file-size="200" :file-type="['mp4', 'mov', 'webm']" />
        </el-form-item>

        <el-form-item label="跟采律师" prop="lawyerName">
          <el-select v-model="form.lawyerName" placeholder="请选择律师" filterable clearable style="width: 100%">
            <el-option v-for="item in lawyerOptions" :key="item.id" :label="item.realName" :value="item.realName" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="YmBatch">
import { listBatch, getBatch, addBatch, updateBatch, delBatch } from '@/api/ym/batch'
import { listOrigin } from '@/api/ym/origin'
import { listLawyer } from '@/api/ym/lawyer'
import { loadEnabledProductOptions, productLabel } from '@/utils/ymProduct'
import { findYmMenuPath } from '@/utils/ymRoute'

const { proxy } = getCurrentInstance()
const router = useRouter()

function goCertificate() {
  const path = findYmMenuPath(router, 'certificate', ['verify'])
  if (path) router.push(path)
  else proxy.$modal.msgWarning('未找到见证书菜单，请重新登录后从「见证业务」进入')
}

const dataList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')
const originOptions = ref([])
const lawyerOptions = ref([])
const productTypeOptions = ref([])

const filteredOriginOptions = computed(() => {
  const type = form.value.productType || 'yangmei'
  return originOptions.value.filter(o => !o.productType || o.productType === type)
})

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    productType: undefined,
    batchNo: undefined,
    name: undefined,
    status: undefined
  },
  rules: {
    productType: [{ required: true, message: '请选择产品类型', trigger: 'change' }],
    batchNo: [{ required: true, message: '请填写批次号', trigger: 'blur' }],
    name: [{ required: true, message: '请填写批次名称', trigger: 'blur' }],
    origin: [{ required: true, message: '请选择产地', trigger: 'change' }],
    processor: [{
      validator: (_r, v, cb) => {
        if (form.value.productType === 'rice' && !(v || '').trim()) cb(new Error('请填写加工包装厂家'))
        else cb()
      },
      trigger: 'blur'
    }],
    warehouse: [{
      validator: (_r, v, cb) => {
        if (form.value.productType === 'rice' && !(v || '').trim()) cb(new Error('请填写仓库'))
        else cb()
      },
      trigger: 'blur'
    }],
    harvestDate: [{
      validator: (_r, v, cb) => {
        if (form.value.productType === 'rice' && !v) cb(new Error('请选择收割时间'))
        else cb()
      },
      trigger: 'change'
    }],
    processDate: [{
      validator: (_r, v, cb) => {
        if (form.value.productType === 'rice' && !v) cb(new Error('请选择加工时间'))
        else cb()
      },
      trigger: 'change'
    }],
    pickDate: [{
      validator: (_r, v, cb) => {
        if (form.value.productType !== 'rice' && !v) cb(new Error('请选择采摘日期'))
        else cb()
      },
      trigger: 'change'
    }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function formatProductType(type) {
  return productLabel(productTypeOptions.value, type) || type || '-'
}

function getList() {
  loading.value = true
  listBatch(queryParams.value).then(res => {
    dataList.value = res.rows
    total.value = res.total
    loading.value = false
  })
}

function loadOptions() {
  return Promise.all([
    listOrigin({ status: 1, pageNum: 1, pageSize: 500 }),
    listLawyer({ status: 1, pageNum: 1, pageSize: 500 }),
    loadEnabledProductOptions()
  ]).then(([originRes, lawyerRes, products]) => {
    originOptions.value = originRes.rows || []
    lawyerOptions.value = lawyerRes.rows || []
    productTypeOptions.value = products.length ? products : [
      { label: '杨梅', value: 'yangmei' },
      { label: '五常大米', value: 'rice' }
    ]
  })
}

function onProductTypeChange() {
  form.value.origin = undefined
  form.value.processor = undefined
  form.value.warehouse = undefined
  form.value.harvestDate = undefined
  form.value.processDate = undefined
  form.value.pickDate = undefined
  form.value.videoUrl = undefined
  proxy.$refs.batchRef?.clearValidate?.()
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    id: undefined,
    productType: 'yangmei',
    batchNo: undefined,
    name: undefined,
    origin: undefined,
    processor: undefined,
    warehouse: undefined,
    harvestDate: undefined,
    processDate: undefined,
    pickDate: undefined,
    lawyerName: undefined,
    videoUrl: undefined,
    remark: undefined,
    status: 1
  }
  proxy.resetForm('batchRef')
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
  loadOptions().then(() => {
    open.value = true
    title.value = '新建批次'
  })
}

function handleUpdate(row) {
  reset()
  const id = row.id || ids.value
  getBatch(id).then(res => {
    form.value = res.data
    if (!form.value.productType) form.value.productType = 'yangmei'
    return loadOptions()
  }).then(() => {
    open.value = true
    title.value = '修改批次'
  })
}

function submitForm() {
  proxy.$refs.batchRef.validate(valid => {
    if (!valid) return
    const payload = { ...form.value }
    const req = payload.id != null ? updateBatch(payload) : addBatch(payload)
    req.then(() => {
      proxy.$modal.msgSuccess(payload.id != null ? '修改成功' : '新增成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const delIds = row.id || ids.value
  proxy.$modal.confirm('是否确认删除选中批次？').then(() => delBatch(delIds)).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

loadOptions().then(() => getList())
</script>
