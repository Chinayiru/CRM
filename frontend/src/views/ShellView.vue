<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api, clearSession, currentUser, isDemoSession, verifyDemoOffline } from '../api'

const route = useRoute()
const router = useRouter()
const mobileOpen = ref(false)
const user = computed(() => currentUser())
const demoMode = ref(isDemoSession())
const menus = [
  { title: '工作台', path: '/dashboard', icon: 'DataLine', group: '工作空间' },
  { title: '线索管理', path: '/leads', icon: 'Promotion', group: '客户经营' },
  { title: '客户管理', path: '/customers', icon: 'Avatar', group: '客户经营' },
  { title: '商机管理', path: '/opportunities', icon: 'TrendCharts', group: '客户经营' },
  { title: '合同管理', path: '/contracts', icon: 'Document', group: '客户经营' },
  { title: '系统管理', path: '/system', icon: 'Setting', group: '系统设置' },
]
const topMenus = [
  { title: '工作台', path: '/dashboard' },
  { title: '客户经营', path: '/leads' },
  { title: '业务协同', path: '/opportunities' },
  { title: '系统管理', path: '/system' },
]
const activeTopMenu = computed(() => {
  if (route.path === '/dashboard') return '/dashboard'
  if (route.path === '/system') return '/system'
  if (route.path === '/opportunities' || route.path === '/contracts') return '/opportunities'
  return '/leads'
})
const title = computed(() => String(route.meta.title || '工作台'))
let demoMonitor: number | undefined
function handleUserCommand(command: string) { if (command === 'logout') void logout() }

onMounted(() => {
  if (demoMode.value) {
    demoMonitor = window.setInterval(async () => {
      if (!await verifyDemoOffline()) {
        ElMessage.warning('检测到后端已连接，离线演示账号已退出。请使用后端账号登录。')
        await router.replace('/login')
      }
    }, 5000)
  }
})
onBeforeUnmount(() => { if (demoMonitor !== undefined) window.clearInterval(demoMonitor) })

async function logout() {
  try { await ElMessageBox.confirm('确定退出当前账号吗？', '退出登录', { type: 'warning' }) }
  catch { return }
  try { await api.logout() } catch { /* token may already have expired */ }
  clearSession()
  ElMessage.success('已退出登录')
  await router.replace('/login')
}
</script>

<template>
  <div class="app-shell">
    <header class="topbar">
      <button class="small-screen-menu icon-button" aria-label="打开菜单" @click="mobileOpen = !mobileOpen"><el-icon><Fold /></el-icon></button>
      <RouterLink to="/dashboard" class="brand"><span class="brand-mark"><el-icon><Opportunity /></el-icon></span><span>CRM 管理系统</span></RouterLink>
      <nav class="top-menu" aria-label="主导航">
        <RouterLink v-for="item in topMenus" :key="item.path" :to="item.path"
          :class="{ selected: activeTopMenu === item.path }">{{ item.title }}</RouterLink>
      </nav>
      <el-tag v-if="demoMode" type="warning" effect="plain" size="small">前端离线演示</el-tag>
      <div class="top-actions">
        <el-tooltip content="通知中心暂未接入"><el-button text circle><el-badge is-dot><el-icon><Bell /></el-icon></el-badge></el-button></el-tooltip>
        <el-dropdown @command="handleUserCommand">
          <span class="user-trigger"><el-avatar :size="31">{{ (user?.nickname || user?.username || 'U').slice(0,1).toUpperCase() }}</el-avatar><span>{{ user?.nickname || user?.username || '用户' }}</span><el-icon><ArrowDown /></el-icon></span>
          <template #dropdown><el-dropdown-menu><el-dropdown-item command="logout">退出登录</el-dropdown-item></el-dropdown-menu></template>
        </el-dropdown>
      </div>
    </header>
    <div class="shell-body">
      <aside class="sidebar" :class="{ open: mobileOpen }">
        <template v-for="group in [...new Set(menus.map(m => m.group))]" :key="group">
          <div class="nav-group-label">{{ group }}</div>
          <RouterLink v-for="item in menus.filter(m => m.group === group)" :key="item.path" :to="item.path"
            class="nav-item" :class="{ active: route.path === item.path }" @click="mobileOpen = false">
            <el-icon><component :is="item.icon" /></el-icon><span>{{ item.title }}</span>
          </RouterLink>
        </template>
        <div class="sidebar-foot"><span class="online-dot"></span> 系统运行正常</div>
      </aside>
      <main class="main-content"><RouterView /></main>
    </div>
    <div v-if="mobileOpen" class="mobile-scrim" @click="mobileOpen = false"></div>
  </div>
</template>

<style scoped>
.icon-button{border:0;background:none;color:#647186;font-size:19px;padding:5px;margin-right:9px;cursor:pointer}
.user-trigger{display:flex;align-items:center;gap:8px;color:#4c596c;font-size:12px;cursor:pointer}
.sidebar-foot{position:sticky;top:calc(100vh - 58px);margin:36px 10px 0;padding:13px 5px 0;border-top:1px solid #f0f2f6;color:#9aa4b2;font-size:10px}
.online-dot{display:inline-block;width:6px;height:6px;border-radius:50%;background:#25b77a;margin-right:6px;vertical-align:1px}
.mobile-scrim{display:none}
@media(max-width:720px){.mobile-scrim{display:block;position:fixed;inset:58px 0 0;background:#14223844;z-index:15}}
</style>
