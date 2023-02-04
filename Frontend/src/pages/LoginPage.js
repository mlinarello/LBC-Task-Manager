import BaseClass from "../util/baseClass";
import DataStore from "../util/DataStore";
import UserClient from "../api/UserClient";

class LoginPage extends BaseClass {

    constructor() {
        super();
        this.bindClassMethods(['login'], this);
        this.dataStore = new DataStore();
    }

    async mount(){
        let loginForm = document.getElementById('login-input');
        if (loginForm) {
            loginForm.addEventListener('submit', this.login);
        }
        this.client = new UserClient();
    }

    async login(event){
        let loginForm = document.getElementById('login-input');
        event.preventDefault();
        let username = document.getElementById('username-input').value;
        let password = document.getElementById('password-input').value;

        // let userLogin = await this.client.login(username, password);
        let userLogin = new Promise((resolve, reject) => {
            const login = this.client.login(username, password);
            console.log(login.data)
            if(login){
                sessionStorage.setItem("username", username);
                window.location.replace("homePage.html");
                resolve(`${login.data}`);
            }else{
                reject("Didn't work");
            }
        });
        this.dataStore.set("user", userLogin);
        loginForm.reset();
    }
}

const main = async () => {
    const login = new LoginPage();
    login.mount();
}
window.addEventListener('DOMContentLoaded', main);