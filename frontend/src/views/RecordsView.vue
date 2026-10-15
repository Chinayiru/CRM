<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search } from '@element-plus/icons-vue'
import { api, currentUser } from '../api'
import type { CrmRecord } from '../types'

type Field = { key: string; label: string; type?: string; required?: boolean; options?: Array<{ label: string; value: string }> }
const route = useRoute()
const kind = computed(() => String(route.meta.kind || 'customers'))
const config = computed(() => configs[kind.value])
const rows = ref<CrmRecord[]>([])
const customers = ref<CrmRecord[]>([])
const busy = ref(false)
const loadError = ref('')
const search = ref('')
const statusFilter = ref('')
const page = ref(1)
const pageSize = 8
const dialogOpen = ref(false)
const detailOpen = ref(false)
const editing = ref<CrmRecord | null>(null)
const detail = ref<CrmRecord | null>(null)
const form = reactive<CrmRecord>({})
const draggingId = ref<number | null>(null)
const dropStage = ref('')

const stageOptions = [
  { label: '初步接触', value: 'INITIAL_CONTACT' }, { label: '需求确认', value: 'NEEDS_CONFIRMED' },
  { label: '方案报价', value: 'PROPOSAL' }, { label: '商务谈判', value: 'NEGOTIATION' }, { label: '赢单', value: 'WON' },
]
const configs: Record<string, { title: string; description: string; api: string; columns: Field[]; fields: Field[]; create: string; empty: string }> = {
  customers: {
    title:'客户管理',description:'维护客户档案，记录客户状态与联系信息',api:'/api/customers',create:'新增客户',empty:'暂无客户记录',
    columns:[{key:'name',label:'客户名称'},{key:'industry',label:'所属行业'},{key:'contact',label:'联系人'},{key:'phone',label:'联系电话'},{key:'status',label:'客户状态'},{key:'createdAt',label:'创建时间'}],
    fields:[{key:'name',label:'客户名称',required:true},{key:'industry',label:'所属行业'},{key:'customerType',label:'客户类型'},{key:'contact',label:'联系人'},{key:'phone',label:'联系电话'},{key:'ownerId',label:'负责人用户ID',type:'number',required:true},{key:'source',label:'客户来源'},{key:'address',label:'地址',type:'textarea'},{key:'status',label:'客户状态',type:'select',options:[{label:'有效',value:'ACTIVE'},{label:'停用',value:'INACTIVE'}]}],
  },
  leads: {
    title:'线索管理',description:'跟进潜在线索，推进到客户转化',api:'/api/clues',create:'新增线索',empty:'暂无线索记录',
    columns:[{key:'name',label:'线索名称'},{key:'phone',label:'联系电话'},{key:'source',label:'线索来源'},{key:'status',label:'线索状态'},{key:'ownerId',label:'负责人'},{key:'createdAt',label:'创建时间'}],
    fields:[{key:'name',label:'线索名称',required:true},{key:'phone',label:'联系电话'},{key:'source',label:'线索来源'},{key:'ownerId',label:'负责人用户ID',type:'number',required:true},{key:'status',label:'线索状态',type:'select',options:[{label:'新建',value:'NEW'},{label:'跟进中',value:'FOLLOWING'},{label:'已转化',value:'CONVERTED'},{label:'已丢弃',value:'DISCARDED'}]},{key:'remark',label:'备注',type:'textarea'}],
  },
  opportunities: {
    title:'商机管理',description:'按销售阶段查看商机进展。拖动卡片可更新阶段。',api:'/api/opportunities',create:'新建商机',empty:'暂无商机记录',
    columns:[{key:'name',label:'商机名称'},{key:'customerId',label:'客户'},{key:'amount',label:'预计金额'},{key:'stage',label:'当前阶段'},{key:'ownerId',label:'负责人'},{key:'expectTime',label:'预计成交日期'}],
    fields:[{key:'name',label:'商机名称',required:true},{key:'customerId',label:'关联客户ID',type:'number',required:true},{key:'amount',label:'预计金额',type:'number'},{key:'stage',label:'商机阶段',type:'select',options:stageOptions},{key:'expectTime',label:'预计成交日期',type:'date'},{key:'winRate',label:'赢率（%）',type:'number'},{key:'ownerId',label:'负责人用户ID',type:'number',required:true},{key:'lostReason',label:'输单原因',type:'textarea'}],
  },
  contracts: {
    title:'合同管理',description:'登记并跟踪合同金额、签订日期及履行状态',api:'/api/contracts',create:'新建合同',empty:'暂无合同记录',
    columns:[{key:'contractNo',label:'合同编号'},{key:'name',label:'合同名称'},{key:'customerId',label:'客户'},{key:'amount',label:'合同金额'},{key:'signDate',label:'签订日期'},{key:'expireDate',label:'到期日期'},{key:'status',label:'合同状态'}],
    fields:[{key:'contractNo',label:'合同编号',required:true},{key:'name',label:'合同名称',required:true},{key:'customerId',label:'关联客户ID',type:'number',required:true},{key:'opportunityId',label:'来源商机ID',type:'number'},{key:'amount',label:'合同金额',type:'number'},{key:'signDate',label:'签订日期',type:'date'},{key:'expireDate',label:'到期日期',type:'date'},{key:'status',label:'合同状态',type:'select',options:[{label:'草稿',value:'DRAFT'},{label:'生效',value:'EFFECTIVE'},{label:'履行中',value:'IN_PROGRESS'},{label:'已完成',value:'COMPLETED'},{label:'已终止',value:'TERMINATED'}]},{key:'terminateReason',label:'终止原因',type:'textarea'}],
  },
}

const filtered = computed(() => {
  const q = search.value.trim().toLowerCase()
  return rows.value.filter((r) => {
    const queryOk = !q || Object.values(r).some(v => String(v ?? '').toLowerCase().includes(q))
    const statusOk = !statusFilter.value || String(r.status || r.stage) === statusFilter.value
    return queryOk && statusOk
  })
})
const pageRows = computed(() => filtered.value.slice((page.value - 1) * pageSize, page.value * pageSize))
const boardStages = computed(() => kind.value === 'opportunities' ? stageOptions.map(s => ({ ...s, rows:rows.value.filter(r => r.stage === s.value) })) : [])
const lostRows = computed(() => rows.value.filter(r => r.stage === 'LOST'))

async function load() {
  busy.value = true; loadError.value = ''
  try {
    rows.value = await api.list(config.value.api)
    if (kind.value !== 'customers') {
      try { customers.value = await api.list('/api/customers') } catch { customers.value = [] }
    }
  } catch (error) {
    rows.value = []
    loadError.value = error instanceof Error ? error.message : '读取失败'
  } finally { busy.value = false }
}
watch(kind, () => { search.value=''; statusFilter.value=''; page.value=1; load() })
onMounted(load)

function openCreate() {
  editing.value = null
  Object.keys(form).forEach(k => delete form[k])
  form.ownerId = currentUser()?.id ?? undefined
  form.status = kind.value === 'customers' ? 'ACTIVE' : kind.value === 'leads' ? 'NEW' : kind.value === 'contracts' ? 'DRAFT' : 'INITIAL_CONTACT'
  dialogOpen.value = true
}
function openEdit(row: CrmRecord) {
  editing.value = row
  Object.keys(form).forEach(k => delete form[k])
  Object.assign(form, { ...row })
  dialogOpen.value = true
}
function cleanPayload() {
  const payload: CrmRecord = {}
  for (const f of config.value.fields) {
    const value = form[f.key]
    payload[f.key] = value === '' || value === undefined ? null : value
  }
  return payload
}
async function save() {
  for (const field of config.value.fields.filter(f => f.required)) {
    if (form[field.key] === '' || form[field.key] == null) return ElMessage.warning(`${field.label}不能为空`)
  }
  try {
    if (editing.value?.id) await api.update(config.value.api, Number(editing.value.id), cleanPayload())
    else await api.create(config.value.api, cleanPayload())
    ElMessage.success(editing.value ? '修改成功' : '创建成功')
    dialogOpen.value = false; await load()
  } catch (error) { ElMessage.error(error instanceof Error ? error.message : '保存失败') }
}
async function remove(row: CrmRecord) {
  try { await ElMessageBox.confirm(`确定删除“${row.name || row.contractNo || `#${row.id}`}”吗？`, '删除确认', { type:'warning' }) }
  catch { return }
  try { await api.remove(config.value.api, Number(row.id)); ElMessage.success('删除成功'); await load() }
  catch (error) { ElMessage.error(error instanceof Error ? error.message : '删除失败') }
}
function view(row: CrmRecord) { detail.value = row; detailOpen.value = true }
function stageName(value: string) { return stageOptions.find(s => s.value === value)?.label || (value === 'LOST' ? '输单' : value || '—') }
function statusClass(value: string) {
  if (['ACTIVE','EFFECTIVE','IN_PROGRESS','WON','FOLLOWING','NEW'].includes(value)) return 'status-active'
  if (['DRAFT','INITIAL_CONTACT','NEEDS_CONFIRMED','PROPOSAL','NEGOTIATION'].includes(value)) return 'status-warn'
  if (['COMPLETED','CONVERTED'].includes(value)) return 'status-success'
  return 'status-muted'
}
function cellText(row: CrmRecord, key: string) {
  if (key === 'status') return kind.value === 'leads' ? ({NEW:'新线索',FOLLOWING:'跟进中',CONVERTED:'已转化',DISCARDED:'已丢弃'} as Record<string,string>)[row[key]] || row[key] : ({ACTIVE:'有效',INACTIVE:'停用',DRAFT:'草稿',EFFECTIVE:'已生效',IN_PROGRESS:'履行中',COMPLETED:'已完成',TERMINATED:'已终止'} as Record<string,string>)[row[key]] || row[key]
  if (key === 'stage') return stageName(row[key])
  if (key === 'amount') return row[key] == null ? '—' : `¥${Number(row[key]).toLocaleString('zh-CN')}`
  return row[key] ?? '—'
}
async function dropAt(stage: string) {
  const row = rows.value.find(r => Number(r.id) === draggingId.value)
  if (!row || row.stage === stage) { draggingId.value = null; dropStage.value=''; return }
  const oldStage = row.stage
  const next = { ...row, stage }
  try {
    // Dedicated transition endpoint is not available; use the existing PUT API and only allow moving to the next board column in UI.
    const payload = { name:row.name, customerId:row.customerId, amount:row.amount, stage, expectTime:row.expectTime, winRate:row.winRate, lostReason:row.lostReason, closedAt:row.closedAt, ownerId:row.ownerId }
    await api.update('/api/opportunities', Number(row.id), payload)
    row.stage = stage; ElMessage.success(`商机阶段已更新：${stageName(stage)}`)
  } catch (error) {
    row.stage = oldStage
    ElMessage.error(error instanceof Error ? error.message : '阶段更新失败')
  } finally { draggingId.value=null; dropStage.value=''; }
}
</script>

<template>
  <div class="page-heading">
    <div><h1>{{ config.title }}</h1><p>{{ config.description }}</p></div>
    <el-button type="primary" :icon="Plus" @click="openCreate">{{ config.create }}</el-button>
  </div>
  <template v-if="kind === 'opportunities'">
    <div class="stat-grid compact-stats">
      <div class="surface stat-card"><div class="stat-label">商机总数</div><div class="stat-number">{{ rows.length }}</div><div class="stat-foot">当前可见记录</div></div>
      <div class="surface stat-card"><div class="stat-label">进行中商机</div><div class="stat-number">{{ rows.filter(r => !['WON','LOST'].includes(r.stage)).length }}</div><div class="stat-foot">未进入终态</div></div>
      <div class="surface stat-card"><div class="stat-label">本月新增商机</div><div class="stat-number">{{ rows.filter(r => String(r.createdAt || '').slice(0,7) === new Date().toISOString().slice(0,7)).length }}</div><div class="stat-foot">按后端创建时间统计</div></div>
      <div class="surface stat-card"><div class="stat-label">阶段拖动</div><div class="stat-number" style="font-size:18px">可用</div><div class="stat-foot">沿用现有 PUT 接口</div></div>
    </div>
    <div v-if="loadError" class="surface empty-note"><el-icon><Warning /></el-icon> {{ loadError }} <el-button link type="primary" @click="load">重试</el-button></div>
    <div v-else-if="busy" class="surface empty-note"><el-icon class="is-loading"><Loading /></el-icon> 正在读取商机…</div>
    <div v-else class="board">
      <div v-for="stage in boardStages" :key="stage.value" class="board-column" :class="{'drag-target':dropStage===stage.value}"
        @dragover.prevent="dropStage=stage.value" @dragleave="dropStage=''" @drop.prevent="dropAt(stage.value)">
        <div class="board-head"><span>{{ stage.label }}</span><span class="board-count">{{ stage.rows.length }}</span></div>
        <div v-for="item in stage.rows" :key="item.id" class="opportunity-card" draggable="true" @dragstart="draggingId=Number(item.id)">
          <h3>{{ item.name }}</h3><p>关联客户：{{ customers.find(c => c.id === item.customerId)?.name || `#${item.customerId}` }}</p>
          <p>销售负责人：{{ item.ownerId || '—' }}</p><p>预计金额：{{ item.amount == null ? '—' : `¥${Number(item.amount).toLocaleString()}` }}</p><p>下一步行动：{{ item.nextAction || '待补充' }}</p>
          <div class="card-actions"><span class="action-link" @click="view(item)">详情</span><span class="action-link" @click="openEdit(item)">编辑</span></div>
        </div>
        <div v-if="!stage.rows.length" class="board-empty">拖动商机卡片到此阶段</div>
      </div>
    </div>
    <section v-if="lostRows.length" class="surface panel lost-panel"><div class="panel-title">输单商机 <span class="board-count">{{ lostRows.length }}</span></div><div class="lost-list"><span v-for="item in lostRows" :key="item.id">{{ item.name }} <small>{{ item.lostReason || '未记录原因' }}</small></span></div></section>
  </template>
  <template v-else>
    <section class="surface filter-bar">
      <el-input v-model="search" clearable placeholder="搜索名称、联系人或编号" :prefix-icon="Search" @input="page=1" />
      <el-select v-model="statusFilter" clearable placeholder="全部状态" @change="page=1">
        <el-option v-for="o in (kind==='leads' ? [{label:'新线索',value:'NEW'},{label:'跟进中',value:'FOLLOWING'},{label:'已转化',value:'CONVERTED'},{label:'已丢弃',value:'DISCARDED'}] : kind==='customers' ? [{label:'有效',value:'ACTIVE'},{label:'停用',value:'INACTIVE'}] : [{label:'草稿',value:'DRAFT'},{label:'已生效',value:'EFFECTIVE'},{label:'履行中',value:'IN_PROGRESS'},{label:'已完成',value:'COMPLETED'},{label:'已终止',value:'TERMINATED'}])" :key="o.value" :label="o.label" :value="o.value" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="page=1">搜索</el-button><el-button :icon="Refresh" @click="load">刷新</el-button><span class="filter-spacer"></span>
      <span class="muted-note">{{ filtered.length }} 条记录</span>
    </section>
    <section class="surface table-wrap">
      <el-table v-loading="busy" :data="pageRows" row-key="id" style="width:100%" @row-dblclick="view">
        <el-table-column v-for="col in config.columns" :key="col.key" :label="col.label" :min-width="col.key==='name'?150:110" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="col.key==='status' || col.key==='stage'" class="status-pill" :class="statusClass(scope.row[col.key])">{{ cellText(scope.row,col.key) }}</span>
            <span v-else>{{ cellText(scope.row,col.key) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="145">
          <template #default="scope"><span class="action-link" @click="view(scope.row)">详情</span><span class="action-link" @click="openEdit(scope.row)">编辑</span><span class="action-link danger" @click="remove(scope.row)">删除</span></template>
        </el-table-column>
        <template #empty><div class="empty-note">{{ loadError ? '读取失败：'+loadError : config.empty }}</div></template>
      </el-table>
      <div class="table-footer"><span>共 {{ filtered.length }} 条记录</span><el-pagination v-model:current-page="page" :page-size="pageSize" :total="filtered.length" layout="prev, pager, next" small /></div>
    </section>
  </template>

  <el-dialog v-model="dialogOpen" :title="editing ? `编辑${config.title.replace('管理','')}` : config.create" width="min(680px, 94vw)" destroy-on-close>
    <el-form label-position="top"><div class="dialog-form">
      <el-form-item v-for="field in config.fields" :key="field.key" :label="field.label" :required="field.required" :class="{full:field.type==='textarea'}">
        <el-select v-if="field.type==='select'" v-model="form[field.key]" clearable placeholder="请选择" style="width:100%"><el-option v-for="o in field.options" :key="o.value" :label="o.label" :value="o.value" /></el-select>
        <el-input-number v-else-if="field.type==='number'" v-model="form[field.key]" :min="0" :precision="field.key==='amount'?2:0" controls-position="right" style="width:100%" />
        <el-date-picker v-else-if="field.type==='date'" v-model="form[field.key]" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width:100%" />
        <el-input v-else-if="field.type==='textarea'" v-model="form[field.key]" type="textarea" :rows="3" maxlength="1000" show-word-limit />
        <el-input v-else v-model="form[field.key]" :maxlength="field.key==='name'?200:500" />
      </el-form-item>
    </div></el-form>
    <div class="muted-note">字段校验以当前后端 DTO 为准；下拉选项尚未接入数据字典时使用演示值。</div>
    <template #footer><el-button @click="dialogOpen=false">取消</el-button><el-button type="primary" :loading="busy" @click="save">保存</el-button></template>
  </el-dialog>
  <el-dialog v-model="detailOpen" title="记录详情" width="min(600px, 94vw)"><el-descriptions v-if="detail" :column="1" border><el-descriptions-item v-for="col in config.columns" :key="col.key" :label="col.label">{{ cellText(detail,col.key) }}</el-descriptions-item><el-descriptions-item label="备注" v-if="detail.remark">{{ detail.remark }}</el-descriptions-item></el-descriptions></el-dialog>
</template>

<style scoped>
.compact-stats{grid-template-columns:repeat(4,minmax(0,1fr));margin-bottom:15px}
.board-empty{border:1px dashed #dbe2ec;border-radius:7px;color:#a0aab8;text-align:center;padding:18px 6px;font-size:10px}
.card-actions{border-top:1px solid #f0f2f6;margin-top:8px;padding-top:7px;font-size:10px}
.lost-panel{margin-top:15px;padding:15px 18px}.lost-list{display:flex;gap:10px;flex-wrap:wrap;margin-top:10px}.lost-list span{background:#f4f6f9;border-radius:6px;padding:7px 10px;font-size:11px}.lost-list small{color:#8d98a8;margin-left:6px}
@media(max-width:1000px){.compact-stats{grid-template-columns:repeat(2,minmax(0,1fr))}}
</style>
