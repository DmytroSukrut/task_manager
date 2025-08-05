import { createRouter, createWebHistory} from "vue-router";

import LoginAccount from "../components/LoginAccount.vue";
import RegistrationAccount from "../components/RegistrationAccount.vue";
import MainPage from "../components/MainPage.vue";
import TaskManagerPage from "../components/TaskManagerPage.vue";

const routes = [
    { path: "/", component: MainPage },
    { path: '/login', component: LoginAccount },
    { path: '/register', component: RegistrationAccount },
    { path: '/taskmanager', component: TaskManagerPage },
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router;