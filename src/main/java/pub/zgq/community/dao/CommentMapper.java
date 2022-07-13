package pub.zgq.community.dao;

import org.apache.ibatis.annotations.Mapper;
import pub.zgq.community.entity.Comment;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);
}
