package com.iesa.dep.config;

import java.io.FileInputStream;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class FirestoreConfig {

    @PostConstruct
    public void init() throws IOException {
        String credPath = System.getenv("FIREBASE_CREDENTIALS_PATH");
        if(credPath==null || credPath.isBlank()){
            System.out.println("FIREBASE_CREDENTIALS_PATH no está configurado. Conexión a Firestore deshabilitada.");
            return;
        }
        FileInputStream serviceAccount = new FileInputStream(credPath);
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        try {
            FirebaseApp.initializeApp(options);
            System.out.println("Firebase inicializado");
        } catch (IllegalStateException ex) {
            System.out.println("Firebase ya estaba inicializado");
        }
    }
}
