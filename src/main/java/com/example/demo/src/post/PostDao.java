package com.example.demo.src.post;


import com.example.demo.src.post.model.GetPostImgRes;
import com.example.demo.src.post.model.GetPostRes;
import com.example.demo.src.user.model.GetUserPostRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PostDao {
    private JdbcTemplate jdbcTemplate;
    private List<GetPostImgRes> getPostImgRes;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public List<GetPostRes> selectPosts(int userIdx) {
        String selectPostsQuery = "select p.postIdx as postIdx, u.useridx as userIdx, u.nickName, u.profileimgUrl as profileimgUrl, p.content as content,\n" +
                "       IF(postLikeCount is null, 0, postLikeCount) as postLikeCount,\n" +
                "       IF(commentCount is null, 0, postLikeCount) as commentCount,\n" +
                "       case when timestampdiff(second, p.updateAt, current_timestamp) < 60 then concat(timestampdiff(second, p.updateAt, current_timestamp), '초 전')\n" +
                "       when timestampdiff(minute, p.updateAt, current_timestamp) < 60 then concat(timestampdiff(minute, p.updateAt, current_timestamp), '분 전')\n" +
                "       when timestampdiff(hour, p.updateAt, current_timestamp) < 60 then concat(timestampdiff(hour, p.updateAt, current_timestamp), '시간 전')\n" +
                "       when timestampdiff(day, p.updateAt, current_timestamp) < 60 then concat(timestampdiff(day, p.updateAt, current_timestamp), '일 전')\n" +
                "       else timestampdiff(year, p.updateAt, current_timestamp) end as updatedAt,\n" +
                "       IF(pl.status = 'ACTIVE', 'Y', 'N') as likeOrNot\n" +
                "from Post p\n" +
                "join instagram.User u on p.userIdx = u.userIdx\n" +
                "left join (select postIdx, userIdx, count(postLike) as postLikeCount from PostLike WHERE status = 'ACTIVE' group by postIdx) plc on plc.postIdx = p.postIdx\n" +
                "left join (select postIdx, count(commentIdx) as commentCount from Comment WHERE status = 'ACTIVE' group by postIdx) c on c.postIdx = p.postIdx\n" +
                "left join Follow as f on f.followingIdx = p.userIdx and f.status = 'ACTIVE'\n" +
                "left join PostLike as pl on pl.userIdx = f.followerIdx and pl.postIdx = p.postIdx\n" +
                "WHERE f.followerIdx = ? and p.status = 'ACTIVE'\n" +
                "group by p.postIdx";
        int selectPostsParams = userIdx;
        return this.jdbcTemplate.query(selectPostsQuery,
                (rs, rowNum) -> new GetPostRes(
                        rs.getInt("postIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("profileimgUrl"),
                        rs.getString("content"),
                        rs.getInt("postLikeCount"),
                        rs.getInt("commentCount"),
                        rs.getString("updatedAt"),
                        rs.getString("likeOrNot"),
                        getPostImgRes = this.jdbcTemplate.query("select pi.postImgUrlIdx, pi.imgUrl\n" +
                                "from PostImgUrl as pi\n" +
                                "join Post p on pi.postIdx = p.postIdx\n" +
                                "where pi.status = 'ACTIVE' and p.postIdx = ?",
                                (rk, rownum) -> new GetPostImgRes(
                                        rk.getInt("postImgUrlIdx"),
                                        rk.getString("imgUrl")
                                ), rs.getInt("postIdx"))
                ),
                selectPostsParams);
    }

    public int checkUserExist(int userIdx){
        String checkUserQuery = "select exists(select userIdx from User where userIdx = ?)";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserQuery,
                int.class,
                checkUserExistParams);

    }
}
