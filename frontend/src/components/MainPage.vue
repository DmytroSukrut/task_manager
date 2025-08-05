<template>
  <div class="top-panel">
    <button class="MainPage">Task manager</button>
    <button class="Features">Features</button>
    <button class="About">About</button>
    <div class="spacer"></div>
    <button class="LogIn" @click="GoLogIn">Log in</button>
  </div>
  <div class="site-name-wrapper">
    <div class="site-name-left" ref="tiltElement">
      <h1>Task manager</h1>
    </div>
  </div>

  <div class="site-small-info-right">
    <h1 class="Quote">Organize Your Life — Menage your tasks</h1>
    <p class="main-text">
      &nbsp;&nbsp;&nbsp;&nbsp;This task manager is designed to help you take control of your day.
      Whether you're planning your studies, managing work, or just keeping
      track of daily chores — this tool makes it simple and fast.
      <br>
    </p>
    <p class="small-goods">&nbsp;&nbsp;&nbsp;&nbsp;📝 Add your own tasks</p>
    <p class="small-goods">&nbsp;&nbsp;&nbsp;&nbsp;✅ Mark them as done</p>
    <p class="small-goods">&nbsp;&nbsp;&nbsp;&nbsp;🔄 Stay motivated with small wins</p>
    <p class="small-goods">&nbsp;&nbsp;&nbsp;&nbsp;📊 Track your progress</p>
  </div>

  <div class="bottom-panel">
    <div class="white-panel">
      <a class="task" :class="{ animate: animateTask }">💡 {{ taskText }}</a>
    </div>
    <div class="spacer"></div>
    <button class="what-to-do-button" @click="GetRandomTask">What to do?</button>
  </div>
</template>

<script setup>

import {onBeforeUnmount, onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import VanillaTilt from 'vanilla-tilt'

const tiltElement = ref(null)
const router = useRouter()
const taskText = ref('')
const socket = ref(null)

const animateTask = ref(false)

  function GoLogIn() {
    router.push('/login')
  }

  function GetRandomTask() {
    animateTask.value = false
    void taskText.value

    setTimeout(() => {
      animateTask.value = true
    }, 10)

    const message = {
      type: 'get_random_task',
    }
    SendMessageToBack(JSON.stringify(message))
  }

  function connectWebsocket() {
    socket.value = new WebSocket('ws://localhost:8080/ws')

    socket.value.onopen = () => {
      console.log('WebSocket connected')
      GetRandomTask();
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
          case 'random_task_got':
            taskText.value = data.message;
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
    if(tiltElement.value) {
      VanillaTilt.init(tiltElement.value,{
        reverse: true,
        perspective: 1000,
        easing: "cubic-bezier(.03,.98,.52,.99)",
        max: 10,
        speed: 100,
        scale: 1.05,
        glare: true,
        "max-glare": 0.4,
      })
    }
  })
</script>

<style>

.small-goods{
  font-size: 20px;
  margin-bottom: 5px;
  margin-top: 15px;
}

.top-panel {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 50px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: flex-start;
  background: rgba(255, 255, 255, 0.5); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  position: fixed;
  top: 0;
  left: 0;
  z-index: 2;
}

.bottom-panel {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 50px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: flex-start;
  background: rgba(255, 255, 255, 0.5); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  position: fixed;
  bottom: 0;
  left: 0;
  z-index: 2;
}

.white-panel {
  height: 80%;
  margin-left: 270px;
  width: 800px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.7);
}

.main-text {
  font-size: 20px;
  color: white;
  line-height: 1.5;
  text-shadow: 0 0 5px rgba(0, 0, 0, 0.3);
}


.Quote {
  margin-top: 30px;
  font-size: 30px;
}

.what-to-do-button {
  margin-right: 30px;
  background-color: rgba(115, 122, 253, 0.9);
  border-radius: 3px;
  border-color: #444cef;
  cursor: pointer;
  transition: all .1s ease-in-out;
}

.what-to-do-button:hover {
  transform: scale(1.03);
  outline: none;
}

.what-to-do-button:active {
  background-color: #5f67da;
  border-color: #3b43ef;
  transition: .01s;
  transform: scale(0.98);
  outline: none;
}

.task {
  display: block;
  text-align: center;
  color: #272993;
  margin-top: 3px;
}

.task.animate{
  opacity: 0;
  transform: translateY(10px);
  animation: fadeInUp 0.4s ease-in-out forwards;
}

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.site-small-info-right {
  display: flex;
  position: fixed;
  right: 50px;
  top: 50%;
  padding-left: 30px;
  transform: translateY(-50%);

  flex-direction: column;
  text-align: left;
  width: 700px;
  height: 450px;


  font-size: 40px;

  background: rgba(255, 255, 255, 0.2); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 1px solid rgba(255, 255, 255, 0.2); /* light border */
  border-radius: 20px;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.2);
}

.site-name-wrapper {
  display: flex;
  position: fixed;
  left: 50px;
  top: 50%;
  transform: translateY(-50%);
}

.site-name-left {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 600px;
  height: 310px;
  margin: 0;
  padding: 0;
  font-size: 40px;

  background: rgba(255, 255, 255, 0.2); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 1px solid rgba(255, 255, 255, 0.2); /* light border */
  border-radius: 20px;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.2);
}

.spacer {
  flex-grow: 1;
}

.LogIn {
  height: 100%;
  padding: 5px;
  width: 200px;
  background-color: rgba(122, 122, 122, 0.9);
  border-radius: 1px;
  border-right: 1px solid black;
  border-left: 1px solid black;
  cursor: pointer;
  font-size: 35px;
  transition: all .3s ease-in-out;
}

.LogIn:hover {
  background-color: rgba(81, 81, 81, 0.9);
}

.About {
  height: 100%;
  padding: 5px;
  width: 200px;
  background-color: rgba(122, 122, 122, 0.9);
  border-radius: 1px;
  border-right: 1px solid black;
  cursor: pointer;
  font-size: 35px;
  transition: all .3s ease-in-out;
}

.About:hover {
  background-color: rgba(81, 81, 81, 0.9);
}

.Features {
  padding: 5px;
  height: 100%;
  width: 200px;
  background-color: rgba(122, 122, 122, 0.9);
  border-radius: 0;
  border-right: 1px solid black;
  cursor: pointer;
  font-size: 35px;
  transition: all .3s ease-in-out;
}

.Features:hover {
  background-color: rgba(81, 81, 81, 0.9);
}


.MainPage {
  height: 100%;
  padding: 5px;
  width: 400px;
  background-color: rgba(115, 122, 253, 0.9);
  border-radius: 0;
  border-left: 3px solid #444cef;
  border-right: 3px solid #444cef;
  cursor: pointer;
  font-size: 40px;
  transition: all .3s ease-in-out;
}

.MainPage:hover {
  background-color: rgba(95, 102, 251, 0.9);
}

.MainPage,
.MainPage:focus,
.MainPage:focus-visible,
.MainPage:active,
.MainPage:hover,
.Features,
.Features:focus,
.Features:focus-visible,
.Features:active,
.Features:hover,
.About,
.About:focus,
.About:focus-visible,
.About:active,
.About:hover,
.LogIn,
.LogIn:focus,
.LogIn:focus-visible,
.LogIn:active,
.LogIn:hover,
.what-to-do-button,
.what-to-do-button:focus,
.what-to-do-button:focus-visible,
.what-to-do-button:active,
.what-to-do-button:hover{
  outline: none !important;
  z-index: 5;
}

body {
  background: #020024;
  background: linear-gradient(140deg, rgba(2, 0, 36, 1) 0%, rgba(9, 9, 121, 1) 35%, rgba(0, 212, 255, 1) 100%);
  z-index: 2;
}

</style>