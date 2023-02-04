import BaseClass from "../util/baseClass";
import DataStore from "../util/DataStore";
import TaskClient from "../api/TaskClient";

class HomePage extends BaseClass {

    constructor() {
        super();
        this.bindClassMethods(['renderTasks', 'createTask'], this);
        this.dataStore = new DataStore();
    }

    async mount() {
        this.client = new TaskClient();
        await this.renderTasks();
        let createTaskForm = document.getElementById('create-task-form');
        if (createTaskForm) {
            createTaskForm.addEventListener('submit', this.createTask);
        }
    }


    async renderTasks() {
        let username = sessionStorage.getItem("username");
        let listItems = "";
        let tasks = null;
        try {
            tasks = await this.client.getTasksByUsername(username);
            //Generating tasks html
            for (let i = 0; i < tasks.data.length; i++) {
                listItems += `<div class="accordion-item">
            <h2 class="accordion-header" id="headingOne" >
                <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseOne"
                    aria-expanded="true" aria-controls="collapseOne">
                    <strong><h4>${tasks.data[i].title}</h4></strong> 
                </button>
                </h2>
                <div id="collapseOne" class="accordion-collapse collapse show" aria-labelledby="headingOne"
                data-bs-parent="#accordionExample">
                <div class="accordion-body">
                <div class="task-dropdown-top">
                <strong> ${tasks.data[i].collaborators} </strong>
                <strong>Status: <p id="status"> ${tasks.data[i].status} </p> </strong>
                <a href="taskView.html"><button type="button" id="task-button-series-${i}" name="${tasks.data[i].taskId}" class="btn btn-info taskview-button">View
                Comments</button></a>
                <a href=""><button type="button" id="task-delete-${i}" class="btn btn-danger">Delete Task</button></a>
                </div>
                <hr>
                <div class="taskbody">
                <p> ${tasks.data[i].description} </p>
                <button type="button" id="task-submit-${i}" name="${tasks.data[i].taskId}" class="btn btn-success taskSubmitButton">Submit as complete</button>
                </div>
                </div>
                </div>
                </div>`;
            }

        } catch (error) {
            alert("If you're seeing me, there was an issue rendering tasks :(")
        }
        // let buttons = document.getElementsByClassName("")
        if (username == null) {
            listItems = "<h4 style='color: antiquewhite'>You don't appear to be logged in!</h4>";
            document.getElementById("create-task-form").hidden = true;
        } else {
            document.getElementById("welcome-renderer").innerHTML = `<h2>Welcome ${username}!</h2>
                                                                               <button id="logout-button" class="btn btn-dark"><h5>Logout</h5></button>`;
            try {
                let logout = document.getElementById("logout-button").addEventListener('click', () => {
                    sessionStorage.clear();
                    window.location.replace("/index.html");
                });
            } catch (event) {
                console.log(event);
            }

        }
        let renderArea = document.getElementById("task-renderer");
        renderArea.innerHTML = listItems;
        //Event listeners for buttons that bring you to individual task view
        try {
            for (let j = 0; j < tasks.data.length; j++) {
                let button = document.getElementById(`task-button-series-${j}`);
                button.addEventListener('click', function () {
                    let taskId = button.getAttribute("name");
                    sessionStorage.setItem("taskId", taskId);
                    sessionStorage.setItem("taskTitle", tasks.data[j].title);
                    sessionStorage.setItem("taskBody", tasks.data[j].description);
                    sessionStorage.setItem("taskStatus", tasks.data[j].status);
                });
            }
        } catch (event) {
            console.log(event);
        }

        //Event Listeners for submit as complete to change status to complete
        for (let k = 0; k < tasks.data.length; k++) {
            let button = document.getElementById(`task-submit-${k}`);
            button.addEventListener('click', () => {
                this.client.updateTask(tasks.data[k].creatorUsername, tasks.data[k].taskId, tasks.data[k].description,
                    tasks.data[k].title, tasks.data[k].collaborators, "COMPLETE");
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            });
        }

        //Event listeners for delete task button
        for (let d = 0; d < tasks.data.length; d++) {
            let button = document.getElementById(`task-delete-${d}`);
            button.addEventListener('click', () => {
                this.client.deleteTaskById(tasks.data[d].taskId);
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
                alert(`Task deleted: ${tasks.data[d].title}`);
            })
        }

    }

    async createTask(event) {
        event.preventDefault();
        const title = document.getElementById("create-task-title").value;
        const collaborators = document.getElementById("create-task-collaborators").value;
        const description = document.getElementById("create-task-description").value;
        const username = sessionStorage.getItem("username");
        const newTask = await this.client.createTask(username, title, description, collaborators);
        setTimeout(() => {
            window.location.reload();
            this.renderTasks();
        }, 1000);
    }
}

const main = async () => {
    const homePage = new HomePage();
    homePage.mount();
}
window.addEventListener('DOMContentLoaded', main);

