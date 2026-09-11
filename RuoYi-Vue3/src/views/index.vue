<template>
  <div class="workbench-page">
    <header class="workbench-header">
      <div class="header-text">
        <h1 class="page-title">工作台</h1>
        <p class="page-subtitle">见证书管理系统概览</p>
      </div>
      <el-button
        type="primary"
        icon="Plus"
        v-hasPermi="['ym:certificate:add']"
        @click="goNewCertificate"
      >
        新建证书
      </el-button>
    </header>

    <el-row :gutter="16" class="stat-row" v-loading="statsLoading">
      <el-col :xs="12" :sm="12" :md="6" v-for="item in statCards" :key="item.key">
        <div class="stat-card" @click="item.onClick && item.onClick()">
          <div class="stat-icon" :class="item.iconClass">
            <el-icon :size="22"><component :is="item.icon" /></el-icon>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <section class="quick-section">
      <h2 class="section-title">快速操作</h2>
      <div class="quick-actions">
        <el-button
          v-for="action in quickActions"
          :key="action.key"
          type="primary"
          plain
          class="quick-btn"
          v-hasPermi="action.permi"
          @click="action.handler"
        >
          {{ action.label }}
        </el-button>
      </div>
    </section>
  </div>
</template>

<script setup name="Index">
import { Document, CircleCheck, Clock, Delete } from '@element-plus/icons-vue'
import { listCertificate } from '@/api/ym/certificate'

const router = useRouter()

const statsLoading = ref(false)
const stats = ref({
  total: 0,
  activated: 0,
  pending: 0,
  deleted: 0
})

const statCards = computed(() => [
  {
    key: 'total',
    label: '证书总数',
    value: stats.value.total,
    icon: Document,
    iconClass: 'icon-default',
    onClick: () => goCertificateList()
  },
  {
    key: 'activated',
    label: '已激活',
    value: stats.value.activated,
    icon: CircleCheck,
    iconClass: 'icon-success',
    onClick: () => goCertificateList({ status: 2 })
  },
  {
    key: 'pending',
    label: '待激活',
    value: stats.value.pending,
    icon: Clock,
    iconClass: 'icon-warning',
    onClick: () => goCertificateList({ status: 1 })
  },
  {
    key: 'deleted',
    label: '已删除',
    value: stats.value.deleted,
    icon: Delete,
    iconClass: 'icon-muted',
    onClick: () => goCertificateList({ status: 3 })
  }
])

const quickActions = [
  {
    key: 'new',
    label: '新建证书',
    permi: ['ym:certificate:add'],
    handler: goNewCertificate
  },
  {
    key: 'pending',
    label: '待激活证书',
    permi: ['ym:certificate:list'],
    handler: () => goCertificateList({ status: 1 })
  },
  {
    key: 'code',
    label: '批量生成查询码',
    permi: ['ym:verificationCode:generate'],
    handler: goVerificationCode
  },
  {
    key: 'origin',
    label: '管理产地',
    permi: ['ym:origin:list'],
    handler: goOrigin
  }
]

function findRoutePath(keyword, exclude = []) {
  const routes = router.getRoutes()
  const matched = routes
    .filter(r => r.path && r.path !== '/' && !r.path.includes(':'))
    .filter(r => !exclude.some(ex => r.path.toLowerCase().includes(ex)))
    .filter(r => r.path.toLowerCase().includes(keyword.toLowerCase()))
  if (!matched.length) {
    return null
  }
  return matched.sort((a, b) => b.path.length - a.path.length)[0].path
}

function navigate(path, query) {
  if (!path) {
    ElMessage.warning('未找到对应菜单，请从侧边栏进入')
    return
  }
  router.push({ path, query })
}

function goCertificateList(query) {
  navigate(findRoutePath('certificate', ['verify']), query)
}

function goNewCertificate() {
  navigate(findRoutePath('certificate', ['verify']), { action: 'add' })
}

function goVerificationCode() {
  navigate(findRoutePath('verification'))
}

function goOrigin() {
  navigate(findRoutePath('origin'))
}

function fetchStatCount(params) {
  return listCertificate({ pageNum: 1, pageSize: 1, ...params }).then(res => res.total || 0)
}

function loadStats() {
  statsLoading.value = true
  Promise.all([
    fetchStatCount({}),
    fetchStatCount({ status: 2 }),
    fetchStatCount({ status: 1 }),
    fetchStatCount({ status: 3 })
  ])
    .then(([total, activated, pending, deleted]) => {
      stats.value = { total, activated, pending, deleted }
    })
    .catch(() => {})
    .finally(() => {
      statsLoading.value = false
    })
}

onMounted(() => {
  loadStats()
})

onActivated(() => {
  loadStats()
})
</script>

<style scoped lang="scss">
$bg: #f5f6f8;
$text: #333;
$text-muted: #888;
$border: #e8e8e8;

.workbench-page {
  min-height: calc(100vh - 84px);
  padding: 24px;
  background: $bg;
  box-sizing: border-box;
}

.workbench-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
}

.page-title {
  margin: 0 0 8px;
  font-size: 28px;
  font-weight: 700;
  color: $text;
  line-height: 1.2;
}

.page-subtitle {
  margin: 0;
  font-size: 14px;
  color: $text-muted;
}

.stat-row {
  margin-bottom: 28px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px 18px;
  background: #fff;
  border: 1px solid $border;
  border-radius: 4px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: box-shadow 0.2s, border-color 0.2s;

  &:hover {
    border-color: #d9d9d9;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  &.icon-default {
    background: #f5f5f5;
    color: #666;
  }

  &.icon-success {
    background: #f0f9eb;
    color: #67c23a;
  }

  &.icon-warning {
    background: #fdf6ec;
    color: #e6a23c;
  }

  &.icon-muted {
    background: #f5f5f5;
    color: #909399;
  }
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: $text;
  line-height: 1.2;
}

.stat-label {
  margin-top: 4px;
  font-size: 13px;
  color: $text-muted;
}

.quick-section {
  background: #fff;
  border: 1px solid $border;
  border-radius: 4px;
  padding: 20px 24px 24px;
}

.section-title {
  margin: 0 0 16px;
  font-size: 16px;
  font-weight: 600;
  color: $text;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.quick-btn {
  width: 100%;
  height: 48px;
  margin: 0;
  font-size: 14px;
}

@media (max-width: 992px) {
  .quick-actions {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 576px) {
  .workbench-header {
    flex-direction: column;
    align-items: stretch;
  }

  .quick-actions {
    grid-template-columns: 1fr;
  }
}
</style>
