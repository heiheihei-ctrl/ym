<template>
  <div class="app-container">
    <!-- 批量生成 -->
    <el-card class="generate-card mb16" shadow="never">
      <template #header><span class="card-title">批量生成查询码</span></template>
      <el-form ref="generateRef" :model="generateForm" :rules="generateRules" label-width="100px" :inline="true">
        <el-form-item label="查询码前缀" prop="prefix">
          <el-input v-model="generateForm.prefix" placeholder="WM" maxlength="10" style="width: 120px" @input="onPrefixInput" />
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
      <p class="generate-tip">规则：前缀 + 年月(6位) + 流水号(6位)，如 WM202605000001；生成时同步创建扫码二维码</p>
      <div v-if="resultCodes.length" class="result-tags">
        <el-tag v-for="c in resultCodes" :key="c" class="code-tag">{{ c }}</el-tag>
      </div>
    </el-card>

    <!-- 列表 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="查询码" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入查询码" clearable style="width: 200px" @keyup.enter="handleQuery" />
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
      <el-table-column label="查询码" align="center" prop="code" min-width="160" />
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
import { parseTime } from '@/utils/ruoyi'

const { proxy } = getCurrentInstance()

const dataList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const multiple = ref(true)
const total = ref(0)
const queryParams = ref({ pageNum: 1, pageSize: 10, code: undefined })

const generateLoading = ref(false)
const resultCodes = ref([])
const generateForm = ref({ prefix: 'WM', count: 10 })
const generateRules = {
  prefix: [
    { required: true, message: '请输入前缀', trigger: 'blur' },
    { pattern: /^[A-Za-z]+$/, message: '仅限字母', trigger: 'blur' }
  ],
  count: [{ required: true, message: '请输入数量', trigger: 'change' }]
}

function onPrefixInput(v) {
  generateForm.value.prefix = v.replace(/[^a-zA-Z]/g, '').toUpperCase()
}

function handleGenerate() {
  proxy.$refs.generateRef.validate(valid => {
    if (!valid) return
    generateLoading.value = true
    batchGenerateVerificationCode({ prefix: generateForm.value.prefix, count: generateForm.value.count })
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
