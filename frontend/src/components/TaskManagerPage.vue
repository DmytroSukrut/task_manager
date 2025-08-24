<template>
  <div class="top-panel-tm">
    <h1>Task Manager</h1>
    <button @click="logOut">Log Out</button>
  </div>
  <div class="content">
    <div class="lists">
      <div class="lists-here">
        <h3 class="text">Your lists here</h3>
      </div>
      <button
          class="add-new-list-button"
          @click="showListPopup = true"
      >
        Add new list
      </button>
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
            @click="showTaskPopup = true"
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
  <div class="insert-list-popup"
       :class="{ open: showListPopup }"
       @click.self="showListPopup = false; clearNewListName()">
    <div class="in-list-popup">
      <button class="close-cross" @click="showListPopup = false; clearNewListName()">✖</button>

      <h2 class="insert-list-text">Insert new list</h2>

      <div class="list-name-info">
        <label for="listName" class="new-list-name-label">List name:</label>
        <a class="attention">List name must be different from those you have</a>
      </div>


      <input
        v-model="newListName"
        type="text"
        placeholder="Enter list name..."
        class="new-list-name-textbox"
        maxlength="50"
      >


      <button
          class="insert-list-button"
          @click="checkADDLIST(); showListPopup = false"
      >
        INSERT LIST
      </button>
    </div>
  </div>

  <div class="insert-task-popup"
       :class="{ open: showTaskPopup }"
       @click.self="showTaskPopup = false; clearNewTask()">
    <div class="in-task-popup">
      <button class="close-cross" @click="showTaskPopup = false; clearNewTask()">✖</button>

      <h2 class="insert-task-text">Insert new task</h2>

      <div class="task-name-info">
        <label for="taskName" class="new-task-name-label">Task name:</label>
      </div>

      <div>
        <input
            v-model="newTaskName"
            type="text"
            placeholder="Enter task name..."
            class="new-task-name-textbox"
            maxlength="100"
        >
      </div>

      <div class="task-name-info">
        <label for="taskName" class="new-task-name-label">Task description:</label>
      </div>

      <div>
        <textarea
            v-model="newTaskDescription"
            placeholder="Enter task description..."
            class="new-task-description-textbox"
            maxlength="1000"
        ></textarea>
        <p>{{ newTaskDescription.length }}/1000</p>

      </div>

      <div class="task-dropdown-info">
        <label for="taskName" class="new-task-name-label">Pick task status:</label>

        <div
            class="dropdown"
            @mouseenter="showDropDownMenu = true"
            @mouseleave="showDropDownMenu = false"
        >
          <button class="dropbtn">{{ newTaskStatusDisplay }}</button>
          <transition name="dd">
            <div class="dropdown-content" v-show="showDropDownMenu">
              <a @click.prevent="newTaskStatus = 'not_started'; newTaskStatusDisplay = 'To do'">To do</a>
              <a @click.prevent="newTaskStatus = 'started'; newTaskStatusDisplay = 'In progress'">In progress</a>
              <a @click.prevent="newTaskStatus = 'finished'; newTaskStatusDisplay = 'Finished'">Finished</a>
            </div>
          </transition>
        </div>

      </div>

      <button
          class="insert-task-button"
          @click="checkADDTASK(); showTaskPopup = false"
      >
        INSERT TASK
      </button>
    </div>
  </div>
</template>

<script setup>
import {onBeforeUnmount, onMounted, ref, computed} from 'vue'
import {useRouter} from 'vue-router'
const router = useRouter()
const socket = ref(null)

const prof = getProf()
const userID = ref(prof?.UserID)
const userName = ref(prof?.UserName)
const userEmail = ref(prof?.UserEmail)

const userLists = ref([])
const selectedListName = ref(null)
const newTaskName = ref('')
const newTaskDescription = ref('')
const newTaskStatus = ref('not_started')
const newTaskStatusDisplay = ref('to do')

const newListName = ref(null)

//popup code
const showListPopup = ref(false)
const showTaskPopup = ref(false)
const showDropDownMenu = ref(false)

onMounted(() => {
  if (userName.value === null || userName.value === undefined) {
    router.push('/login');
    return;
  }
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
  clearNewListName()
}

function checkADDTASK() {
  const name = (newTaskName.value ?? '').trim();
  if (!name) {
    alert("Min length is 1");
    return;
  }
  const message = {
    type: 'insert_task',
    data: {
      userID: userID.value,
      listName: selectedListName.value,
      taskName: newTaskName.value,
      taskDescription: newTaskDescription.value,
      taskStatus: newTaskStatus.value
    }
  }
  SendMessageToBack(JSON.stringify(message))
  clearNewTask()
}

function checkADDLIST() {
  const name = (newListName.value ?? '').trim();
  if (!name) {
    alert("Min length is 1");
    return;
  }
  const message = {
    type: 'insert_list',
    data: {
      userID: userID.value,
      listName: newListName.value
    }
  }
  SendMessageToBack(JSON.stringify(message))
}

function logOut() {
  socket.value?.close()
  localStorage.removeItem('prof')
  router.replace('/login')
}

function getProf() {
  try {
    return JSON.parse(localStorage.getItem('prof') || 'null')
  } catch (e) {
    return null
  }
}

function clearNewListName() {
  newListName.value = ''
}

function clearNewTask() {
  newTaskName.value = ''
  newTaskDescription.value = ''
  newTaskStatus.value = 'not_started'
  newTaskStatusDisplay.value = 'To do'
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
        case 'insert_list_success':
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

.insert-task-popup {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;

  backdrop-filter: blur(0px); /* blur */
  -webkit-backdrop-filter: blur(0px); /* blur for Safari */

  z-index: 2;
  pointer-events: none;
  opacity: 0;

  transition:
      opacity 0.3s ease-in-out,
      backdrop-filter 0.3s ease-in-out,
      -webkit-backdrop-filter 0.3s ease-in-out;
}

.insert-task-popup.open {
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  opacity: 1;
  pointer-events: auto;
}

.task-name-info{
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.task-dropdown-info {
  padding-bottom: 20px;
  display: flex;
  flex-direction: row;
  align-items: center;
}

.new-task-name-textbox{
  background: rgba(255, 255, 255, 0.3); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 2px solid rgba(255, 255, 255, 0.4); /* light border */
  border-radius: 5px;
  width: 100%;
  margin-top: 10px;
  margin-bottom: 20px;
  font-size: 25px;
}

.new-task-description-textbox{
  background: rgba(255, 255, 255, 0.3); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 2px solid rgba(255, 255, 255, 0.4); /* light border */
  border-radius: 5px;
  width: 100%;
  margin-top: 10px;
  margin-bottom: 20px;
  font-size: 25px;

  resize: vertical;
  min-height: 80px;
  max-height: 270px;

  overflow-y: auto;
}

.new-task-description-textbox::-webkit-scrollbar {
  width: 10px;
  background: rgba(255, 255, 255, 0.3); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 2px solid rgba(255, 255, 255, 0.4); /* light border */
  border-radius: 10px;
  cursor: pointer;
}

.new-task-description-textbox::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.3); /* прозорий чорний */
  border-radius: 10px;
  border: 2px solid rgba(255, 255, 255, 0.4); /* контур */
  cursor: pointer;

}

.dropbtn {
  width: 140px;
  font-size: 16px;

  background: rgba(33, 43, 255, 0.5); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 2px solid rgba(128, 135, 255, 0.7); /* light border */
  border-radius: 10px;

  cursor: pointer;
  transition: all .3s ease-in-out;
}

/* The container <div> - needed to position the dropdown content */
.dropdown {
  position: relative;
  display: inline-block;
  padding-left: 20px;

  transition: all .3s ease-in-out;
}

/* Dropdown Content (Hidden by Default) */
.dropdown-content {
  position: absolute;
  background: rgba(255, 255, 255, 0.7); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 1px solid rgba(255, 255, 255, 0.2); /* light border */
  border-radius: 10px;
  min-width: 140px;
  z-index: 20;

  cursor: pointer;
  transition: all .3s ease-in-out;
}

/* Links inside the dropdown */
.dropdown-content a {
  color: black;
  padding: 12px 16px;
  text-decoration: none;
  display: block;
  border: 2px;
  border-radius: 10px;
}

/* Change color of dropdown links on hover */
.dropdown-content a:hover {
  background: rgba(148, 189, 255, 0.7); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 2px solid rgba(0, 103, 147, 0.2); /* light border */
}

.dd-enter-active, .dd-leave-active {
  transition: opacity .18s ease, transform .22s ease;
  will-change: opacity, transform;
}

.dd-enter-from, .dd-leave-to {
  opacity: 0;
  transform: translateY(6px) scale(0.98);
}

/* Change the background color of the dropdown button when the dropdown content is shown */
.dropdown:hover .dropbtn {
  background: rgba(166, 33, 255, 0.5); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 2px solid rgba(128, 135, 255, 0.7); /* light border */
  border-radius: 10px;
}

.insert-task-button {
  background: rgba(255, 255, 255, 0.3); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 2px solid rgba(255, 255, 255, 0.4); /* light border */
  border-radius: 10px;

  cursor: pointer;
  transition: all .3s ease-in-out;
}

.insert-task-button:hover{
  transform: scale(1.04);
  background: rgba(255, 255, 255, 0.4);
  border: 2px solid rgba(255, 255, 255, 0.5);
}

.insert-task-button:active {
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid rgba(255, 255, 255, 0.2);
  transition: .02s;
  transform: scale(0.98);
  outline: none;
}

.insert-list-popup {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;

  backdrop-filter: blur(0px); /* blur */
  -webkit-backdrop-filter: blur(0px); /* blur for Safari */

  z-index: 2;
  pointer-events: none;
  opacity: 0;

  transition:
      opacity 0.3s ease-in-out,
      backdrop-filter 0.3s ease-in-out,
      -webkit-backdrop-filter 0.3s ease-in-out;
}

.insert-list-popup.open {
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  opacity: 1;
  pointer-events: auto;
}

.in-list-popup {
  position: relative;
  background: rgba(98, 105, 255, 0.9);
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 4px solid rgba(79, 91, 255, 0.9); /* light border */
  padding: 10px 20px;
  border-radius: 12px;
  flex-direction: column;
  width: 400px;
}

.in-task-popup {
  position: relative;
  background: rgba(98, 105, 255, 0.9);
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 4px solid rgba(79, 91, 255, 0.9); /* light border */
  padding: 10px 20px;
  border-radius: 12px;
  flex-direction: column;
  width: 600px;
}

.close-cross {
  position: absolute;
  z-index: 3;
  top: 8px;
  right: 8px;
  background: transparent;
  cursor: pointer;
  border: none;
  font-size: 15px;
  transition: 0.2s ease-in-out;
}

.close-cross:hover {
  transform: scale(1.3);
}

.insert-list-text {
  margin: 0;
  margin-bottom: 10px;
  font-size: 30px;
}

.new-list-name-textbox {
  background: rgba(255, 255, 255, 0.3); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 2px solid rgba(255, 255, 255, 0.4); /* light border */
  border-radius: 5px;
  width: 100%;
  margin-top: 10px;
  margin-bottom: 20px;
  font-size: 25px;
}

.new-list-name-label {
  font-size: 20px;
}

.attention {
  font-size: 15px;
  pointer-events: none;
  color: #ff5f5f;
}

.list-name-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.listnb-and-button {
  z-index: 10;
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

.insert-list-button {
  background: rgba(255, 255, 255, 0.3); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 2px solid rgba(255, 255, 255, 0.4); /* light border */
  border-radius: 10px;

  cursor: pointer;
  transition: all .3s ease-in-out;
}

.insert-list-button:hover{
  transform: scale(1.04);
  background: rgba(255, 255, 255, 0.4);
  border: 2px solid rgba(255, 255, 255, 0.5);
}

.insert-list-button:active {
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid rgba(255, 255, 255, 0.2);
  transition: .02s;
  transform: scale(0.98);
  outline: none;
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
  transition: .02s;
  transform: scale(0.98);
  outline: none;
}

.add-new-list-button {
  width: 100%;
  background: rgba(33, 43, 255, 0.5);
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 2px solid rgba(128, 135, 255, 0.7); /* light border */
  border-radius: 10px;

  cursor: pointer;
  transition: all .3s ease-in-out;
}

.add-new-list-button:hover{
  transform: scale(1.04);
  background: rgba(33, 43, 255, 0.6);
  border: 2px solid rgba(128, 135, 255, 0.8);
}

.add-new-list-button:active {
  background: rgba(33, 43, 255, 0.5);
  border: 2px solid rgba(128, 135, 255, 0.6);
  transition: .02s;
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
  position: relative;
  padding: 10px;
  background: rgba(255, 255, 255, 0.2); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 1px solid rgba(255, 255, 255, 0.2); /* light border */
  border-radius: 10px;

  overflow-y: auto;
}

.tasks::-webkit-scrollbar {
  width: 10px;
  background: rgba(255, 255, 255, 0.3); /* transparent white bg */
  backdrop-filter: blur(10px); /* blur */
  -webkit-backdrop-filter: blur(10px); /* blur for Safari */
  border: 2px solid rgba(255, 255, 255, 0.4); /* light border */
  border-radius: 10px;
  cursor: pointer;
}

.tasks::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.3); /* прозорий чорний */
  border-radius: 10px;
  border: 2px solid rgba(255, 255, 255, 0.4); /* контур */
  cursor: pointer;

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

  transition: all .1s ease-in-out;
}

.tasks-box:hover {
  background: rgba(44, 55, 255, 0.3);
  border: 2px solid rgba(97, 106, 255, 0.7);
  border-radius: 10px;
  outline: none !important;
  transform: scale(1.01);
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
  background: rgba(44, 55, 255, 0.3);
  border: 2px solid rgba(97, 106, 255, 0.7);
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
.add-new-task-button,close-cross
.add-new-task-button:focus,
.add-new-task-button:focus-visible,
.add-new-task-button:active,
.add-new-task-button:hover,
.add-new-list-button,
.add-new-list-button:focus,
.add-new-list-button:focus-visible,
.add-new-list-button:active,
.add-new-list-button:hover,
.close-cross,
.close-cross:focus,
.close-cross:focus-visible,
.close-cross:active,
.close-cross:hover,
.insert-list-button,
.insert-list-button:focus,
.insert-list-button:focus-visible,
.insert-list-button:active,
.insert-list-button:hover {
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