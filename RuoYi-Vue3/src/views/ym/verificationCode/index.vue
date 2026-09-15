<template>
  <div class="app-container">
    <!-- 批量生成 -->
    <el-card class="generate-card mb16" shadow="never">
      <template #header><span class="card-title">批量生成查询码</span></template>
      <el-form ref="generateRef" :model="generateForm" :rules="generateRules" label-width="100px" :inline="true">
        <el-form-item label="产品类型" prop="productType">
          <el-select v-model="generateForm.productType" placeholder="请选择产品" style="width: 160px">
            <el-option v-for="item in productTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="可选前缀" prop="prefix">
          <el-input v-model="generateForm.prefix" placeholder="可留空，如 WM" maxlength="10" style="width: 140px" @input="onPrefixInput" />
        </el-form-item>
        <el-form-item label="生成数量" prop="count">
          <el-input-number v-model="generateForm.count" :min="1" :max="1000" controls-position="right" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="generateLoading" v-hasPermi="['ym:verificationCode:generate']" @click="handleGenerate">
            批量生成
          </el-button>
        </el-form-item>
      </el-form>
      <p class="generate-tip">规则：随机不可猜查询码（12 位），可选字母前缀；生成时记录产品与创建人，见证书绑码需产品类型一致</p>
      <div v-if="resultCodes.length" class="result-tags">
        <el-tag v-for="c in resultCodes" :key="c" class="code-tag">{{ c }}</el-tag>
      </div>
    </el-card>

    <!-- 列表 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="产品类型" prop="productType">
        <el-select v-model="queryParams.productType" placeholder="全部" clearable style="width: 140px">
          <el-option v-for="item in productTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="查询码" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入查询码" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="创建人" prop="createBy">
        <el-input v-model="queryParams.createBy" placeholder="创建人账号" clearable style="width: 140px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="绑定状态" prop="bindStatus">
        <el-select v-model="queryParams.bindStatus" placeholder="全部" clearable style="width: 140px">
          <el-option label="未绑定" :value="0" />
          <el-option label="已绑定" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" :disabled="multiple" @click="handleBatchDownload" v-hasPermi="['ym:verificationCode:list']">打包下载二维码</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['ym:verificationCode:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="产品类型" align="center" prop="productType" width="110">
        <template #default="scope">
          <el-tag :type="scope.row.productType === 'rice' ? 'warning' : 'success'" effect="plain">
            {{ formatProductType(scope.row.productType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="查询码" align="center" prop="code" min-width="160" />
      <el-table-column label="绑定状态" align="center" prop="bindStatus" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.bindStatus === 1 ? 'success' : 'info'">
            {{ scope.row.bindStatus === 1 ? '已绑定' : '未绑定' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="证书ID" align="center" prop="certificateId" width="90">
        <template #default="scope">
          <span>{{ scope.row.certificateId || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="createBy" width="100" show-overflow-tooltip />
      <el-table-column label="创建时间" align="center" prop="createdAt" width="170">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createdAt) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="扫码二维码" align="center" prop="codeUrl" width="100">
        <template #default="scope">
          <image-preview v-if="scope.row.codeUrl" :src="scope.row.codeUrl" :width="60" :height="60" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template #default="scope">
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['ym:verificationCode:remove']">删除</el-button>
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
  </div>
</template>

<script setup name="YmVerificationCode">
import { listVerificationCode, delVerificationCode, batchGenerateVerificationCode } from '@/api/ym/verificationCode'
import { loadEnabledProductOptions, productLabel } from '@/utils/ymProduct'
import { parseTime } from '@/utils/ruoyi'

const { proxy } = getCurrentInstance()

const productTypeOptions = ref([])
const dataList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const multiple = ref(true)
const total = ref(0)
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  productType: undefined,
  code: undefined,
  createBy: undefined,
  bindStatus: undefined
})

const generateLoading = ref(false)
const resultCodes = ref([])
const generateForm = ref({ productType: 'yangmei', prefix: '', count: 1 })
const generateRules = {
  productType: [{ required: true, message: '请选择产品类型', trigger: 'change' }],
  prefix: [
    { pattern: /^[A-Za-z]*$/, message: '仅限字母，可留空', trigger: 'blur' }
  ],
  count: [{ required: true, message: '请输入数量', trigger: 'change' }]
}

function formatProductType(type) {
  return productLabel(productTypeOptions.value, type) || type || '-'
}

function onPrefixInput(v) {
  generateForm.value.prefix = v.replace(/[^a-zA-Z]/g, '').toUpperCase()
}

function handleGenerate() {
  proxy.$refs.generateRef.validate(valid => {
    if (!valid) return
    generateLoading.value = true
    batchGenerateVerificationCode({
      productType: generateForm.value.productType,
      prefix: generateForm.value.prefix,
      count: generateForm.value.count
    })
      .then(res => {
        resultCodes.value = res.data.codes || []
        proxy.$modal.msgSuccess(`成功生成 ${res.data.count} 个查询码`)
        getList()
      })
      .finally(() => { generateLoading.value = false })
  })
}

function getList() {
  loading.value = true
  listVerificationCode(queryParams.value).then(res => {
    dataList.value = res.rows
    total.value = res.total
    loading.value = false
  })
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
  multiple.value = !selection.length
}

function handleDelete(row) {
  const delIds = row.id || ids.value
  proxy.$modal.confirm('是否确认删除？').then(() => delVerificationCode(delIds)).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

function handleBatchDownload() {
  if (!ids.value.length) {
    proxy.$modal.msgWarning('请选择要下载的查询码')
    return
  }
  const zipName = '扫码二维码_' + parseTime(new Date(), '{y}{m}{d}{h}{i}{s}') + '.zip'
  proxy.$download.zip('/ym/certificate/verificationCode/downloadZip?ids=' + ids.value.join(','), zipName)
}

loadEnabledProductOptions().then(list => {
  productTypeOptions.value = list
  if (list.length && !generateForm.value.productType) {
    generateForm.value.productType = list[0].value
  }
})
getList()
</script>

<style scoped lang="scss">
.mb16 { margin-bottom: 16px; }
.card-title { font-weight: 600; }
.generate-tip {
  margin: 0 0 12px;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}
.result-tags { margin-top: 8px; }
.code-tag { margin: 0 8px 8px 0; }
</style>
