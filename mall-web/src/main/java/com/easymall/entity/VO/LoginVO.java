package com.easymall.entity.VO;

import lombok.Data;

@Data
public class LoginVO {
    private String userId;
    private String accessToken;
    private String username;
    private String nickname;
    private String avatar;

    public LoginVO(String userId, String accessToken, String username) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.username = username;
    }

    public LoginVO(String userId, String accessToken, String username, String nickname, String avatar) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
    }
}
