const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

//Topic
exports.sendNotificacionAndroid = functions.firestore.document("Notas/{docId}/{notas}/{notasId}").onCreate(
    (snapshot, context) => {

        admin.messaging().sendToTopic(
            snapshot.data().topic, 
            {
                notification: {
                    title: "Te enviaron una nota ❤️",
                    body: "Pulsa para entrar en la aplicación"
                }
            }
        );
    }
)

