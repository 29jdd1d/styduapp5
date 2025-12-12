package com.studyapp.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyapp.community.entity.PostComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PostCommentMapper extends BaseMapper<PostComment> {

    @Update("UPDATE post_comment SET like_count = like_count + #{delta} WHERE id = #{id}")
    void updateLikeCount(@Param("id") Long id, @Param("delta") int delta);
}
