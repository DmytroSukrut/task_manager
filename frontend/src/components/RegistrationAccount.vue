<template>
  <div class="register">
    <h1 class="SiteName">Task manager</h1>
    <input type="text" v-model="name" placeholder="Enter your name"/>
    <input type="text" v-model="email" placeholder="Enter your email"/>
    <input type="text" v-model="password" placeholder="Enter your password"/>
    <button  class="reg_button" @click="register">Register</button>
  </div>
</template>

<script setup>
  import {onBeforeUnmount, onMounted, ref} from 'vue'
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
  background-color: white;
}
</style>