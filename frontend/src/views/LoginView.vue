<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api, saveSession, setRememberMe } from '../api'
import { DEMO_PASSWORD, DEMO_USERNAME } from '../demo'

const router = useRouter()
const mode = ref<'login' | 'register'>('login')
const loading = ref(false)
const form = reactive({ username: '', password: '', captcha: '', nickname: '', phone: '', email: '', remember: localStorage.getItem('crm_remember') === '1' })
const demoCaptcha = ref(makeCaptcha())
const isLogin = computed(() => mode.value === 'login')

function makeCaptcha() {
  return Math.random().toString(36).slice(2, 6).toUpperCase()
}
function refreshCaptcha() { demoCaptcha.value = makeCaptcha(); form.captcha = '' }

async function submit() {
  if (!form.username.trim() || !form.password) return ElMessage.warning('请输入用户名和密码')
  if (isLogin.value && form.username.trim() !== DEMO_USERNAME && form.captcha.toUpperCase() !== demoCaptcha.value) {
    refreshCaptcha()
    return ElMessage.error('演示验证码不正确，请重试')
  }
  loading.value = true
  try {
    if (isLogin.value) {
      const result = await api.login(form.username.trim(), form.password)
      setRememberMe(form.remember)
      saveSession(result)
      ElMessage.success('登录成功')
      await router.replace('/dashboard')
    } else {
      await api.register({ username: form.username.trim(), password: form.password,
        nickname: form.nickname, phone: form.phone, email: form.email })
      ElMessage.success('注册申请已提交，请等待管理员审核')
      mode.value = 'login'
      Object.assign(form, { password: '', captcha: '' })
      refreshCaptcha()
    }
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '请求失败，请稍后重试')
  } finally { loading.value = false }
}
</script>

<template>
  <div class="login-page">
    <div class="login-brand">
      <div class="brand-mark"><el-icon><Opportunity /></el-icon></div>
      <h1>CRM 管理系统</h1><p>客户关系管理平台</p>
    </div>
    <el-card class="login-card" shadow="never">
      <h2>{{ isLogin ? '欢迎回来' : '创建账号' }}</h2>
      <p class="intro">{{ isLogin ? '登录以继续管理您的客户关系' : '提交后由管理员审核，通过后即可登录' }}</p>
      <el-form label-position="top" @submit.prevent="submit">
        <el-form-item label="账号">
          <el-input v-model="form.username" maxlength="64" placeholder="请输入用户名" @keyup.enter="submit">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password maxlength="128" placeholder="请输入密码" @keyup.enter="submit">
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <template v-if="!isLogin">
          <el-form-item label="姓名（选填）"><el-input v-model="form.nickname" maxlength="100" placeholder="请输入姓名" /></el-form-item>
          <div class="dialog-form">
            <el-form-item label="联系电话"><el-input v-model="form.phone" maxlength="32" placeholder="选填" /></el-form-item>
            <el-form-item label="邮箱"><el-input v-model="form.email" maxlength="254" placeholder="选填" /></el-form-item>
          </div>
        </template>
        <el-form-item v-if="isLogin && form.username.trim() !== DEMO_USERNAME" label="验证码">
          <div class="login-code">
            <el-input v-model="form.captcha" maxlength="4" placeholder="输入右侧验证码" @keyup.enter="submit" />
            <button class="code-chip" type="button" title="刷新验证码" @click="refreshCaptcha">{{ demoCaptcha }}</button>
          </div>
          <div class="muted-note" style="margin-top:6px">当前为前端演示验证码，后端验证码接口尚未提供。</div>
        </el-form-item>
        <div v-if="isLogin" class="login-options">
          <el-checkbox v-model="form.remember">记住我</el-checkbox>
          <el-link type="primary" :underline="false" @click="ElMessage.info('找回密码功能待后端接口支持')">忘记密码？</el-link>
        </div>
        <el-button type="primary" class="login-submit" :loading="loading" @click="submit">{{ isLogin ? '登 录' : '提交注册申请' }}</el-button>
        <div class="login-switch">
          <span>{{ isLogin ? '还没有账号？' : '已有账号？' }}</span>
          <el-link type="primary" :underline="false" @click="mode = isLogin ? 'register' : 'login'">{{ isLogin ? '申请注册' : '返回登录' }}</el-link>
        </div>
      </el-form>
      <div v-if="!isLogin" class="demo-note">注册账号需管理员审批。审核通过前，账号不能登录。</div>
      <div v-else class="demo-note">离线演示账号（仅后端不可达时有效）：用户名 <b>{{ DEMO_USERNAME }}</b>，密码 <b>{{ DEMO_PASSWORD }}</b>。后端在线时此账号会被拒绝。</div>
    </el-card>
    <div class="login-footer">© 2026 Cordys CRM · 客户关系管理平台</div>
  </div>
</template>

<style scoped>
.login-options{display:flex;align-items:center;justify-content:space-between;margin:-1px 0 18px;font-size:12px}
.login-submit{width:100%;height:42px;font-weight:600;letter-spacing:1px}
.login-switch{display:flex;justify-content:center;gap:6px;margin-top:17px;color:#929dad;font-size:12px}
.login-card :deep(.el-form-item){margin-bottom:15px}
.login-card :deep(.el-form-item__label){font-size:12px;color:#56647a;padding-bottom:5px}
</style>
