package com.example.demo.src.post.model;


import com.example.demo.src.user.model.GetUserInfoRes;
import com.example.demo.src.user.model.GetUserPostRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetPostImgRes {
    private boolean _isMyFeed;
    private GetUserInfoRes getUserInfo;
    private List<GetUserPostRes> getUserPosts;
}
