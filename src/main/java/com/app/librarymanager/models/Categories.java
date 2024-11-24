package com.app.librarymanager.models;

import java.util.Date;
import lombok.Data;
import org.bson.Document;
import org.bson.types.ObjectId;

@Data
public class Categories {

  private ObjectId _id;
  private String name;
  private Date lastUpdated;

  public Categories() {
    _id = null;
    name = null;
    lastUpdated = null;
  }

  public Categories(String name) {
    this._id = null;
    this.name = name.toLowerCase();
    this.lastUpdated = null;
  }

  public Categories(ObjectId _id, String name, Date lastUpdated) {
    this._id = _id;
    this.name = name.toLowerCase();
    this.lastUpdated = lastUpdated;
  }

  public Categories(Document document) {
    this._id = document.getObjectId("_id");
    this.name = document.getString("name");
    this.lastUpdated = document.getDate("lastUpdated");
  }
}
