package com.threemin.model;

public class FeedbackModel {

    /**
     * In this model, product model and user model don't have all field { "id":
     * 1, "content": "nice", "status": "positive", "product": { "id": 277,
     * "name": "phich cam" }, "user": { "id": 149, "full_name":
     * "Trung Trực Trần", "avatar":
     * "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg?sz=50"
     * } }
     * */

    private int id;
    private String content;
    private String status;
    private ProductModel product;
    private UserModel user;
    private long update_time;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ProductModel getProduct() {
        return product;
    }

    public void setProduct(ProductModel product) {
        this.product = product;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }
    
    

}
