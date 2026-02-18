import { createPinia } from 'pinia'
import persist from 'pinia-plugin-persistedstate'

const pinia = createPinia()
pinia.use(persist)

export default pinia

export * from './modules/user'
export * from './modules/article'
export * from './modules/category'
export * from './modules/comment'
export * from './modules/friendLink'
export * from './modules/message'
export * from './modules/operationLog'
export * from './modules/visitor'
export * from './modules/profile'
export * from './modules/music'
export * from './modules/analytics'
