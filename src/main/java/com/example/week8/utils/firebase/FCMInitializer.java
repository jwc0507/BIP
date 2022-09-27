//package com.example.week8.utils.firebase;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//
//@Slf4j
//@Service
//public class FCMInitializer {
//
//    private static final Logger logger = LoggerFactory.getLogger(FCMInitializer.class);
//    private static final String FIREBASE_CONFIG_PATH = "event-scheduler-4736e-firebase-adminsdk-1atyo-5249f2d5a7.json";
//
//    @PostConstruct
//    public void initialize() {
//        try {
//            FirebaseOptions options = new FirebaseOptions.Builder()
//                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream())).build();
//            if (FirebaseApp.getApps().isEmpty()) {
//                FirebaseApp.initializeApp(options);
//                logger.info("Firebase application has been initialized");
//            }
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        }
//    }
//
//}