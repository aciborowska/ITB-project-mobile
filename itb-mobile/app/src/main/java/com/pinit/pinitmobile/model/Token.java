package com.pinit.pinitmobile.model;

public class Token {
    public String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public long getUserId(){
        return Long.parseLong(token.substring(token.indexOf("_")+1,token.length()));
    }
}
