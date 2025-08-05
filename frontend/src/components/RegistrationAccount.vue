<template>
  <div class="register">
    <h1 class="SiteName">Task manager</h1>
    <p>Please register an account</p>
    <input type="text" v-model="name" placeholder="Enter your name"/>
    <input type="text" v-model="email" placeholder="Enter your email"/>
    <input type="text" v-model="password" placeholder="Enter your password"/>
    <button  class="reg_button" @click="register">Register</button>
    <p>
      If you have account,
      <router-link to="/login" class="register-link">log in it here</router-link>
    </p>
  </div>
</template>

<script setup>
  import {onBeforeUnmount, onMounted, ref} from 'vue'
  import {useRouter} from 'vue-router'
  const router = useRouter()
  const name = ref('')
  const email = ref('')
  const password = ref('')
  const socket = ref(null)

  function register() {
    const message = {
      type: 'register',
      data: {
        name: name.value,
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
          case 'register_success':
            alert(`Thank you for registration ${data.message}!`);
            router.push('/login')
            break;
          case 'user_exists':
            alert(`Sorry but this user has already been registered!`);
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

.reg_button {
  background-color: #747bff;
  border-radius: 3px;
  border-color: #444cef;
  transition: all .3s ease-in-out;
  cursor: pointer;
  outline: none;
}

.reg_button:hover {
  transform: scale(1.03);
  outline: none;
}

.reg_button:active {
  background-color: #5f67da;
  border-color: #3b43ef;
  transition: .05s;
  transform: scale(0.98);
  outline: none;
}

.reg_button,
.reg_button:focus,
.reg_button:focus-visible,
.reg_button:active,
.reg_button:hover {
  outline: none !important;
}

.register {
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

.register input {
  margin-bottom: 12px;
  padding: 10px;
}

body {
  background: linear-gradient(140deg, rgba(2, 0, 36, 1) 0%, rgba(9, 9, 121, 1) 35%, rgba(0, 212, 255, 1) 100%);
  z-index: 2;
}
</style>