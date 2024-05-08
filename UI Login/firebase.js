// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
import firebase from 'firebase';
const firebaseConfig = {
  apiKey: "AIzaSyDGHkD7T7Ap6oyY6e2YBM9mO8bNfv33ArI",
  authDomain: "test-rw-e68c2.firebaseapp.com",
  projectId: "test-rw-e68c2",
  storageBucket: "test-rw-e68c2.appspot.com",
  messagingSenderId: "1077268288759",
  appId: "1:1077268288759:web:d5cb7e25e39f2b55cf2ff2",
  measurementId: "G-B691QS2HG2"
};


const firebaseApp = firebase.initializeApp(firebaseConfig);
const db = firebaseApp.firestore();
const auth = firebase.auth();
const provider = new firebase.auth.GoogleAuthProvider();

export {auth, provider};
export default db;
// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);