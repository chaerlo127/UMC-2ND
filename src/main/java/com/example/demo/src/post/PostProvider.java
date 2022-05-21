package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.src.post.model.GetPostRes;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.model.GetUserFeedRes;
import com.example.demo.src.user.model.GetUserInfoRes;
import com.example.demo.src.user.model.GetUserPostRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.USERS_EMPTY_USER_ID;

@Service
public class PostProvider {

    private final PostDao postDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PostProvider(PostDao postDao, JwtService jwtService) {
        this.postDao = postDao;
        this.jwtService = jwtService;
    }

    public List<GetPostRes> retrievePosts(int userIdx) throws BaseException {
        Boolean isMyFeed = true;
        if(checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        try{
            List<GetPostRes> getPosts = postDao.selectPosts(userIdx);
            return getPosts;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUserExist(int userIdx) throws BaseException{
        try{
            return postDao.checkUserExist(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
