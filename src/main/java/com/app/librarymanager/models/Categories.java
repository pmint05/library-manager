package com.app.librarymanager.models;

import com.app.librarymanager.utils.StringUtil;
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
    this.name = StringUtil.toCapitalize(name);
    this.lastUpdated = null;
  }

  public Categories(ObjectId _id, String name, Date lastUpdated) {
    this._id = _id;
    this.name = StringUtil.toCapitalize(name);
    this.lastUpdated = lastUpdated;
  }

  public Categories(Document document) {
    this._id = document.getObjectId("_id");
    this.name = document.getString("name");
    this.lastUpdated = document.getDate("lastUpdated");
  }

  public Document toDocument() {
    return new Document("name", name);
  }
}
