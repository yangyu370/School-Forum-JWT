import {get, post} from "@/net"
export const apiEmailRecordList=(page, size, success)=>
    get(`/api/admin/email/list?page=${page}&size=${size}`,success)
export const apiEmailResend=(id, success, failure)=>
    post(`/api/admin/email/resend`, {id}, success, failure)