<template>
  <div class="top-panel">
    <h1>TaskManager</h1>
    <button @click="checkLAT">Check</button>
  </div>
  <div class="content">
    <div class="lists">
      <h3 class="text">Your lists here:</h3>
      <button
      v-for="list in userLists"
      :key="list.listName"
      @click="selectedListName = list.listName"
      class="list-button"
      >
        {{list.listName}}
      </button>
    </div>
    <div class="tasks">
      <h3 v-if="selectedListName">Tasks in list "{{selectedListName}}"</h3>
      <p v-else>Select a list to view your tasks!</p>

      <ul v-if="selectedListName">
        <li
            v-for="task in getSelectedListTasks"
            :key="task.taskName"
        >
          <strong>{{task.taskName}}</strong> — {{ task.taskExplanation }} ({{ task.taskStatus }})
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import {onBeforeUnmount, onMounted, ref, computed} from 'vue'
import {useRouter} from 'vue-router'
const router = useRouter()
const socket = ref(null)

const userID = ref('1')
const userLists = ref([])
const selectedListName = ref(null)

onMounted(() => {
  connectWebsocket();
})

const getSelectedListTasks = computed(() => {
  const found = userLists.value.find(list => list.listName === selectedListName.value);
  return found ? found.tasks : [];
});

function checkLAT() {
  const message = {
    type: 'get_users_lists_and_tasks',
    data: {
      userID: userID.value,
    }
  }
  SendMessageToBack(JSON.stringify(message))
}

function connectWebsocket() {
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
    const data = JSON.parse(msg.data)
    console.log('Message from backend: ', JSON.stringify(data, null, 2))

    try {
      switch (data.type) {
        case 'users_lists_and_tasks':
          userLists.value = data.message;
          console.log("Got lists and tasks:", userLists.value);
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
</script>

<style>

.content {
  position: fixed;
  display: flex;
  height: calc(100vh - 100px);
  left: 80px;
  bottom: 30px;
  right: 80px;
  gap: 40px;
}

.lists {
  display: flex;
  width: 300px;
  gap: 10px;
  flex-direction: column;
  align-items: center;
  padding: 10px;
  background: rgba(255, 255, 255, 0.2); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 1px solid rgba(255, 255, 255, 0.2); /* light border */
  border-radius: 10px;
  overflow-y: auto;
}

.tasks {
  flex: 1;
  padding: 10px;
  background: rgba(255, 255, 255, 0.2); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 1px solid rgba(255, 255, 255, 0.2); /* light border */
  border-radius: 10px;
  overflow-y: auto;
}

.list-button{
  width: 90%;
}

.text {
  font-size: 35px;
  margin: 0;
}

.top-panel {
  margin: 0;
  padding: 0;
  gap: 20px;
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

</style>