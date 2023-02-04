import BaseClass from "../util/baseClass";
import axios from 'axios'

export default class UserClient extends BaseClass {

    constructor(props = {}) {
        super();
        const methodsToBind = ['clientLoaded', 'createAccount', 'login'];
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

    async createAccount(name, username, password) {
        try {
            const response = await this.client.post(`/users/registration`, {
                "name": name,
                "username": username,
                "password": password
            });
            return response;
        } catch (error) {

        }
    }

    async login(username, password) {
        try {
            const response = await this.client.post(`/users/login`, {
                username: username,
                password: password
            });
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