import {useStore} from "@/store/index.js";
import {get} from "@/net/index.js";
export const apiUserInfo = (loadingRef) => {
    loadingRef.value = true
    const store = useStore();
    get('/api/user/info', (data) => {
        store.user = data
        loadingRef.value = false
    })
}
