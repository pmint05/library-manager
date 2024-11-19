package com.app.librarymanager.services;

import static com.mongodb.client.model.Filters.eq;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import io.github.cdimascio.dotenv.Dotenv;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.Document;
import java.util.Map;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;


public class MongoDB {

  private static MongoDB instance = null;

  private static final Dotenv dotenv = Dotenv.load();
  private static final String connectionString = dotenv.get("MONGODB_URI");
  private static final String databaseName = "library-manager";

  private MongoClient mongoClient = null;
  private MongoDatabase database = null;

  private MongoDB() {
    try {
      ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();
      MongoClientSettings settings = MongoClientSettings.builder()
          .applyConnectionString(new ConnectionString(connectionString)).serverApi(serverApi)
          .build();
      mongoClient = MongoClients.create(settings);
      database = mongoClient.getDatabase(databaseName);
    } catch (Exception e) {
      System.err.println("Error when connecting MongoDB: " + e.getMessage());
    }
  }


  public static MongoDB getInstance() {
    if (instance == null) {
      instance = new MongoDB();
    }
    return instance;
  }

  public MongoClient getMongoClient() {
    if (instance == null) {
      instance = new MongoDB();
    }
    return mongoClient;
  }

  public MongoDatabase getDatabase() {
    if (instance == null) {
      instance = new MongoDB();
    }
    return database;
  }

  public static <T> Map<String, Object> objectToMap(T object) {
    Gson gson = new Gson();
    return gson.fromJson(gson.toJson(object), new TypeToken<Map<String, Object>>() {
    }.getType());
  }

  public static <T> T jsonToObject(String json, Class<T> myClass) {
    Gson gson = new Gson();
    return gson.fromJson(json, myClass);
  }

  public static <T> T mapToObject(Map<String, Object> data, Class<T> myClass) {
    Gson gson = new Gson();
    JsonElement jsonElement = gson.toJsonTree(data);
    return gson.fromJson(jsonElement, myClass);
  }

  public boolean addToCollection(String collectionName, Map<String, Object> data) {
    try {
      MongoCollection<Document> collection = database.getCollection(collectionName);
      Document toInsert = new Document(data).append("_id", new ObjectId());
      InsertOneResult result = collection.insertOne(toInsert);
      System.out.println("Success! Inserted document id: " + result.getInsertedId());
      return true;
    } catch (Exception e) {
      System.err.println("Error when trying to add " + collectionName + e.getMessage());
      return false;
    }
  }

  public List<String> findAllObject(String collectionName, String criteriaName, String regex) {
    try {
      List<String> result = new ArrayList<>();
      MongoCollection<Document> collection = database.getCollection(collectionName);
      // i: intensive, which doesn't separate from lower and uppercase
      collection.find(Filters.regex(criteriaName, regex, "i")).forEach(document -> {
        result.add(document.toJson());
      });
      return result;
    } catch (Exception e) {
      System.err.println("Error when trying to find " + criteriaName + " match " + regex + " at "
          + collectionName);
      return null;
    }
  }

  public String findAnObject(String collectionName, String criteriaName, Object valueCriteria) {
    try {
      MongoCollection<Document> collection = database.getCollection(collectionName);
      Document result = collection.find(eq(criteriaName, valueCriteria)).first();
      if (result == null) {
        return null;
      }
      return result.toJson();
    } catch (Exception e) {
      System.err.println("Fail when finding: " + e.getMessage());
      return null;
    }
  }

  public String findAnObject(String collectionName, Map<String, Object> criteria) {
    try {
      MongoCollection<Document> collection = database.getCollection(collectionName);
      Bson filter = Filters.and(
          criteria.entrySet().stream().map(entry -> eq(entry.getKey(), entry.getValue()))
              .toArray(Bson[]::new));
      Document result = collection.find(filter).first();
      if (result != null) {
        return result.toJson();
      } else {
        return null;
      }
    } catch (Exception e) {
      System.err.println("Fail when finding: " + e.getMessage());
      return null;
    }
  }

  public boolean updateData(String collectionName, String idCriteria, Object valueCriteria,
      Map<String, Object> newObject) {
    List<Bson> updateList = new ArrayList<>(
        newObject.entrySet().stream().map(entry -> Updates.set(entry.getKey(), entry.getValue()))
            .toList());
    updateList.add(Updates.currentTimestamp("lastUpdated"));
    Bson updates = Updates.combine(updateList);
    MongoCollection<Document> collection = database.getCollection(collectionName);
    System.err.println(idCriteria + " " + valueCriteria);
    try {
      collection.updateOne(eq(idCriteria, valueCriteria), updates);
      return true;
    } catch (Exception e) {
      System.err.println("Fail when trying to update at " + collectionName + " " + e.getMessage());
      return false;
    }
  }

  public boolean deleteFromCollection(String collectionName, String criteriaName,
      Object valueCriteria) {
    try {
      MongoCollection<Document> collection = database.getCollection(collectionName);
      Bson query = eq(criteriaName, valueCriteria);
      collection.deleteOne(query);
      return true;
    } catch (Exception e) {
      System.err.println("Error when trying to delete " + criteriaName + " " + valueCriteria + ": "
          + e.getMessage());
      return false;
    }
  }

//  public boolean appendToArray(String collectionName, String fieldName, String nameArray,
//      Object newElement) {
//    try {
//      MongoCollection<Document> collection = database.getCollection(collectionName);
//      Bson updates = Updates.combine(Updates.push(nameArray, newElement),
//          Updates.currentTimestamp("lastUpdated"));
//      collection.updateOne(Filters.eq("id", fieldName), updates);
//      return true;
//    } catch (Exception e) {
//      System.err.println(
//          "Fail when pushing: " + newElement + " into " + nameArray + " at " + fieldName);
//      return false;
//    }
//  }
//
//  public boolean removeFromArray(String collectionName, String fieldName, String nameArray,
//      Object newElement) {
//    try {
//      MongoCollection<Document> collection = database.getCollection(collectionName);
//      Bson updates = Updates.combine(Updates.pull(nameArray, newElement),
//          Updates.currentTimestamp("lastUpdated"));
//      collection.updateOne(Filters.eq("id", fieldName), updates);
//      return true;
//    } catch (Exception e) {
//      System.err.println(
//          "Fail when pushing: " + newElement + " into " + nameArray + " at " + fieldName);
//      return false;
//    }
//  }
}
