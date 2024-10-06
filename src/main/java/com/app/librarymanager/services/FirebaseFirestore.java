package com.app.librarymanager.services;


import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.json.JSONObject;

public class FirebaseFirestore {
  private static FirebaseFirestore instance;
  private final Firestore db = Firebase.getDb();

  public FirebaseFirestore() {

  }

  public static FirebaseFirestore getInstance() {
    if (instance == null) {
      instance = new FirebaseFirestore();
    }
    return instance;
  }



  public void addData(String collection, String document, Map<String, Object> data) {
    DocumentReference docRef = db.collection(collection).document(document);
    ApiFuture<WriteResult> result = docRef.set(data);
    try {
      System.out.println("Update time : " + result.get().getUpdateTime());
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  public JSONObject getData(String collection, String document) {
    DocumentReference docRef = db.collection(collection).document(document);
    ApiFuture<DocumentSnapshot> future = docRef.get();
    try {
      DocumentSnapshot documentSnapshot = future.get();
      if (documentSnapshot.exists()) {
        System.out.println("Document data: " + documentSnapshot.getData());
        return new JSONObject(documentSnapshot.getData());
      } else {
        System.out.println("No such document!");
        return null;
      }
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
  public JSONObject getCollection(String collection) {
    ApiFuture<QuerySnapshot> future = db.collection(collection).get();
    try {
      List<Map<String, Object>> list = new ArrayList<>();
      for (DocumentSnapshot document : future.get().getDocuments()) {
        System.out.println("Document data: " + document.getData());
        list.add(document.getData());
      }
      return new JSONObject(list);
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
  public Timestamp updateData(String collection, String document, Map<String, Object> data) {
    DocumentReference docRef = db.collection(collection).document(document);
    ApiFuture<WriteResult> result = docRef.update(data);
    try {
      System.out.println("Update time : " + result.get().getUpdateTime());
      return result.get().getUpdateTime();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  public void deleteData(String collection, String document) {
    DocumentReference docRef = db.collection(collection).document(document);
    ApiFuture<WriteResult> result = docRef.delete();
    try {
      System.out.println("Update time : " + result.get().getUpdateTime());
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

//  public static void main(String[] args) {
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    Map<String, Object> data = new HashMap<>();
//    data.put("name", "Los Angeles");
//    data.put("state", "CA");
//    data.put("country", "USA");
//    db.addData("cities", "LA", data);
//    JSONObject obj = db.getData("cities", "LA");
//    System.out.println(obj);
////    db.getCollection("cities");
//    data.put("name", "San Francisco");
//    db.updateData("cities", "LA", data);
////    db.deleteData("cities", "LA");
//    JSONObject col =  db.getCollection("cities");
//    System.out.println(col);
//  }


}
