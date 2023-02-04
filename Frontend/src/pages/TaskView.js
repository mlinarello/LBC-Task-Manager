import BaseClass from "../util/baseClass";
import DataStore from "../util/DataStore";
import CommentClient from "../api/CommentClient";

class TaskView extends BaseClass {
    constructor() {
        super();
        this.bindClassMethods(['renderComments', 'createComment'], this);
        this.dataStore = new DataStore();
    }

    async mount(){
        this.client = new CommentClient();
        await this.renderComments();
        let createCommentForm = document.getElementById("create-comment-form");
        if (createCommentForm) {
            createCommentForm.addEventListener('submit', this.createComment);
        }
        document.getElementById("task-view-page-title").innerText = sessionStorage.getItem("taskTitle");
    }


    async renderComments(){
        let taskId = sessionStorage.getItem("taskId");
        let taskTitle = sessionStorage.getItem("taskTitle");
        let taskBody = sessionStorage.getItem("taskBody");
        let taskStatus = sessionStorage.getItem("taskStatus");

        let comments = "";
        let commentResponse = null;
        try {
            commentResponse = await this.client.getCommentsByTaskId(taskId);
            for (let i = 0; i < commentResponse.length; i++) {
                comments += `
            <a href="#" class="list-group-item list-group-item-action" aria-current="true">
            <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1">${commentResponse[i].username} </h5>
                </div>
                <p class="mb-1">${commentResponse[i].commentBody} </p>
                </a> <button class="btn btn-danger" id="delete-comment-button-${i}" style="">Delete Comment</button>
                <hr>`;
            }
            console.log(commentResponse);
        } catch (error) {
            console.log("Error in RenderComments");
        }

        document.getElementById("comment-render-area").innerHTML = comments;

        document.getElementById("render-header").innerHTML = `<h1>${taskTitle}</h1>
        <h2>${taskBody}</h2>
        <h4>Status:${taskStatus}</h4>`;
        //Event Listeners for delete comment button
        for(let j = 0; j < commentResponse.length; j++){
            document.getElementById( `delete-comment-button-${j}`).addEventListener('click', () => {
                this.client.deleteCommentByCommentId(commentResponse[j].commentId);
                setTimeout(() => {
                    this.renderComments();
                    window.location.reload();
                }, 1000);
            });

        }
    }


    async createComment(){
        let taskId = sessionStorage.getItem("taskId");
        let taskTitle = sessionStorage.getItem("taskTitle");
        let username = sessionStorage.getItem("username");
        let body = document.getElementById("create-comment-description").value;
        await this.client.createComment(taskId, username, body);

        setTimeout(() => {
            window.location.reload();
        }, 1200);
    }
}
const main = async () => {
    const tv = new TaskView();
    tv.mount();
}
window.addEventListener('DOMContentLoaded', main);


// <a href="#" className="list-group-item list-group-item-action" aria-current="true">
//     <div className="d-flex w-100 justify-content-between">
//         <h5 className="mb-1">${commentResponse[i].username} < /h5>
//         <small>3 days ago | 10:38am</small>
//     </div>
//     <p className="mb-1">I ran into an issue with range key in dynamodb, anyone else experiencing this?</p>
//     <small>Team engineer</small>
// </a>