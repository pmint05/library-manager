package com.app.librarymanager.services;


import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import org.json.JSONArray;
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

  public ArrayList<Map<String, Object>> getCollection(String collection) {
    ApiFuture<QuerySnapshot> future = db.collection(collection).get();
    try {
      ArrayList<Map<String, Object>> list = new ArrayList<>();
      for (DocumentSnapshot document : future.get().getDocuments()) {
//        document.getData().get("name")
//        System.out.println("Document data: " + document.getId() + document.getData().get("name"));
//        System.out.println("Document data: " + document.getData());
        list.add(document.getData());
      }
      return list;
//      System.out.println(list);
//      return new JSONArray(list);
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  public Map<String, Object> getDocumentObject(String collection, String document) {
    try {
      DocumentReference docRef = db.collection(collection).document(document);
      ApiFuture<DocumentSnapshot> future = docRef.get();
      DocumentSnapshot docSnap = future.get();
      if (docSnap.exists()) {
        return docSnap.getData();
      } else {
        throw new Exception("Document " + document + " doesn't exists in collection " + collection);
//        System.out.println("No such document!");
      }
    } catch (Exception e) {
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

  public boolean haveDocument(String collection, String document) {
    try {
      return db.collection(collection).document(document).get().get().exists();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public JSONArray getDataWithFilter(String collection, String nameField, Object valueField) {
    try {
      // Create a reference to the cities collection
      CollectionReference cities = db.collection(collection);
      // Create a query against the collection.
      Query query = cities.whereEqualTo(nameField, valueField);
      // retrieve  query results asynchronously using query.get()
      ApiFuture<QuerySnapshot> querySnapshot = query.get();

      List<QueryDocumentSnapshot> listSnapshot = querySnapshot.get().getDocuments();
      List<Map<String, Object>> list = new ArrayList<>();

      for (DocumentSnapshot document : listSnapshot) {
//        System.out.println(document.getId());
        list.add(document.getData());
      }

      return new JSONArray(list);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // collection -> document -> value[nameField] equals to valueField
  public String findDocumentWithField(String collection, String nameField, String valueField) {
    try {
      // Create a reference to the cities collection
      CollectionReference cities = db.collection(collection);
      // Create a query against the collection.
      Query query = cities.whereEqualTo(nameField, valueField);
      // retrieve  query results asynchronously using query.get()
      ApiFuture<QuerySnapshot> querySnapshot = query.get();

      List<QueryDocumentSnapshot> listSnapshot = querySnapshot.get().getDocuments();

      if (listSnapshot.size() != 1) {
        throw new Exception(
            "Collection " + collection + " has zero or more than two " + nameField + " equal to "
                + valueField);
      }

      return listSnapshot.get(0).getId();
    } catch (Exception e) {
      e.printStackTrace();
      return "N/A";
    }
//    ApiFuture<QuerySnapshot> future = db.collection(collection).get();
//    try {
//      String document = "N/A";
//      for (DocumentSnapshot currentDocument : future.get().getDocuments()) {
//        if (currentDocument.get(nameField) == null) {
//          continue;
//        }
//        if (Objects.equals(currentDocument.get(nameField), valueField)) {
//          if (!document.equals("N/A")) {
//            throw new Exception("Exists multiple document with value[" + nameField + "] = " + valueField);
//          }
//          document = currentDocument.getId();
//        }
//      }
//      return document;
//    } catch (Exception e) {
//      e.printStackTrace();
//      return "";
//    }
  }

//  public void addNewArray() {
//
//  }

  public void appendToArray(String collection, String document, String nameArray,
      String newElement) {
    try {
      DocumentReference docRef = db.collection(collection).document(document);
      ApiFuture<WriteResult> arrayUnion = docRef.update(nameArray,
          FieldValue.arrayUnion(newElement));
      System.out.println("Update time : " + arrayUnion.get());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void removeFromArray(String collection, String document, String nameArray,
      String newElement) {
    try {
      DocumentReference docRef = db.collection(collection).document(document);
      ApiFuture<WriteResult> arrayRemove = docRef.update(nameArray,
          FieldValue.arrayRemove(newElement));
      System.out.println("Update time : " + arrayRemove.get());
    } catch (Exception e) {
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
