import BaseClass from "../util/baseClass";
import axios from 'axios'

export default class CommentClient extends BaseClass {

 constructor(props = {}) {
        super();
        const methodsToBind = ['clientLoaded', 'createComment', 'getCommentsByTaskId', 'deleteCommentByCommentId']
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

    async createComment(taskId, username, commentBody) {
        try {
            const response = await this.client.post(`/comments/create`, {
               taskId: taskId,
               username: username,
               commentBody: commentBody
            });
            return response.data;
        } catch (error) {
            this.handleError();
        }
    }

    async getCommentsByTaskId(taskId) {
        try {
            const response = await this.client.get(`/comments/${taskId}`);
            return response.data;
        } catch (error) {
            this.handleError();
        }
    }

    async deleteCommentByCommentId(commentId) {
            try {
                const response = await this.client.delete(`/comments/${commentId}`);
            } catch (error) {
                this.handleError();
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
