package com.iesa.dep.models;
public class User {
  public String id;
  public String name;
  public String document;
  public String phone;
  public String type;
  public User(){}
  public User(String name,String document,String phone,String type){
    this.name=name;this.document=document;this.phone=phone;this.type=type;
  }
}
