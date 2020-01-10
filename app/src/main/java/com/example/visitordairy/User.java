package com.example.visitordairy;

public class User {
    private String user_id,phone;
    int count;

    public User(String user_id, String phone, int count) {
        this.user_id = user_id;
        this.phone = phone;
        this.count = count;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
