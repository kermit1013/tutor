package com.gtalent.tutor.responses;

import com.gtalent.tutor.models.User;

public class GetUserResponse {
    private int id;
    private String username;

    public GetUserResponse() {
    }

    public GetUserResponse(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public GetUserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }




}
