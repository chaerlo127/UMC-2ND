package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserInfoRes {
    private String name;
    private String nickName;
    private String profileimgUrl;
    private String website;
    private String introduction;
    private int followerCount;
    private int followingCount;
    private int postCount;
}
