package com.studyapp.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyapp.community.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PostMapper extends BaseMapper<Post> {

    @Update("UPDATE post SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);

    @Update("UPDATE post SET like_count = like_count + #{delta} WHERE id = #{id}")
    void updateLikeCount(@Param("id") Long id, @Param("delta") int delta);

    @Update("UPDATE post SET comment_count = comment_count + #{delta} WHERE id = #{id}")
    void updateCommentCount(@Param("id") Long id, @Param("delta") int delta);
}
