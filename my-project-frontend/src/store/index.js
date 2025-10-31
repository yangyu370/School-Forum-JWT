import {defineStore} from "pinia"
import axios from "axios";
export  const useStore=defineStore('general',{
    state:()=>{
        return {
            user:{
                id:-1,
                username:'',
                email:'',
                role:'',
                avatar:null,
                registerTime: null
            },
            forum:{
                types:[]
            }
        }
    },getters:{
        avatarUrl(){
            if(this.user.avatar)
                return `${axios.defaults.baseURL}/images${this.user.avatar}`
            else
                return 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
        }
    },actions:{
        async loadForumTypes() {
            if (this.forum.types.length > 0) return  // 如果已加载则跳过
            const {apiForumTypes} = await import('@/net/api/forum')
            apiForumTypes(data => {
                const array = []
                array.push({name: '全部', id: 0, color: 'linear-gradient(45deg, white, red, orange, gold, green, blue)'})
                data.forEach(d => array.push(d))
                this.forum.types = array
            })
        },
        findTypeById(id){
            for (let type of this.forum.types) {
                if(type.id === id){
                    return type
                }
            }
            return {name: '未知', color: '#999999'}
        },
        avatarUserUrl(avatar) {
            if(avatar)
                return `${axios.defaults.baseURL}/images${avatar}`
            else
                return 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
        }
    }
})