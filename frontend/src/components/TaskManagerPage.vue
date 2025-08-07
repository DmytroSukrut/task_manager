<template>
  <div class="top-panel-tm">
    <h1>Task Manager</h1>
    <button @click="checkLAT">Check</button>
  </div>
  <div class="content">
    <div class="lists">
      <div class="lists-here">
        <h3 class="text">Your lists here</h3>
      </div>
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
      <div class="listnb-and-button">
        <div class="list-name-box">
          <h3 v-if="selectedListName" class="list-name">"{{selectedListName}}"</h3>
          <p v-else class="list-name">Select a list to view your tasks!</p>
        </div>

        <button
            v-if="selectedListName"
            class="add-new-task-button"
            @click="checkADDTASK"
        >
          Add new task
        </button>
      </div>
      <ul v-if="selectedListName">
        <li
            v-for="task in getSelectedListTasks"
            :key="task.taskName"
        >
          <div class="tasks-box">
            <div class="tasks-box-section name">{{ task.taskName }}</div>
            <div class="tasks-box-section explanation">{{ task.taskExplanation }}</div>
            <div class="tasks-box-section status">{{ task.taskStatus }}</div>
            <div class="tasks-box-section actions">⚙️</div>
          </div>
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
const taskName = ref('Buy some bread')
const taskDescription = ref('Buy some tasty bulochek')
const taskStatus = ref('started')

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

function checkADDTASK() {
  const message = {
    type: 'insert_task',
    data: {
      userID: userID.value,
      listName: selectedListName.value,
      taskName: taskName.value,
      taskDescription: taskDescription.value,
      taskStatus: taskStatus.value
    }
  }
  SendMessageToBack(JSON.stringify(message))
}

function connectWebsocket() {
  socket.value = new WebSocket('ws://localhost:8080/ws')

  socket.value.onopen = () => {
    console.log('WebSocket connected')
    setTimeout(() => {
      checkLAT();
    }, 10);
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
        case 'insert_task_success':
          console.log(data);
          setTimeout(() => {
            checkLAT();
          }, 10);
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

.listnb-and-button {
  display: flex;
  justify-content: space-between;
  align-items: stretch;
  width: 100%;
  margin-bottom: 20px;
  height: 55px;
}

.list-name-box {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 99%;
  height: 100%;
  background: rgba(255, 255, 255, 0.3); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 2px solid rgba(255, 255, 255, 0.4); /* light border */
  border-radius: 10px;
}

.add-new-task-button {
  height: 100%;
  align-self: stretch;
  margin-left: 10px;
  background: rgba(33, 43, 255, 0.5); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 2px solid rgba(128, 135, 255, 0.7); /* light border */
  border-radius: 10px;

  cursor: pointer;
  transition: all .3s ease-in-out;
}

.add-new-task-button:hover{
  transform: scale(1.04);
  background: rgba(33, 43, 255, 0.6);
  border: 2px solid rgba(128, 135, 255, 0.8);
}

.add-new-task-button:active {
  background: rgba(33, 43, 255, 0.5);
  border: 2px solid rgba(128, 135, 255, 0.6);
  transition: .03s;
  transform: scale(0.98);
  outline: none;
}

.list-name {
  font-size: 35px;
  margin: 0;
}

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

.tasks ul {
  list-style-type: none;
  padding-left: 0;
  margin: 0;
}

.tasks-box {
  display: flex;
  align-items: stretch;
  justify-content: space-between;
  margin-bottom: 15px;
  padding-top: 6px;
  padding-bottom: 6px;
  width: 99%;
  height: auto;
  background: rgba(95, 102, 251, 0.3); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 2px solid rgba(115, 122, 253, 0.7); /* light border */
  border-radius: 10px;
}

.tasks-box-section {
  display: flex;
  padding: 10px;
  height: auto;
  border-right: 2px solid rgba(255, 255, 255, 0.4);
  white-space: normal;
  word-break: break-word;
  align-items: center;
}

.tasks-box-section:last-child {
  border-right: none;
}

.name {
  flex: 2;
  text-align: left;
}

.explanation {
  flex: 5.7;
  text-align: left;
}

.status {
  flex: 2;
  text-align: center;
}

.actions {
  flex: 0.3;
  text-align: center;
}

.list-button{
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  width: 99%;
  height: auto;
  background: rgba(95, 102, 251, 0.3); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 2px solid rgba(115, 122, 253, 0.7); /* light border */
  border-radius: 10px;

  cursor: pointer;
  transition: all .3s ease-in-out;
}

.list-button:hover {
  transform: scale(1.04);
  background: rgba(95, 102, 251, 0.4);
  border: 2px solid rgba(115, 122, 253, 0.8);
}

.list-button:active {
  background: rgba(73, 80, 255, 0.3);
  border: 2px solid rgba(115, 122, 253, 0.5);
  transition: .03s;
  transform: scale(0.98);
  outline: none;
}

.list-button,
.list-button:focus,
.list-button:focus-visible,
.list-button:active,
.list-button:hover,
.add-new-task-button,
.add-new-task-button:focus,
.add-new-task-button:focus-visible,
.add-new-task-button:active,
.add-new-task-button:hover {
  outline: none !important;
}

.lists-here {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  width: 99%;
  height: auto;
  background: rgba(255, 255, 255, 0.3); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 2px solid rgba(255, 255, 255, 0.4); /* light border */
  border-radius: 10px;
}

.text {
  font-size: 35px;
  margin: 0;
}

.top-panel-tm {
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