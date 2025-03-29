import {axios} from '../utils/request'

type LoginInfo = {
  username: string,
  password: string
}

type AllInfo = {
  username: string;
  password: string;
  name: string;
  avatar: string;
  role: string;
  telephone: string;
  email: string;
  location: string;
}

// 用户登录
export const userLogin = (loginInfo: LoginInfo) => {
  return axios.post(`/api/accounts/login`, null, {params: loginInfo})
    .then(res => {
      return res
    })
}

// 用户注册
export const userRegister = (registerInfo: AllInfo) => {
  return axios.post(`http://localhost:8080/api/accounts`, registerInfo,
    {headers: {'Content-Type': 'application/json'}})
    .then(res => {
      return res
    })
}

// 获取用户信息
export const userInfo = (username: String) => {
  return axios.get(`/api/accounts/${username}`)
    .then(res => {
      return res
    })
}

// 更新用户信息
export const userInfoUpdate = (updateInfo: AllInfo) => {
  return axios.put(`/api/accounts/${updateInfo.username}`, updateInfo, {headers: {'Content-Type': 'application/json'}})
    .then(res => {
      return res
    })
}
