import type { CrmRecord, LoginData } from './types'

export const DEMO_USERNAME = 'demo'
export const DEMO_PASSWORD = 'CordysDemo2026!'
export const DEMO_SESSION_KEY = 'crm_demo_session'

const today = new Date().toISOString().slice(0, 10)
const initialData: Record<string, CrmRecord[]> = {
  '/api/customers': [
    { id: 101, name:'成都华星科技有限公司', industry:'信息技术', customerType:'企业客户', contact:'张经理', phone:'138****0021', ownerId:900001, source:'官网', address:'成都高新区', status:'ACTIVE', createdAt:`${today}T09:30:00` },
    { id: 102, name:'蓝海数字服务有限公司', industry:'软件服务', customerType:'重点客户', contact:'王总', phone:'139****1288', ownerId:900001, source:'展会', address:'成都市武侯区', status:'ACTIVE', createdAt:`${today}T10:10:00` },
    { id: 103, name:'北辰智能设备有限公司', industry:'智能制造', customerType:'企业客户', contact:'赵女士', phone:'137****6610', ownerId:900002, source:'客户推荐', address:'重庆市渝北区', status:'ACTIVE', createdAt:'2026-10-13T15:20:00' },
    { id: 104, name:'星河供应链管理有限公司', industry:'物流运输', customerType:'潜在客户', contact:'刘经理', phone:'136****2514', ownerId:900002, source:'电话咨询', address:'成都市锦江区', status:'INACTIVE', createdAt:'2026-10-12T14:05:00' },
    { id: 105, name:'云杉企业咨询有限公司', industry:'商务服务', customerType:'企业客户', contact:'陈总', phone:'135****4027', ownerId:900001, source:'官网', address:'成都市天府新区', status:'ACTIVE', createdAt:'2026-10-11T11:12:00' },
    { id: 106, name:'远山医疗科技有限公司', industry:'医疗健康', customerType:'重点客户', contact:'周主任', phone:'186****7411', ownerId:900002, source:'客户推荐', address:'绵阳市涪城区', status:'ACTIVE', createdAt:'2026-10-10T16:40:00' },
    { id: 107, name:'青禾新能源有限公司', industry:'新能源', customerType:'企业客户', contact:'杨工', phone:'185****9351', ownerId:900001, source:'行业活动', address:'德阳市旌阳区', status:'ACTIVE', createdAt:'2026-10-09T13:15:00' },
    { id: 108, name:'锦程文化传媒有限公司', industry:'文化传媒', customerType:'潜在客户', contact:'吴女士', phone:'187****1836', ownerId:900002, source:'官网', address:'成都市青羊区', status:'ACTIVE', createdAt:'2026-10-08T09:15:00' },
    { id: 109, name:'启明电子商务有限公司', industry:'电子商务', customerType:'企业客户', contact:'孙经理', phone:'189****3305', ownerId:900001, source:'展会', address:'成都市高新区', status:'ACTIVE', createdAt:'2026-10-07T10:00:00' },
    { id: 110, name:'嘉木建筑设计有限公司', industry:'建筑设计', customerType:'企业客户', contact:'郑工', phone:'180****1168', ownerId:900002, source:'客户推荐', address:'成都市金牛区', status:'ACTIVE', createdAt:'2026-10-06T08:55:00' },
  ],
  '/api/clues': [
    {id:201,name:'西岭科技采购线索',phone:'138****8142',source:'官网',ownerId:900001,status:'NEW',remark:'关注客户管理模块',createdAt:`${today}T09:12:00`},
    {id:202,name:'锦江商贸数字化需求',phone:'139****3365',source:'展会',ownerId:900002,status:'FOLLOWING',remark:'已预约产品演示',createdAt:'2026-10-13T15:10:00'},
    {id:203,name:'川渝物流系统升级',phone:'136****9958',source:'客户推荐',ownerId:900001,status:'FOLLOWING',remark:'等待预算确认',createdAt:'2026-10-12T10:45:00'},
    {id:204,name:'乐水文旅合作咨询',phone:'187****2208',source:'电话咨询',ownerId:900002,status:'CONVERTED',remark:'已建立客户档案',createdAt:'2026-10-11T11:00:00'},
  ],
  '/api/opportunities': [
    {id:301,name:'华星 CRM 系统升级',customerId:101,amount:180000,stage:'INITIAL_CONTACT',expectTime:'2026-12-15',winRate:20,ownerId:900001,nextAction:'安排需求调研',createdAt:`${today}T09:30:00`},
    {id:302,name:'蓝海销售平台项目',customerId:102,amount:260000,stage:'NEEDS_CONFIRMED',expectTime:'2026-11-28',winRate:40,ownerId:900001,nextAction:'确认接口范围',createdAt:'2026-10-13T10:20:00'},
    {id:303,name:'北辰客户管理平台',customerId:103,amount:320000,stage:'PROPOSAL',expectTime:'2026-12-08',winRate:60,ownerId:900002,nextAction:'发送最终报价',createdAt:'2026-10-12T15:00:00'},
    {id:304,name:'星河供应链协同项目',customerId:104,amount:145000,stage:'NEGOTIATION',expectTime:'2026-11-20',winRate:80,ownerId:900002,nextAction:'确认合同条款',createdAt:'2026-10-10T11:00:00'},
    {id:305,name:'云杉客户运营平台',customerId:105,amount:98000,stage:'WON',expectTime:'2026-10-18',winRate:100,ownerId:900001,nextAction:'准备合同',createdAt:'2026-10-08T08:00:00'},
    {id:306,name:'远山服务管理项目',customerId:106,amount:76000,stage:'LOST',expectTime:'2026-10-20',winRate:0,lostReason:'预算暂缓',ownerId:900002,createdAt:'2026-10-07T13:20:00'},
  ],
  '/api/contracts': [
    {id:401,contractNo:'HT-2026-0018',name:'华星 CRM 实施合同',customerId:101,amount:180000,signDate:'2026-10-12',expireDate:'2027-10-11',status:'EFFECTIVE',createdAt:'2026-10-12T10:00:00'},
    {id:402,contractNo:'HT-2026-0019',name:'蓝海销售平台服务合同',customerId:102,amount:260000,signDate:'2026-10-10',expireDate:'2027-10-09',status:'IN_PROGRESS',createdAt:'2026-10-10T09:00:00'},
    {id:403,contractNo:'HT-2026-0020',name:'北辰平台采购合同',customerId:103,amount:320000,signDate:null,expireDate:null,status:'DRAFT',createdAt:'2026-10-09T13:00:00'},
    {id:404,contractNo:'HT-2026-0021',name:'云杉运营服务合同',customerId:105,amount:98000,signDate:'2026-09-26',expireDate:'2027-09-25',status:'COMPLETED',createdAt:'2026-09-26T14:00:00'},
  ],
  '/api/follow-records': [
    {id:501,customerId:101,customerName:'成都华星科技有限公司',contact:'张经理',content:'确认产品演示时间',ownerName:'演示管理员',createdAt:`${today}T10:30:00`},
    {id:502,customerId:102,customerName:'蓝海数字服务有限公司',contact:'王总',content:'已发送方案报价',ownerName:'演示管理员',createdAt:`${today}T09:20:00`},
    {id:503,customerId:103,customerName:'北辰智能设备有限公司',contact:'赵女士',content:'跟进采购计划',ownerName:'演示销售',createdAt:'2026-10-14T16:40:00'},
  ],
  '/api/users': [
    {id:900001,username:'demo',nickname:'演示管理员',phone:'138****0000',email:'demo@example.test',departmentId:1,status:'ENABLED',approvalStatus:'APPROVED'},
    {id:900002,username:'demo_sales',nickname:'演示销售',phone:'139****0000',email:'sales@example.test',departmentId:1,status:'ENABLED',approvalStatus:'APPROVED'},
  ],
  '/api/roles': [
    {id:1,roleName:'系统管理员',roleKey:'admin',dataScope:'ALL',remark:'离线演示角色',status:'ENABLED'},
    {id:2,roleName:'销售专员',roleKey:'sales',dataScope:'SELF',remark:'离线演示角色',status:'ENABLED'},
  ],
  '/api/dicts': [
    {id:601,dictType:'industry',dictLabel:'信息技术',dictValue:'信息技术',sortNo:1,status:'ENABLED'},
    {id:602,dictType:'industry',dictLabel:'智能制造',dictValue:'智能制造',sortNo:2,status:'ENABLED'},
    {id:603,dictType:'customer_source',dictLabel:'官网',dictValue:'官网',sortNo:1,status:'ENABLED'},
    {id:604,dictType:'customer_source',dictLabel:'客户推荐',dictValue:'客户推荐',sortNo:2,status:'ENABLED'},
  ],
  '/api/operation-logs': [
    {id:701,operatorId:900001,module:'客户管理',action:'CREATE',targetType:'customer',targetId:'101',result:'SUCCESS',ipAddress:'127.0.0.1',createdAt:`${today} 10:30:00`},
    {id:702,operatorId:900001,module:'商机管理',action:'UPDATE',targetType:'opportunity',targetId:'302',result:'SUCCESS',ipAddress:'127.0.0.1',createdAt:`${today} 09:45:00`},
    {id:703,operatorId:900002,module:'合同管理',action:'CREATE',targetType:'contract',targetId:'401',result:'SUCCESS',ipAddress:'127.0.0.1',createdAt:'2026-10-14 17:20:00'},
  ],
  '/api/admin/registrations': [
    {id:900003,username:'new_sales',nickname:'待审核用户',phone:'137****0999',email:'new-sales@example.test',approvalStatus:'PENDING',status:'DISABLED',createdAt:`${today}T08:30:00`},
  ],
  '/api/menus': [
    {id:1,menuName:'工作台',path:'/dashboard',permissionKey:'dashboard:view'},
    {id:2,menuName:'客户管理',path:'/customers',permissionKey:'customer:read'},
    {id:3,menuName:'商机管理',path:'/opportunities',permissionKey:'opportunity:read'},
    {id:4,menuName:'合同管理',path:'/contracts',permissionKey:'contract:read'},
    {id:5,menuName:'系统管理',path:'/system',permissionKey:'system:manage'},
  ],
  '/api/role-menus': [
    {roleId:1,menuId:1},{roleId:1,menuId:2},{roleId:1,menuId:3},{roleId:1,menuId:4},{roleId:1,menuId:5},
    {roleId:2,menuId:1},{roleId:2,menuId:2},{roleId:2,menuId:3},
  ],
}

function dataStore(): Record<string, CrmRecord[]> {
  try {
    const saved = localStorage.getItem('crm_demo_data')
    if (saved) return JSON.parse(saved) as Record<string, CrmRecord[]>
  } catch { /* reset invalid local demo data */ }
  const copy = structuredClone(initialData)
  localStorage.setItem('crm_demo_data', JSON.stringify(copy))
  return copy
}

function saveStore(data: Record<string, CrmRecord[]>) { localStorage.setItem('crm_demo_data', JSON.stringify(data)) }
function normalizedPath(url: string) { return url.split('?')[0].replace(/\/$/, '') || '/' }
function keyFor(url: string) {
  const path = normalizedPath(url)
  if (path.startsWith('/api/role-menus/')) return '/api/role-menus'
  return path
}

export function demoList(url: string): CrmRecord[] {
  const data = dataStore()
  return structuredClone(data[keyFor(url)] || [])
}
export function demoGet(url: string): CrmRecord | undefined {
  const path = normalizedPath(url)
  const id = Number(path.split('/').pop())
  return demoList(keyFor(url)).find(r => Number(r.id) === id)
}
export function demoCreate(url: string, value: CrmRecord): CrmRecord {
  const data = dataStore();const key=keyFor(url);const list=data[key]||[]
  const nextId=Math.max(1000,...list.map(r=>Number(r.id)||0))+1
  const row={...value,id:nextId,createdAt:value.createdAt||new Date().toISOString()}
  list.unshift(row);data[key]=list;saveStore(data);return structuredClone(row)
}
export function demoUpdate(url: string, id: number, value: CrmRecord): CrmRecord | undefined {
  const data=dataStore();const key=keyFor(url);const list=data[key]||[];const index=list.findIndex(r=>Number(r.id)===id)
  if(index<0)return undefined
  list[index]={...list[index],...value,id};data[key]=list;saveStore(data);return structuredClone(list[index])
}
export function demoRemove(url: string, id: number, parentId?: number): boolean {
  const data=dataStore();const key=keyFor(url);const list=data[key]||[]
  const remaining=list.filter(r=>key==='/api/role-menus' ? !(Number(r.roleId)===parentId&&Number(r.menuId)===id) : Number(r.id)!==id)
  const changed=remaining.length!==list.length;data[key]=remaining;saveStore(data);return changed
}
export function demoReview(id: number, approve: boolean) {
  return demoUpdate('/api/admin/registrations',id,{approvalStatus:approve?'APPROVED':'REJECTED',status:approve?'ENABLED':'DISABLED',reviewedAt:new Date().toISOString()})
}

export function demoLoginData(): LoginData {
  return { token:'local-demo-session-only',tokenType:'Bearer',expiresIn:86400,
    user:{id:900001,username:DEMO_USERNAME,nickname:'演示管理员',departmentId:1,status:'ENABLED',approvalStatus:'APPROVED',roleKeys:['admin']} }
}
