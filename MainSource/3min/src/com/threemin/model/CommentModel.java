package com.threemin.model;

public class CommentModel {
    
    private int id;
    private String content;
    private UserModel user;
    private long created_at;
    private long updated_at;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public UserModel getUser() {
        return user;
    }
    public void setUser(UserModel user) {
        this.user = user;
    }
    public long getCreated_at() {
        return created_at;
    }
    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }
    public long getUpdated_at() {
        return updated_at;
    }
    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }
    
    
}
