<template>
  <div class="app-container">
    <el-alert
      type="info"
      :closable="false"
      show-icon
      class="mb8"
      title="系统预置产品类型，不支持新增或删除，仅可修改编号、名称与状态。"
    />

    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="编号" prop="productCode">
        <el-input v-model="queryParams.productCode" placeholder="产品编号" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="名称" prop="productName">
        <el-input v-model="queryParams.productName" placeholder="产品名称" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px">
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
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['ym:product:edit']">修改</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="70" />
      <el-table-column label="编号" align="center" prop="productCode" min-width="120" />
      <el-table-column label="名称" align="center" prop="productName" min-width="140" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">{{ scope.row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updatedAt" width="170">
        <template #default="scope">
          <span>{{ parseTime(scope.row.updatedAt) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['ym:product:edit']">修改</el-button>
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

    <el-dialog title="修改产品" v-model="open" width="480px" append-to-body>
      <el-form ref="productRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="编号" prop="productCode">
          <el-input v-model="form.productCode" placeholder="小写字母开头，如 yangmei" maxlength="32" />
          <p class="form-tip">修改编号会同步更新已关联的批次、产地、见证书中的产品类型字段</p>
        </el-form-item>
        <el-form-item label="名称" prop="productName">
          <el-input v-model="form.productName" placeholder="产品显示名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="YmProduct">
import { listProduct, getProduct, updateProduct } from '@/api/ym/product'

const { proxy } = getCurrentInstance()

const dataList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const total = ref(0)
const title = ref('')

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    productCode: undefined,
    productName: undefined,
    status: undefined
  },
  rules: {
    productCode: [{ required: true, message: '请填写编号', trigger: 'blur' }],
    productName: [{ required: true, message: '请填写名称', trigger: 'blur' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listProduct(queryParams.value).then(res => {
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
  single.value = selection.length !== 1
}

function reset() {
  form.value = {
    id: undefined,
    productCode: undefined,
    productName: undefined,
    status: 1
  }
  proxy.resetForm('productRef')
}

function cancel() {
  open.value = false
  reset()
}

function handleUpdate(row) {
  reset()
  const id = row?.id || ids.value[0]
  getProduct(id).then(res => {
    form.value = {
      id: res.data.id,
      productCode: res.data.productCode,
      productName: res.data.productName,
      status: res.data.status
    }
    open.value = true
  })
}

function submitForm() {
  proxy.$refs.productRef.validate(valid => {
    if (!valid) return
    updateProduct({
      id: form.value.id,
      productCode: form.value.productCode,
      productName: form.value.productName,
      status: form.value.status
    }).then(() => {
      proxy.$modal.msgSuccess('修改成功')
      open.value = false
      getList()
    })
  })
}

getList()
</script>

<style scoped>
.form-tip {
  margin: 6px 0 0;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  line-height: 1.4;
}
.mb8 {
  margin-bottom: 8px;
}
</style>
