package com.app.librarymanager.services;

import com.app.librarymanager.utils.Fetcher;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.FirestoreClient;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import org.json.JSONObject;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;

public class Firebase {

  private static Firebase instance;
  private static final Dotenv dotenv = Dotenv.load();
  private static final String apiKey = dotenv.get("FIREBASE_API_KEY");
  private static final String authDomain = dotenv.get("FIREBASE_AUTH_DOMAIN");
  private static final String databaseURL = dotenv.get("FIREBASE_DATABASE_URL");
  private static final String projectId = dotenv.get("FIREBASE_PROJECT_ID");
  private static final String storageBucket = dotenv.get("FIREBASE_STORAGE_BUCKET");
  private static final String messagingSenderId = dotenv.get("FIREBASE_MESSAGING_SENDER_ID");
  private static final String appId = dotenv.get("FIREBASE_APP_ID");

  private FirebaseApp app;
  private Firestore db;

  private Firebase() {
    Dotenv dotenv = Dotenv.load();
    try {
      FileInputStream serviceAccount = new FileInputStream(
          Objects.requireNonNull(dotenv.get("FIREBASE_SERVICE_ACCOUNT_PATH")));
      FirebaseOptions options = new FirebaseOptions.Builder()
          .setCredentials(GoogleCredentials.fromStream(serviceAccount))
          .setDatabaseUrl(databaseURL)
          .build();
      app = FirebaseApp.initializeApp(options);
      db = FirestoreClient.getFirestore();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Firebase getInstance() {
    if (instance == null || instance.app == null) {
      instance = new Firebase();
    }
    return instance;
  }

  public static FirebaseApp getApp() {
    if (instance == null || instance.app == null) {
      instance = new Firebase();
    }
    return instance.app;
  }

  public static Firestore getDb() {
    if (instance == null || instance.app == null) {
      instance = new Firebase();
    }
    return instance.db;
  }

  public static void login(String email, String password) {
    String url =
        "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;
    String body = "{\n"
        + "  \"email\": \"" + email + "\",\n"
        + "  \"password\": \"" + password + "\",\n"
        + "  \"returnSecureToken\": true\n"
        + "}";
    JSONObject response = Fetcher.post(url, body);
    System.out.println(response);
    if (response == null) {
      System.out.println("Login Failed");
      return;
    }
    if (response.has("error")) {
      System.out.println(response.get("error"));
    } else {
      System.out.println("Login Successful");
      String idToken = response.getString("idToken");
      try {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String uid = decodedToken.getUid();

        System.out.println("UID: " + uid);

      } catch (FirebaseAuthException e) {
        throw new RuntimeException(e);
      }
    }
  }
}