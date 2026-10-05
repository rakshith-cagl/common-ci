 if( 'function' === typeof importScripts){
     importScripts('https://www.gstatic.com/firebasejs/7.6.1/firebase-app.js');
     importScripts('https://www.gstatic.com/firebasejs/7.6.1/firebase-messaging.js');
     this.registerFirebase();
   }

 function registerFirebase()
 {
firebase.initializeApp({
	'apiKey': 'FIREBASESWAPIKEY',
	'projectId': 'FIREBASEPROJECTID',
	'messagingSenderId': 'FIREBASEMESSAGINGSENDERID',
	'appId': 'FIREBASEAPPID'
});
  messaging = firebase.messaging();

  return messaging;
 }

