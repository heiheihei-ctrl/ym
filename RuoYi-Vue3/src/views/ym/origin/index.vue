<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="产地地址" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="省市区或详细地址"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
          <el-option
            v-for="item in ymEnableOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ym:origin:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['ym:origin:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['ym:origin:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="originList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="产地地址" align="left" prop="name" min-width="220" show-overflow-tooltip />
      <el-table-column label="排序" align="center" prop="sortOrder" width="80" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">{{ formatEnable(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createdAt" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createdAt) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['ym:origin:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['ym:origin:remove']">删除</el-button>
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
      <el-form ref="originRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="所在地区" required>
          <el-cascader
            v-model="regionCodes"
            :options="regionOptions"
            placeholder="请选择省 / 市 / 区"
            filterable
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item v-if="form.id && form.name" label="当前地址">
          <span class="current-name">{{ form.name }}</span>
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input
            v-model="form.detailAddress"
            type="textarea"
            :rows="2"
            placeholder="街道、村组、门牌号等"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="完整地址">
          <span class="full-address-preview">{{ fullAddressPreview || '选择省市区并填写详细地址后自动生成' }}</span>
        </el-form-item>
        <el-form-item label="排序序号" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="item in ymEnableOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
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

<script setup name="YmOrigin">
import { listOrigin, getOrigin, addOrigin, updateOrigin, delOrigin } from '@/api/ym/origin'
import { regionData, buildFullAddress } from '@/utils/chinaRegion'

const { proxy } = getCurrentInstance()

const ymEnableOptions = [
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 }
]

const regionOptions = regionData
const regionCodes = ref([])

const originList = ref([])
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
    name: undefined,
    status: undefined
  },
  rules: {
    detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

const fullAddressPreview = computed(() => {
  const codes = regionCodes.value || []
  return buildFullAddress(codes[0], codes[1], codes[2], form.value.detailAddress)
})

function formatEnable(status) {
  const item = ymEnableOptions.find(o => o.value === status)
  return item ? item.label : status
}

function buildSubmitPayload() {
  const codes = regionCodes.value || []
  return {
    id: form.value.id,
    name: buildFullAddress(codes[0], codes[1], codes[2], form.value.detailAddress),
    sortOrder: form.value.sortOrder,
    status: form.value.status
  }
}

function getList() {
  loading.value = true
  listOrigin(queryParams.value).then(response => {
    originList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  regionCodes.value = []
  form.value = {
    id: undefined,
    name: undefined,
    detailAddress: undefined,
    sortOrder: 0,
    status: 1
  }
  proxy.resetForm('originRef')
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
  open.value = true
  title.value = '添加产地'
}

function handleUpdate(row) {
  reset()
  const id = row.id || ids.value
  getOrigin(id).then(response => {
    form.value = response.data
    regionCodes.value = []
    form.value.detailAddress = undefined
    open.value = true
    title.value = '修改产地'
  })
}

function submitForm() {
  if (!regionCodes.value || regionCodes.value.length < 3) {
    proxy.$modal.msgError('请选择省、市、区')
    return
  }
  proxy.$refs['originRef'].validate(valid => {
    if (valid) {
      const payload = buildSubmitPayload()
      if (payload.id !== undefined) {
        updateOrigin(payload).then(() => {
          proxy.$modal.msgSuccess('修改成功')
          open.value = false
          getList()
        })
      } else {
        addOrigin(payload).then(() => {
          proxy.$modal.msgSuccess('新增成功')
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row) {
  const delIds = row.id || ids.value
  proxy.$modal.confirm('是否确认删除编号为"' + delIds + '"的数据？').then(() => {
    return delOrigin(delIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

getList()
</script>

<style scoped>
.full-address-preview,
.current-name {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  line-height: 1.5;
  word-break: break-all;
}
</style>
