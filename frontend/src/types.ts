export interface ApiResponse<T> {
  code: number
  message: string
  path: string | null
  data: T
  errors: Array<{ field: string; message: string }>
  timestamp: string
}

export interface UserInfo {
  id: number
  username: string
  nickname?: string
  departmentId?: number | null
  status?: string
  approvalStatus?: string
  roleKeys?: string[]
}

export interface LoginData {
  token: string
  tokenType: string
  expiresIn: number
  user: UserInfo
}

export type CrmRecord = Record<string, any> & { id?: number }
