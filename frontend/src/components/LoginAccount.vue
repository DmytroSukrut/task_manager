<template>
  <div class="login">
    <h1 class="SiteName">Task manager</h1>
    <p>Please login in account</p>
    <input type="text" v-model="email" placeholder="Enter your email"/>
    <input type="text" v-model="password" placeholder="Enter your password"/>
    <button class="login_button" @click="login">Login</button>
    <p>
      If you dont have account,
      <router-link to="/register" class="register-link">create it here</router-link>
    </p>
  </div>
</template>

<script setup>
import {onBeforeUnmount, onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
const router = useRouter()
const id = ref(null)
const name = ref('')
const email = ref('')
const password = ref('')
const socket = ref(null)

function login() {
  const message = {
    type: 'login',
    data: {
      email: email.value,
      password: password.value
    }
  }
  SendMessageToBack(JSON.stringify(message))
}

function connectWebsocket(){
  socket.value = new WebSocket('ws://localhost:8080/ws')

  socket.value.onopen = () => {
    console.log('WebSocket connected')
  }

  socket.value.onerror = (error) => {
    console.error('WebSocket error: ', error)
  }

  socket.value.onclose = () => {
    console.warn('WebSocket closed')
  }

  socket.value.onmessage = (msg) => {
    console.log('Message from backend: ', msg)
    const data = JSON.parse(msg.data)

    try {
      switch (data.type) {
        case 'login_success':
          id.value = data.user_id;
          name.value = data.user_name;
          email.value = data.user_email;


          const profile = {
            UserID: id.value,
            UserName: name.value,
            UserEmail: email.value,
            ts: Date.now()
          }

          localStorage.setItem('prof', JSON.stringify(profile));

          alert(`You have successfully logged in ${name.value}!`);
          router.push('/taskmanager');
          break;
        case 'login_fail':
          alert(`Wrong email or password`);
          break;
        default:
          console.warn('Unknown message from backend: ', msg);
      }
    } catch (error) {
      console.error(data);
      console.error('Error parsing message from server: ', error)
    }
  }
}

function SendMessageToBack(cmd) {
  if(socket.value.readyState === WebSocket.OPEN){
    socket.value.send(cmd)
  } else {
    console.warn("WebSocket not ready");
  }
}

onMounted(() => {
  connectWebsocket();
})

onBeforeUnmount(() => {
  if(socket.value) {
    socket.value.close()
  }
})
</script>

<style>

.register-link {
  cursor: pointer;
  color: #747bff;
  text-decoration: underline;
}
.register-link:hover {
  color: #5f67da;
}

.login_button {
  background-color: #747bff;
  border-radius: 3px;
  border-color: #444cef;
  transition: all .3s ease-in-out;
  cursor: pointer;
  outline: none;
}

.login_button:hover {
  transform: scale(1.03);
  outline: none;
}

.login_button:active {
  background-color: #5f67da;
  border-color: #3b43ef;
  transition: .05s;
  transform: scale(0.98);
  outline: none;
}

.login_button,
.login_button:focus,
.login_button:focus-visible,
.login_button:active,
.login_button:hover {
  outline: none !important;
}

.login {
  width: 400px;
  margin: 10px auto;
  padding: 30px;
  display: flex;
  flex-direction: column;
  border: 1px solid black;
  border-radius: 8px;
  background-color: #a7a7a7;
  box-shadow: 0 1px 10px black;
}

.login input {
  margin-bottom: 12px;
  padding: 10px;
}

body {
  background: linear-gradient(140deg, rgba(2, 0, 36, 1) 0%, rgba(9, 9, 121, 1) 35%, rgba(0, 212, 255, 1) 100%);
  z-index: 2;
}
</style>