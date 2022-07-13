package pub.zgq.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;
import pub.zgq.community.dao.CommentMapper;
import pub.zgq.community.entity.Comment;
import pub.zgq.community.util.CommunityConstant;
import pub.zgq.community.util.SensitiveFilter;

import java.util.List;

/**
 * @Author 孑然
 */
@SuppressWarnings("all")
@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit ) {
        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 添加评论
        // 转义html
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        // 过滤
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);

        // 更新对应帖子的评论数量
        // 判断当前comment是否为帖子（是评论则不需要更新）
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            // 查询最新评论数量
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            // 更新
            discussPostService.updateCommentCount(comment.getEntityId(), count);

        }

        return rows;
    }
}
