<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="系统用户" prop="username">
        <el-input v-model="queryParams.username" placeholder="请输入用户账号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="真实姓名" prop="realName">
        <el-input v-model="queryParams.realName" placeholder="请输入真实姓名" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
          <el-option v-for="item in ymEnableOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ym:lawyer:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['ym:lawyer:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['ym:lawyer:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="lawyerList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="系统用户" align="center" prop="username" min-width="110" />
      <el-table-column label="用户昵称" align="center" prop="nickName" min-width="110" />
      <el-table-column label="手机号" align="center" prop="phone" width="130" />
      <el-table-column label="真实姓名" align="center" prop="realName" />
      <el-table-column label="执业证号" align="center" prop="lawyerNo" />
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
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['ym:lawyer:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['ym:lawyer:remove']">删除</el-button>
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
      <el-form ref="lawyerRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="系统用户" prop="userId">
          <el-select
            v-model="form.userId"
            placeholder="请选择若依系统用户"
            filterable
            style="width: 100%"
            @change="handleUserChange"
          >
            <el-option
              v-for="item in userOptions"
              :key="item.userId"
              :label="formatUserLabel(item)"
              :value="item.userId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="20" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入律师真实姓名" />
        </el-form-item>
        <el-form-item label="执业证号" prop="lawyerNo">
          <el-input
            v-model="form.lawyerNo"
            placeholder="请输入17位数字执业证号"
            maxlength="17"
            show-word-limit
            @input="handleLawyerNoInput"
          />
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

<script setup name="YmLawyer">
import { listLawyer, getLawyer, addLawyer, updateLawyer, delLawyer } from '@/api/ym/lawyer'
import { listUser } from '@/api/system/user'
import { validateLawyerNoRule } from '@/utils/lawyerNo'

const { proxy } = getCurrentInstance()

const ymEnableOptions = [
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 }
]

const lawyerList = ref([])
const userOptions = ref([])
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
    username: undefined,
    phone: undefined,
    realName: undefined,
    status: undefined
  },
  rules: {
    userId: [{ required: true, message: '请选择系统用户', trigger: 'change' }],
    phone: [{ required: true, message: '手机号不能为空', trigger: 'blur' }],
    realName: [{ required: true, message: '真实姓名不能为空', trigger: 'blur' }],
    lawyerNo: [{ required: true, validator: validateLawyerNoRule, trigger: 'blur' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function formatEnable(status) {
  const item = ymEnableOptions.find(o => o.value === status)
  return item ? item.label : status
}

function formatUserLabel(user) {
  return `${user.userName}（${user.nickName || '-'}）`
}

function handleLawyerNoInput(val) {
  form.value.lawyerNo = (val || '').replace(/\D/g, '').slice(0, 17)
}

function loadUserOptions() {
  return listUser({ pageNum: 1, pageSize: 500, status: '0' }).then(response => {
    userOptions.value = response.rows || []
  })
}

function handleUserChange(userId) {
  const user = userOptions.value.find(item => item.userId === userId)
  if (!user) {
    return
  }
  if (!form.value.phone && user.phonenumber) {
    form.value.phone = user.phonenumber
  }
  if (!form.value.realName && user.nickName) {
    form.value.realName = user.nickName
  }
}

function getList() {
  loading.value = true
  listLawyer(queryParams.value).then(response => {
    lawyerList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    id: undefined,
    userId: undefined,
    phone: undefined,
    realName: undefined,
    lawyerNo: undefined,
    status: 1
  }
  proxy.resetForm('lawyerRef')
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
  loadUserOptions().then(() => {
    open.value = true
    title.value = '添加律师'
  })
}

function handleUpdate(row) {
  reset()
  const id = row.id || ids.value
  Promise.all([getLawyer(id), loadUserOptions()]).then(([response]) => {
    form.value = response.data
    open.value = true
    title.value = '修改律师'
  })
}

function submitForm() {
  proxy.$refs['lawyerRef'].validate(valid => {
    if (valid) {
      if (form.value.id !== undefined) {
        updateLawyer(form.value).then(() => {
          proxy.$modal.msgSuccess('修改成功')
          open.value = false
          getList()
        })
      } else {
        addLawyer(form.value).then(() => {
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
    return delLawyer(delIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

getList()
</script>
