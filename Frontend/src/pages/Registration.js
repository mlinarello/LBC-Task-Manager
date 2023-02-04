import BaseClass from "../util/baseClass";
import DataStore from "../util/DataStore";
import UserClient from "../api/UserClient";

class Registration extends BaseClass {
    constructor() {
        super();
        this.bindClassMethods(['createAccount'], this);
        this.dataStore = new DataStore();
    }

    async mount() {

        let registrationForm = document.getElementById('registration-form');
        if (registrationForm) {
            registrationForm.addEventListener('submit', this.createAccount)
        }
        this.client = new UserClient();
        // this.dataStore.addChangeListener(this.renderExample);
    }

    async createAccount(event) {
        let registrationForm = document.getElementById('registration-form');
        event.preventDefault();
        let name = document.getElementById('name').value;
        let username = document.getElementById('form-username').value;
        let password = document.getElementById('form-password').value;
        let passwordConfirm = document.getElementById('form-password-repeat').value;
        let createdAccount = null;
        if (password === passwordConfirm) {
                createdAccount = await this.client.createAccount(name, username, password);
                this.dataStore.set("account", createdAccount);
            if (createdAccount) {
                console.log(createdAccount);
            }
                if (createdAccount.data.username && createdAccount.data.username !== "UsernameTaken") {
                    alert(`Created ${createdAccount.data.username}`);
                    window.location.replace("/login.html")
                } else{
                    alert("Username already taken, be more creative!");
                }
        } else {
            alert("Passwords do not match");
        }
        registrationForm.reset();
    }
}

const main = async () => {
    const registration = new Registration();
    registration.mount();
};
window.addEventListener('DOMContentLoaded', main);