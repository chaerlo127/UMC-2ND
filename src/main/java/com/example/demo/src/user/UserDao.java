package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public GetUserInfoRes selectUserInfo(int userIdx){
        String getUserInfoQuery = "select u.userIdx as userIdx, u.name as name, u.nickName as nickName, u.profileimgUrl as profileimgUrl, u.introduction as introduction, u.website as website,\n" +
                "       IF(followerCount is null, 0, followerIdx) as followerCount,\n" +
                "       IF(followeeCount is null, 0, followingIdx) as followeeCount,\n" +
                "       IF(postCount is null, 0, postCount) as postCount\n" +
                "from User as u\n" +
                "         left JOIN (select count(postIdx) as postCount, userIdx\n" +
                "                    from Post\n" +
                "                    where status = 'ACTIVE'\n" +
                "                    group by userIdx) P on u.userIdx = P.userIdx\n" +
                "         left join (select count(followerIdx) as followerCount, followerIdx\n" +
                "                    from Follow\n" +
                "                    where status = 'ACTIVE'\n" +
                "                    group by followerIdx) F on F.followerIdx = u.userIdx\n" +
                "         left join (select count(followingIdx) as followeeCount, followingIdx\n" +
                "                    from Follow\n" +
                "                    where status = 'ACTIVE'\n" +
                "                    group by followingIdx) E on E.followingIdx = u.userIdx\n" +
                "where u.userIdx = ? and u.status = 'ACTIVE'";
        int selectUserInfoParam = userIdx;
        return this.jdbcTemplate.queryForObject(getUserInfoQuery,
                (rs,rowNum) -> new GetUserInfoRes(
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("profileimgUrl"),
                        rs.getString("website"),
                        rs.getString("introduction"),
                        rs.getInt("followerCount"),
                        rs.getInt("followeeCount"),
                        rs.getInt("postCount")
                ), selectUserInfoParam);
    }

/*    public GetUserFeedRes getUsersByEmail(String email){
        String getUsersByEmailQuery = "select userIdx,name,nickName,email from User where email=?";
        String getUsersByEmailParams = email;
        return this.jdbcTemplate.queryForObject(getUsersByEmailQuery,
                (rs, rowNum) -> new GetUserFeedRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email")),
                getUsersByEmailParams);
    }*/


    public List<GetUserPostRes> selectUserPosts(int userIdx){
        String selectUserPostsQuery = "select p.postIdx, pi.imgUrl as postImgUrl" +
                " from Post as p" +
                "         join User as U on p.userIdx = U.userIdx" +
                "         join PostImgUrl as pi on p.postIdx = pi.postIdx and pi.status = 'ACTIVE'" +
                " where p.status = 'ACTIVE' and U.userIdx = ?" +
                " group by p.postIdx" +
                " order by p.postIdx";
        int selectUserPostsParams = userIdx;
        return this.jdbcTemplate.query(selectUserPostsQuery,
                (rs, rowNum) -> new GetUserPostRes(
                        rs.getInt("postIdx"),
                        rs.getString("postImgUrl")),
                        selectUserPostsParams);
    }


    public GetUserRes getUsersByIdx(int userIdx){
        String getUsersByIdxQuery = "select userIdx,name,nickName,email from User where userIdx=?";
        int getUsersByIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUsersByIdxQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email")),
                getUsersByIdxParams);
    }


    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into User (name, nickName, email, pwd) VALUES (?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getName(), postUserReq.getNickName(), postUserReq.getEmail(), postUserReq.getPwd()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    public int checkUserExist(int userIdx){
        String checkUserQuery = "select exists(select userIdx from User where userIdx = ?)";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserQuery,
                int.class,
                checkUserExistParams);

    }

    public int modifyUserName(PatchUserReq patchUserReq){
        String modifyUserNameQuery = "update User set nickName = ? where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getNickName(), patchUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }


    public int deleteUser(DeleteUserReq deleteUserReq) {
        String deleteUserQuery = "delete from User where userIdx = ?";
        Object[] deleteUserParams = new Object[]{deleteUserReq.getUserIdx()};
        return this.jdbcTemplate.update(deleteUserQuery, deleteUserParams);
    }
}
