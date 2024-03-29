// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getStorage } from "firebase/storage"
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
const firebaseConfig = {
    apiKey: process.env.REACT_APP_API_KEY,
    authDomain: "datn-d6745.firebaseapp.com",
    projectId: "datn-d6745",
    storageBucket: "datn-d6745.appspot.com",
    messagingSenderId: "489250973513",
    appId: "1:489250973513:web:1323f07cd91b37f8c5dc98"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
export const storage = getStorage(app)