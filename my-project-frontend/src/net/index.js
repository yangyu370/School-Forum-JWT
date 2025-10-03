import axios from 'axios'
import {ElMessage} from "element-plus";
const authItemName="access_token"
const defaultFailure=(message,code,url)=>{
  console.warn(`请求地址：${url},状态码：${code},错误信息: ${message}`)
    ElMessage.warning(message)
}
const defaultError=(err)=>{
    console.warn(err)
    ElMessage.warning("发生了一些错误，请联系管理员")
}
function takeAccessToken(){
    const str=localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName)
    if(!str){
        return null;
    }
    const authObj=JSON.parse(str)
    if(authObj.expire<=Date.now()){
        deleteAccessToken()
        ElMessage.warning("登录状态已过期,请重新登录")
        return null
    }
    return authObj
}
function storeAccessToken(token,remember,expire,role){
    const authObj={token:token,expire:expire,role:role}
    const str=JSON.stringify(authObj)
    if(remember)
        localStorage.setItem(authItemName,str)
    else
        sessionStorage.setItem(authItemName,str)
}
function deleteAccessToken(){
    localStorage.removeItem(authItemName)
    sessionStorage.removeItem(authItemName)
}
function internalPost(url, data, headers, success, failure, error = defaultError) {
    axios.post(url, data, { headers: headers }).then(({ data }) => {
        if (data.code === 200) {
            success(data.data)
        } else {
            failure(data.message, data.code, url)
        }
    }).catch(err => error(err))
}
function internalGet(url, headers, success, failure, error = defaultError) {
    axios.get(url, { headers: headers}).then(({ data }) => {
        if (data.code === 200) {
            success(data.data)
        } else {
            failure(data.message, data.code, url)
        }
    }).catch(err => error(err))
}
const accessHeader = () => {
    return {
        'Authorization': `Bearer ${takeAccessToken()?.token}`
    }
}
function get(url,success,failure=defaultFailure){
   internalGet(url,accessHeader(),success,failure)
}
function post(url,data,success,failure=defaultFailure){
    internalPost(url,data,accessHeader(),success,failure)
}
function unauthorized(){
    return !takeAccessToken()
}
function login(username,password,remember,success,failure=defaultFailure){
    internalPost("/api/auth/login",{
        username: username,
        password: password
    },{
        'Content-Type': 'application/x-www-form-urlencoded'
    },(data)=>{
         storeAccessToken(data.token,remember,data.expire,data.role )
         ElMessage.success(`登录成功,欢迎${username}！`)
         success(data)
    },failure)
}
function logout(success,failure=defaultFailure){
   get("/api/auth/logout",()=>{
       deleteAccessToken()
       ElMessage.success("退出登录成功")
       success()
   },(message,code,url)=>{
       console.error(`退出登录失败: ${message}`)
       failure(message,code,url)
   })
}
function isRoleAdmin() {
    return  takeAccessToken()?.role === 'admin'
}
export {login,logout,get,unauthorized,post,accessHeader,isRoleAdmin}
