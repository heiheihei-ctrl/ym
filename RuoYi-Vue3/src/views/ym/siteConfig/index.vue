<template>
  <div class="app-container site-config-page">
    <h2 class="page-title">系统配置</h2>
    <el-card v-loading="loading" shadow="never" class="config-card">
      <el-form ref="configRef" :model="form" :rules="rules" label-position="top" class="config-form">
        <el-form-item label="律所名称" prop="firmName">
          <el-input v-model="form.firmName" placeholder="请输入律所名称" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="查询页顶部提示语" prop="queryTopTip">
          <el-input
            v-model="form.queryTopTip"
            type="textarea"
            :rows="2"
            placeholder="显示在查验页标题下方的说明文字"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="查询页底部提示语" prop="queryBottomTip">
          <el-input
            v-model="form.queryBottomTip"
            placeholder="显示在查验页页脚标语"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="50" />
        </el-form-item>
        <el-form-item label="律所地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入律所地址" maxlength="300" show-word-limit />
        </el-form-item>
        <el-form-item label="扫码后跳转地址" prop="redirectUrl">
          <el-input
            v-model="form.redirectUrl"
            placeholder="例如 https://example.com ；留空则不自动跳转"
            maxlength="500"
            show-word-limit
            clearable
          />
        </el-form-item>
        <el-form-item label="跳转等待秒数" prop="redirectDelaySeconds">
          <el-input-number
            v-model="form.redirectDelaySeconds"
            :min="0"
            :max="300"
            controls-position="right"
          />
          <span class="form-tip">证书页展示后等待指定秒数再跳转，默认 6 秒；填 0 为立即跳转</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="DocumentChecked" :loading="saving" v-hasPermi="['ym:siteConfig:edit']" @click="submitForm">
            保存配置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup name="YmSiteConfig">
import { getSiteConfig, saveSiteConfig } from '@/api/ym/siteConfig'

const { proxy } = getCurrentInstance()

const loading = ref(true)
const saving = ref(false)

const form = ref({
  firmName: '',
  queryTopTip: '',
  queryBottomTip: '',
  phone: '',
  address: '',
  redirectUrl: '',
  redirectDelaySeconds: 6
})

const rules = {
  firmName: [{ required: true, message: '请输入律所名称', trigger: 'blur' }],
  queryTopTip: [{ required: true, message: '请输入查询页顶部提示语', trigger: 'blur' }],
  queryBottomTip: [{ required: true, message: '请输入查询页底部提示语', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  address: [{ required: true, message: '请输入律所地址', trigger: 'blur' }],
  redirectDelaySeconds: [{ required: true, message: '请设置跳转等待秒数', trigger: 'change' }]
}

function loadConfig() {
  loading.value = true
  getSiteConfig()
    .then(res => {
      const data = res.data || {}
      form.value = {
        firmName: data.firmName || '',
        queryTopTip: data.queryTopTip || '',
        queryBottomTip: data.queryBottomTip || '',
        phone: data.phone || '',
        address: data.address || '',
        redirectUrl: data.redirectUrl || '',
        redirectDelaySeconds: data.redirectDelaySeconds != null ? data.redirectDelaySeconds : 6
      }
    })
    .finally(() => {
      loading.value = false
    })
}

function submitForm() {
  proxy.$refs.configRef.validate(valid => {
    if (!valid) return
    saving.value = true
    saveSiteConfig({
      ...form.value,
      redirectUrl: (form.value.redirectUrl || '').trim()
    })
      .then(() => {
        proxy.$modal.msgSuccess('保存成功')
        loadConfig()
      })
      .finally(() => {
        saving.value = false
      })
  })
}

loadConfig()
</script>

<style scoped lang="scss">
.site-config-page {
  max-width: 720px;
}

.page-title {
  margin: 0 0 20px;
  font-size: 22px;
  font-weight: 700;
  color: #303133;
}

.config-card {
  border-radius: 4px;
}

.config-form {
  :deep(.el-form-item__label) {
    font-weight: 600;
    color: #303133;
    padding-bottom: 8px;
  }

  :deep(.el-input__wrapper),
  :deep(.el-textarea__inner) {
    border-radius: 4px;
  }
}

.form-tip {
  display: block;
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}
</style>
