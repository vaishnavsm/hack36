package com.hack36.Models;



public class MessageModel {

    private String message;
    private boolean isUser;

    public MessageModel (String message,boolean f){
        this.message=message;
        this.isUser=f;
    }
    public MessageModel(){

    }

    public void setMessage(String message){
        this.message=message;
    }

    public void setIsUser(boolean f){
        this.isUser=f;
    }
    public String getMessage(){
        return this.message ;
    }
    public boolean getIsUser(){
       return this.isUser;
    }
}
