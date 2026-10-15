import { createApp } from 'vue'
import {
  ElAvatar, ElBadge, ElButton, ElCard, ElCheckbox, ElCheckboxGroup, ElDatePicker,
  ElDescriptions, ElDescriptionsItem, ElDialog, ElDropdown, ElDropdownItem,
  ElDropdownMenu, ElForm, ElFormItem, ElIcon, ElInput, ElInputNumber, ElLink,
  ElOption, ElPagination, ElSelect, ElTable, ElTableColumn, ElTag, ElTooltip,
} from 'element-plus'
import {
  ArrowDown, Avatar, Bell, Checked, Collection, DataLine, Document, Fold,
  Key, Lock, Loading, Money, Opportunity, Promotion, Setting, Tickets,
  TrendCharts, User, Warning,
} from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
import './style.css'
import App from './App.vue'
import router from './router'

const app = createApp(App)
const icons = { ArrowDown, Avatar, Bell, Checked, Collection, DataLine, Document, Fold,
  Key, Lock, Loading, Money, Opportunity, Promotion, Setting, Tickets, TrendCharts, User, Warning }
for (const [name, component] of Object.entries(icons)) {
  app.component(name, component)
}
const components = {
  ElAvatar, ElBadge, ElButton, ElCard, ElCheckbox, ElCheckboxGroup, ElDatePicker,
  ElDescriptions, ElDescriptionsItem, ElDialog, ElDropdown, ElDropdownItem,
  ElDropdownMenu, ElForm, ElFormItem, ElIcon, ElInput, ElInputNumber, ElLink,
  ElOption, ElPagination, ElSelect, ElTable, ElTableColumn, ElTag, ElTooltip,
}
for (const [name, component] of Object.entries(components)) app.component(name, component)
app.use(router).mount('#app')
