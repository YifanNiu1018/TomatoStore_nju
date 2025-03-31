//将身份转化为中文显示
export function parseRole(role: string | null) {
  if (role === 'admin') {
    return "管理员"
  } else if (role === 'customer') {
    return "顾客"
  }
}