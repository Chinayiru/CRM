<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { api } from '../api'
import type { CrmRecord } from '../types'

// Dashboard aggregation endpoints are not available yet; cards, funnel, and trend are clearly marked demo data.
const demoStats = [
  { label: '本月新增线索', value: '86', change: '+12.4%', icon: 'Promotion' },
  { label: '本月新增客户', value: '52', change: '+8.8%', icon: 'Avatar' },
  { label: '进行中商机', value: '19', change: '+3.1%', icon: 'TrendCharts' },
  { label: '本月成交金额', value: '¥328,500', change: '+21.5%', icon: 'Money' },
]
const funnel = [
  { name: '初步接触', value: 40, width: 100 },
  { name: '需求确认', value: 28, width: 70 },
  { name: '方案报价', value: 16, width: 48 },
  { name: '商务谈判', value: 9, width: 30 },
  { name: '赢单', value: 5, width: 17 },
]
const months = ['5月','6月','7月','8月','9月','10月']
const followRecords = ref<CrmRecord[]>([])
const followError = ref('')
const demoFollow: CrmRecord[] = [
  { id: 1, contact: '张经理', customerName: '成都华星科技有限公司', content: '确认产品演示时间', createdAt: '今天 10:30', ownerName: '李明' },
  { id: 2, contact: '王总', customerName: '蓝海数字服务有限公司', content: '已发送方案报价', createdAt: '今天 09:20', ownerName: '陈晨' },
  { id: 3, contact: '赵女士', customerName: '北辰智能设备有限公司', content: '跟进采购计划', createdAt: '昨天 16:40', ownerName: '王芳' },
]

onMounted(async () => {
  try { followRecords.value = (await api.list('/api/follow-records')).slice(0, 5) }
  catch { followError.value = '跟进记录接口暂不可用，当前展示演示数据'; followRecords.value = demoFollow }
})
</script>

<template>
  <div class="page-heading"><div><h1>工作台</h1><p>欢迎回来，今天也一起推进客户进展</p></div><el-tag type="info" effect="plain" round>数据概览 · 演示口径</el-tag></div>
  <div class="stat-grid">
    <div v-for="item in demoStats" :key="item.label" class="surface stat-card">
      <div class="stat-label">{{ item.label }}</div><div class="stat-number">{{ item.value }}</div>
      <div class="stat-foot"><strong>{{ item.change }}</strong>较上月</div><span class="stat-icon"><el-icon><component :is="item.icon" /></el-icon></span>
    </div>
  </div>
  <div class="dashboard-grid">
    <section class="surface panel">
      <div class="panel-title">销售漏斗 <span class="demo-label">演示数据</span></div><div class="panel-subtitle">按商机阶段查看当前分布</div>
      <div class="funnel-list"><div v-for="item in funnel" :key="item.name" class="funnel-row"><span>{{ item.name }}</span><div class="funnel-track"><div class="funnel-fill" :style="{ width: `${item.width}%` }"></div></div><b>{{ item.value }}</b></div></div>
    </section>
    <section class="surface panel">
      <div class="panel-title">近六月成交金额趋势 <span class="demo-label">演示数据</span></div><div class="panel-subtitle">按月份汇总合同金额（万元）</div>
      <div class="chart-area"><svg viewBox="0 0 500 140" preserveAspectRatio="none" aria-label="成交金额趋势演示图"><defs><linearGradient id="trendFill" x1="0" x2="0" y1="0" y2="1"><stop offset="0%" stop-color="#3388ed" stop-opacity=".19"/><stop offset="100%" stop-color="#3388ed" stop-opacity="0"/></linearGradient></defs><path d="M0,116 C40,108 45,91 90,97 S142,74 180,80 S231,60 267,69 S318,44 355,54 S410,24 445,37 S480,18 500,12 L500,140 L0,140Z" fill="url(#trendFill)"/><path d="M0,116 C40,108 45,91 90,97 S142,74 180,80 S231,60 267,69 S318,44 355,54 S410,24 445,37 S480,18 500,12" fill="none" stroke="#3388ed" stroke-width="3" vector-effect="non-scaling-stroke"/></svg></div>
      <div class="month-labels"><span v-for="month in months" :key="month">{{ month }}</span></div>
    </section>
  </div>
  <section class="surface panel">
    <div class="panel-title">最近跟进 <span class="demo-label">{{ followError ? '演示数据' : '来自后端接口' }}</span></div><div class="panel-subtitle">最近更新的客户沟通记录</div>
    <div v-if="followRecords.length" class="follow-list">
      <div v-for="(item,index) in followRecords" :key="item.id || index" class="follow-row">
        <el-avatar :size="27">{{ String(item.contact || item.ownerName || '客').slice(0,1) }}</el-avatar>
        <span class="follow-name">{{ item.customerName || item.customerId || '客户记录' }}</span><span>{{ item.content || item.remark || '已更新跟进' }}</span><span class="follow-date">{{ item.createdAt || item.nextTime || '—' }}</span>
      </div>
    </div><div v-else class="empty-note">暂无跟进记录</div>
  </section>
</template>

<style scoped>
.demo-label{font-size:10px;color:#a17a31;background:#fff7e5;border-radius:4px;padding:3px 6px;margin-left:7px;font-weight:400;vertical-align:1px}
</style>
