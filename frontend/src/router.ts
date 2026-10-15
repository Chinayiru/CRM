import { createRouter, createWebHistory } from 'vue-router'
import LoginView from './views/LoginView.vue'
import ShellView from './views/ShellView.vue'
import { getToken } from './api'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: LoginView, meta: { public: true } },
    {
      path: '/', component: ShellView,
      children: [
        { path: '', redirect: '/dashboard' },
        { path: 'dashboard', component: () => import('./views/DashboardView.vue'), meta: { title: '工作台' } },
        { path: 'customers', component: () => import('./views/RecordsView.vue'), meta: { title: '客户管理', kind: 'customers' } },
        { path: 'leads', component: () => import('./views/RecordsView.vue'), meta: { title: '线索管理', kind: 'leads' } },
        { path: 'opportunities', component: () => import('./views/RecordsView.vue'), meta: { title: '商机管理', kind: 'opportunities' } },
        { path: 'contracts', component: () => import('./views/RecordsView.vue'), meta: { title: '合同管理', kind: 'contracts' } },
        { path: 'system', component: () => import('./views/SystemView.vue'), meta: { title: '系统管理' } },
      ],
    },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
})

router.beforeEach((to) => {
  const token = getToken()
  if (!to.meta.public && !token) return '/login'
  if (to.path === '/login' && token) return '/dashboard'
})

export default router
