import BaseClass from "../util/baseClass";
import axios from 'axios'

export default class TaskClient extends BaseClass {

 constructor(props = {}) {
        super();
        const methodsToBind = ['clientLoaded', 'createTask', 'updateTask', 'getTasksByUsername'];
        this.bindClassMethods(methodsToBind, this);
        this.props = props;
        this.clientLoaded(axios);
    }

    clientLoaded(client) {
        this.client = client;
        if (this.props.hasOwnProperty("onReady")) {
            this.props.onReady();
        }
    }

    async createTask(username, title, description, collaborators) {
        try {
            const response = await this.client.post(`/tasks`, {
                creatorUsername: username,
                title: title,
                description: description,
                collaborators: collaborators
            });
            return response.data;
        } catch (error) {
            this.handleError();
        }
    }

    async updateTask(creatorUsername, taskId, description, title, collaborators, status) {
        console.log("called updateTask");
        try {
            const response = await this.client.put(`/tasks`, {
                creatorUsername: creatorUsername,
                taskId: taskId,
                description: description,
                title: title,
                collaborators: collaborators,
                status: status
            });
            return response.data;
        } catch (error) {
            this.handleError();
        }
    }

    async getTasksByUsername(username){
        try {
            return await this.client.get(`/tasks/user/${username}`);
        } catch (e){
            this.handleError();
        }
    }

    async deleteTaskById(taskId){
        try {
            await this.client.delete(`/tasks/${taskId}`);

        } catch (e){
            this.handleError(this.deleteTaskById(), e, null);
        }

    }

    handleError(method, error, errorCallback) {
        console.error(method + " failed - " + error);
        if (error.response.data.message !== undefined) {
            console.error(error.response.data.message);
        }
        if (errorCallback) {
            errorCallback(method + " failed - " + error);
        }
    }
}