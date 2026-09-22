import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { ElButton, ElDivider, ElDrawer, ElInput, ElRate, ElSwitch } from 'element-plus'
import 'element-plus/theme-chalk/base.css'
import 'element-plus/theme-chalk/el-button.css'
import 'element-plus/theme-chalk/el-divider.css'
import 'element-plus/theme-chalk/el-drawer.css'
import 'element-plus/theme-chalk/el-input.css'
import 'element-plus/theme-chalk/el-message.css'
import 'element-plus/theme-chalk/el-overlay.css'
import 'element-plus/theme-chalk/el-rate.css'
import 'element-plus/theme-chalk/el-switch.css'
import './assets/theme.scss'
import './assets/global.scss'
import './assets/mobile.scss'
import './assets/animations.scss'

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.component(ElButton.name, ElButton)
app.component(ElDivider.name, ElDivider)
app.component(ElDrawer.name, ElDrawer)
app.component(ElInput.name, ElInput)
app.component(ElRate.name, ElRate)
app.component(ElSwitch.name, ElSwitch)

app.mount('#app')
