import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('TOKEN') || '',
    profile: null
  }),
  actions: {
    setToken(token) {
      this.token = token
      if (token) localStorage.setItem('TOKEN', token)
      else localStorage.removeItem('TOKEN')
    },
    setProfile(profile) {
      this.profile = profile
    },
    logout() {
      this.setToken('')
      this.setProfile(null)
    }
  }
})


