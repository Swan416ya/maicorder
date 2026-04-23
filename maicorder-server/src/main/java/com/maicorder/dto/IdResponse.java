package com.maicorder.dto;

import lombok.Data;

@Data
public class IdResponse {
    private String qq;
    private String username;
    private String friend_code;
    private String credentials;

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFriend_code() {
        return friend_code;
    }

    public void setFriend_code(String friend_code) {
        this.friend_code = friend_code;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }
}
