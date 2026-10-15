<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search } from '@element-plus/icons-vue'
import { api } from '../api'
import { formatDateTime } from '../display'
import type { CrmRecord } from '../types'

type Section = 'home'|'users'|'roles'|'dicts'|'logs'|'approvals'
const active = ref<Section>('home')
const sections: Array<{key:Section;label:string;icon:string}> = [
  {key:'home',label:'系统总览',icon:'DataLine'},
  {key:'users',label:'用户管理',icon:'User'},{key:'roles',label:'角色权限',icon:'Key'},
  {key:'dicts',label:'数据字典',icon:'Collection'},{key:'logs',label:'操作日志',icon:'Tickets'},
  {key:'approvals',label:'注册审核',icon:'Checked'},
]
/** 系统总览页的功能入口卡片，对应设计稿图6 第一行。 */
const entryCards: Array<{key:Section;title:string;desc:string;icon:string}> = [
  {key:'users',title:'用户管理',desc:'维护账号、部门与启用状态',icon:'User'},
  {key:'roles',title:'角色权限',desc:'配置角色数据范围与功能权限',icon:'Key'},
  {key:'dicts',title:'数据字典',desc:'维护下拉选项与状态标签来源',icon:'Collection'},
]
const urls: Record<Section,string> = { home:'/api/operation-logs',users:'/api/users',roles:'/api/roles',dicts:'/api/dicts',logs:'/api/operation-logs',approvals:'/api/admin/registrations' }
const rows = ref<CrmRecord[]>([])
const menuRows = ref<CrmRecord[]>([])
const roleMenus = ref<CrmRecord[]>([])
const loading = ref(false)
const errorText = ref('')
const query = ref('')
const dialogVisible = ref(false)
const editId = ref<number|null>(null)
const form = reactive<CrmRecord>({})
const selectedMenuIds = ref<number[]>([])
const permissionRoleId = ref<number|null>(null)
/** 用户 ID → 姓名映射，用于把操作日志的 operatorId 显示为姓名。 */
const userNames = ref<Record<string,string>>({})
let userNamesLoaded = false

const titles: Record<Section,string> = { home:'系统管理',users:'用户管理',roles:'角色权限',dicts:'数据字典',logs:'操作日志',approvals:'注册审核'}
const subtitles: Record<Section,string> = {
  home:'系统概览与操作审计',users:'账号数据来自后端接口',roles:'数据范围与功能权限配置',
  dicts:'下拉选项与状态标签的来源数据',logs:'操作日志为只读记录',approvals:'审核通过后用户才能登录',
}
const createLabels: Partial<Record<Section,string>> = { users:'新增用户',roles:'新增角色',dicts:'新增字典' }
const visibleRows = computed(() => {
  const q=query.value.trim().toLowerCase()
  if(!q) return rows.value
  return rows.value.filter(r => Object.values(r).some(v=>String(v??'').toLowerCase().includes(q)))
})
const columns = computed(() => ({
  home:[['operatorId','操作人'],['module','模块'],['action','操作'],['targetType','对象类型'],['result','结果'],['ipAddress','IP 地址'],['createdAt','操作时间']],
  users:[['username','用户名'],['nickname','姓名'],['departmentId','部门ID'],['phone','联系电话'],['status','账号状态'],['approvalStatus','审核状态']],
  roles:[['roleName','角色名称'],['roleKey','角色标识'],['dataScope','数据范围'],['status','状态']],
  dicts:[['dictType','字典类型'],['dictLabel','字典名称'],['dictValue','字典值'],['sortNo','排序'],['status','状态']],
  logs:[['operatorId','操作人'],['module','模块'],['action','操作'],['targetType','对象类型'],['result','结果'],['ipAddress','IP 地址'],['createdAt','操作时间']],
  approvals:[['username','用户名'],['nickname','姓名'],['phone','联系电话'],['email','邮箱'],['createdAt','申请时间']],
} as Record<Section,string[][]>)[active.value])

/** 操作日志只存 operatorId；该页仅管理员可见，因此可以读取用户列表还原姓名。 */
async function ensureUserNames() {
  if (userNamesLoaded) return
  try {
    const users = await api.list('/api/users')
    const map: Record<string,string> = {}
    for (const user of users) map[String(user.id)] = user.nickname || user.username || `用户#${user.id}`
    userNames.value = map
    userNamesLoaded = true
  } catch { /* 非管理员或接口不可用时保留“用户#ID”显示 */ }
}
function operatorLabel(id: unknown) {
  if (id === null || id === undefined || id === '') return '—'
  return userNames.value[String(id)] || `用户#${id}`
}
async function load() {
  loading.value=true; errorText.value=''
  try {
    rows.value=await api.list(urls[active.value])
    if (active.value==='home' || active.value==='logs') void ensureUserNames()
  }
  catch(e) { rows.value=[];errorText.value=e instanceof Error?e.message:'读取失败' }
  finally { loading.value=false }
}
watch(active,()=>{query.value='';load()})
onMounted(load)

function beginCreate() {
  editId.value=null;Object.keys(form).forEach(k=>delete form[k])
  if(active.value==='users') form.status='ENABLED'
  if(active.value==='roles'){form.status='ENABLED';form.dataScope='SELF'}
  if(active.value==='dicts'){form.status='ENABLED';form.sortNo=0}
  dialogVisible.value=true
}
function beginEdit(row:CrmRecord) { editId.value=Number(row.id);Object.keys(form).forEach(k=>delete form[k]);Object.assign(form,{...row,password:''});dialogVisible.value=true }
async function save() {
  const payload={...form}
  if(active.value==='users'&&editId.value&& !payload.password) delete payload.password
  try {
    if(editId.value) await api.update(urls[active.value],editId.value,payload)
    else await api.create(urls[active.value],payload)
    ElMessage.success('保存成功');dialogVisible.value=false;await load()
  } catch(e){ElMessage.error(e instanceof Error?e.message:'保存失败')}
}
async function remove(row:CrmRecord) {
  try{await ElMessageBox.confirm('删除后将按逻辑删除处理，是否继续？','删除确认',{type:'warning'})}catch{return}
  try{await api.remove(urls[active.value],Number(row.id));ElMessage.success('已删除');await load()}
  catch(e){ElMessage.error(e instanceof Error?e.message:'删除失败')}
}
async function review(row:CrmRecord,approve:boolean) {
  const action=approve?'通过':'拒绝'
  try{await ElMessageBox.confirm(`确认${action}用户 ${row.username} 的注册申请？`,'注册审核',{type:approve?'success':'warning'})}catch{return}
  try{if(approve)await api.approve(Number(row.id));else await api.reject(Number(row.id));ElMessage.success(`已${action}`);await load()}
  catch(e){ElMessage.error(e instanceof Error?e.message:`${action}失败`)}
}
async function openPermissions(role:CrmRecord) {
  permissionRoleId.value=Number(role.id)
  try {
    const [menus,links]=await Promise.all([api.list('/api/menus'),api.list('/api/role-menus')])
    menuRows.value=menus;roleMenus.value=links.filter(x=>Number(x.roleId)===Number(role.id))
    selectedMenuIds.value=roleMenus.value.map(x=>Number(x.menuId))
    dialogVisible.value=true;editId.value=null
  } catch(e){ElMessage.error(e instanceof Error?e.message:'读取权限失败')}
}
async function savePermissions() {
  const old=new Set(roleMenus.value.map(x=>Number(x.menuId))), next=new Set(selectedMenuIds.value)
  try {
    for(const id of next) if(!old.has(id)) await api.create('/api/role-menus',{roleId:permissionRoleId.value,menuId:id})
    for(const link of roleMenus.value) if(!next.has(Number(link.menuId))) await api.remove(`/api/role-menus/${permissionRoleId.value}`,Number(link.menuId))
    ElMessage.success('权限已保存');dialogVisible.value=false;await load()
  } catch(e){ElMessage.error(e instanceof Error?e.message:'权限保存失败')}
}
function valueLabel(value:string) {
  return ({ENABLED:'启用',DISABLED:'停用',PENDING:'待审核',APPROVED:'已通过',REJECTED:'已拒绝',SUCCESS:'成功',FAILURE:'失败',SELF:'本人',DEPT:'本部门',ALL:'全部'} as Record<string,string>)[value]||value||'—'
}
const statusKeys = ['status','approvalStatus','dataScope','result']
function cellValue(row:CrmRecord,key:string) {
  if (key==='operatorId') return operatorLabel(row[key])
  if (key==='createdAt') return formatDateTime(row[key])
  if (statusKeys.includes(key)) return valueLabel(row[key])
  return row[key] ?? '—'
}
/** 字段长度上限与后端数据库列宽一致（sys_user／sys_role／sys_dict）。 */
function fieldsFor(section:Section) {
  const fields:Record<Section,Array<{key:string;label:string;type?:string;maxlength?:number;options?:Array<{label:string;value:string}>}>>={
    home:[],logs:[],approvals:[],
    users:[{key:'username',label:'用户名',maxlength:64},{key:'password',label:'初始密码',type:'password',maxlength:128},{key:'nickname',label:'姓名',maxlength:100},{key:'phone',label:'联系电话',maxlength:32},{key:'email',label:'邮箱',maxlength:254},{key:'departmentId',label:'部门ID',type:'number'},{key:'status',label:'账号状态',type:'select',options:[{label:'启用',value:'ENABLED'},{label:'停用',value:'DISABLED'}]}],
    roles:[{key:'roleName',label:'角色名称',maxlength:100},{key:'roleKey',label:'角色标识',maxlength:64},{key:'dataScope',label:'数据范围',type:'select',options:[{label:'本人',value:'SELF'},{label:'本部门',value:'DEPT'},{label:'全部',value:'ALL'}]},{key:'remark',label:'备注',maxlength:500},{key:'status',label:'状态',type:'select',options:[{label:'启用',value:'ENABLED'},{label:'停用',value:'DISABLED'}]}],
    dicts:[{key:'dictType',label:'字典类型',maxlength:64},{key:'dictLabel',label:'字典名称',maxlength:100},{key:'dictValue',label:'字典值',maxlength:100},{key:'sortNo',label:'排序号',type:'number'},{key:'status',label:'状态',type:'select',options:[{label:'启用',value:'ENABLED'},{label:'停用',value:'DISABLED'}]}],
  }
  return fields[section]
}
</script>

<template>
  <div class="page-heading"><div><h1>系统管理</h1><p>管理账号、角色权限、字典配置与注册申请</p></div></div>
  <div class="system-layout">
    <aside class="surface system-nav"><button v-for="s in sections" :key="s.key" :class="{selected:active===s.key}" @click="active=s.key"><el-icon><component :is="s.icon" /></el-icon>{{ s.label }}<el-badge v-if="s.key==='approvals'&&active==='approvals'" :value="rows.length" /></button></aside>
    <section class="surface system-content">
      <div v-if="active==='home'" class="entry-cards">
        <button v-for="card in entryCards" :key="card.key" class="entry-card" @click="active=card.key">
          <span class="entry-icon"><el-icon><component :is="card.icon" /></el-icon></span>
          <span class="entry-body"><strong>{{ card.title }}</strong><small>{{ card.desc }}</small></span>
          <span class="entry-link">进入 →</span>
        </button>
      </div>
      <div class="content-toolbar"><div><h2>{{ titles[active] }}</h2><span class="muted-note">{{ subtitles[active] }}</span></div>
        <div class="toolbar-actions"><el-input v-model="query" :prefix-icon="Search" placeholder="搜索" clearable /><el-button v-if="createLabels[active]" type="primary" :icon="Plus" @click="beginCreate">{{ createLabels[active] }}</el-button><el-button :icon="Refresh" @click="load">刷新</el-button></div>
      </div>
      <el-table v-loading="loading" :data="visibleRows" style="width:100%">
        <el-table-column v-for="col in columns" :key="col[0]" :prop="col[0]" :label="col[1]" min-width="110" show-overflow-tooltip>
          <template #default="scope"><span v-if="statusKeys.includes(col[0])" class="status-pill status-muted">{{ cellValue(scope.row,col[0]) }}</span><span v-else>{{ cellValue(scope.row,col[0]) }}</span></template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="170">
          <template #default="scope">
            <template v-if="active==='approvals'"><span class="action-link" @click="review(scope.row,true)">通过</span><span class="action-link danger" @click="review(scope.row,false)">拒绝</span></template>
            <template v-else-if="active==='users'||active==='dicts'"><span class="action-link" @click="beginEdit(scope.row)">编辑</span><span class="action-link danger" @click="remove(scope.row)">删除</span></template>
            <template v-else-if="active==='roles'"><span class="action-link" @click="openPermissions(scope.row)">配置权限</span><span class="action-link" @click="beginEdit(scope.row)">编辑</span><span class="action-link danger" @click="remove(scope.row)">删除</span></template>
            <span v-else class="muted-note">只读</span>
          </template>
        </el-table-column>
        <template #empty>
          <div class="empty-note">
            <span>{{ errorText ? `读取失败：${errorText}` : active==='approvals'?'暂无待审核申请':active==='home'||active==='logs'?'暂无操作日志':'暂无记录' }}</span>
            <el-button v-if="!errorText && createLabels[active]" type="primary" plain size="small" :icon="Plus" @click="beginCreate">{{ createLabels[active] }}</el-button>
          </div>
        </template>
      </el-table>
      <div class="table-footer"><span>{{ errorText ? '请检查管理员权限及后端连接' : `共 ${visibleRows.length} 条记录${query ? '（已按关键词过滤）' : ''}` }}</span><span class="muted-note">后端暂未提供分页与筛选接口，列表为一次性加载后在本页过滤</span></div>
    </section>
  </div>

  <el-dialog v-model="dialogVisible" :title="permissionRoleId?'角色权限配置':editId?'编辑':'新增'" width="min(680px, 94vw)" @closed="permissionRoleId=null">
    <template v-if="permissionRoleId"><el-checkbox-group v-model="selectedMenuIds" class="permission-grid"><el-checkbox v-for="menu in menuRows" :key="menu.id" :value="Number(menu.id)">{{ menu.menuName }} <small>{{ menu.permissionKey || menu.path }}</small></el-checkbox></el-checkbox-group><div class="muted-note" style="margin-top:12px">当前后端管理菜单关联；功能级接口权限仍需后端授权规则支持。</div></template>
    <template v-else><el-form label-position="top"><div class="dialog-form"><el-form-item v-for="field in fieldsFor(active)" :key="field.key" :label="field.label" :class="{full:field.key==='remark'}"><el-select v-if="field.type==='select'" v-model="form[field.key]" style="width:100%"><el-option v-for="o in field.options" :key="o.value" :label="o.label" :value="o.value" /></el-select><el-input-number v-else-if="field.type==='number'" v-model="form[field.key]" :min="0" style="width:100%"/><el-input v-else v-model="form[field.key]" :type="field.type||'text'" :maxlength="field.maxlength" :placeholder="field.key==='password'&&editId?'留空表示不修改':''"/></el-form-item></div></el-form></template>
    <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="permissionRoleId ? savePermissions() : save()">{{ permissionRoleId?'保存权限':'保存' }}</el-button></template>
  </el-dialog>
</template>

<style scoped>
.system-layout{display:grid;grid-template-columns:190px minmax(0,1fr);gap:15px;align-items:start}
.system-nav{padding:9px}.system-nav button{border:0;background:transparent;display:flex;align-items:center;gap:9px;padding:12px;width:100%;border-radius:7px;text-align:left;color:#6f7d91;cursor:pointer;font:inherit;font-size:12px}.system-nav button .el-icon{font-size:16px}.system-nav button:hover,.system-nav button.selected{background:#eaf3ff;color:#1976e9;font-weight:600}
.system-content{padding:18px 18px 0;min-width:0}.content-toolbar{display:flex;align-items:center;justify-content:space-between;gap:12px;margin-bottom:16px}.content-toolbar h2{font-size:15px;margin:0 0 5px}.toolbar-actions{display:flex;gap:8px;align-items:center}.toolbar-actions .el-input{width:175px}.permission-grid{display:grid;grid-template-columns:repeat(2,1fr);gap:12px}.permission-grid small{display:block;color:#9aa4b2;margin:3px 0 0 23px;font-size:10px}
.entry-cards{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:14px;margin-bottom:18px}
.entry-card{display:flex;align-items:center;gap:12px;padding:16px;border:1px solid #e8edf5;border-radius:9px;background:#fff;cursor:pointer;text-align:left;font:inherit;transition:border-color .15s,box-shadow .15s}
.entry-card:hover{border-color:#b9d6fa;box-shadow:0 4px 14px #1976e91a}
.entry-icon{display:inline-flex;align-items:center;justify-content:center;width:38px;height:38px;border-radius:9px;background:#eaf3ff;color:#1976e9;font-size:19px}
.entry-body{display:flex;flex-direction:column;gap:3px;flex:1;min-width:0}
.entry-body strong{font-size:13px;color:#26374d}.entry-body small{font-size:11px;color:#8d98a8}
.entry-link{font-size:11px;color:#1976e9;white-space:nowrap}
@media(max-width:900px){.system-layout{grid-template-columns:1fr}.system-nav{display:flex;overflow-x:auto}.system-nav button{white-space:nowrap;width:auto}.content-toolbar{align-items:flex-start;flex-direction:column}.toolbar-actions{width:100%;flex-wrap:wrap}.toolbar-actions .el-input{flex:1}.entry-cards{grid-template-columns:1fr}}
</style>
