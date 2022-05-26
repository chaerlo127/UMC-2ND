package com.example.demo.src.post;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.post.model.GetPostRes;
import com.example.demo.src.post.model.PatchPostReq;
import com.example.demo.src.post.model.PostPostReq;
import com.example.demo.src.post.model.PostPostRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final PostProvider postProvider;
    @Autowired
    private final PostService postService;
    @Autowired
    private final JwtService jwtService;




    public PostController(PostProvider postProvider, PostService postService, JwtService jwtService){
        this.postProvider = postProvider;
        this.postService = postService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<List<GetPostRes>> getPosts(@RequestParam int userIdx) {
        try{

            List<GetPostRes> getPostRes = postProvider.retrievePosts(userIdx);
            return new BaseResponse<>(getPostRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("") // (GET) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<PostPostRes> createPosts(@RequestBody PostPostReq postPostReq) {
        try{
            if(postPostReq.getContent().length()>450){
                return new BaseResponse<>(BaseResponseStatus.POST_POSTS_INVALID_CONTENTS);
            }
            if(postPostReq.getPostImgUrls().size()<1){
                return new BaseResponse<>(BaseResponseStatus.POST_POSTS_EMPTY_IMGRL);
            }
            PostPostRes getPostRes = postService.createPosts(postPostReq.getUserIdx(), postPostReq);
            return new BaseResponse<>(getPostRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    @ResponseBody
    @PatchMapping("/{postIdx}") // (GET) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<String> modifyPost(@PathVariable int postIdx, @RequestBody PatchPostReq patchPostReq) {
        try{
            if(patchPostReq.getContent().length()>450){
                return new BaseResponse<>(BaseResponseStatus.POST_POSTS_INVALID_CONTENTS);
            }

            postService.modifyPost(patchPostReq.getUserIdx(), postIdx, patchPostReq);
            String result = "게시물 수정을 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping("/{postIdx}/status") // (GET) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<String> deletePost(@PathVariable int postIdx) {
        try{
            postService.deletePost(postIdx);
            String result = "게시물 삭제를 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
