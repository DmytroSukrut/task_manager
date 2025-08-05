import { createRouter, createWebHistory} from "vue-router";

import LoginAccount from "../components/LoginAccount.vue";
import RegistrationAccount from "../components/RegistrationAccount.vue";
import MainPage from "../components/MainPage.vue";

const routes = [
    { path: "/", component: MainPage },
    { path: '/login', component: LoginAccount },
    { path: '/register', component: RegistrationAccount }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router;