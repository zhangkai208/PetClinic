import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserInfoStore = defineStore('userinfo',
    () => {
        const userInfo = ref({})

        const setUserInfo = (newUserInfo) => {
            userInfo.value = newUserInfo
        }
        const removeUserInfo = () => {
            userInfo.value = {}
        }
        return {
            userInfo, setUserInfo, removeUserInfo
        }
    },
    {
        persist: true
    }
)