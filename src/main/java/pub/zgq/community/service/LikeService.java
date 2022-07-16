package pub.zgq.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import pub.zgq.community.util.RedisKeyUtil;

/**
 * @Author 孑然
 */
@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 给实体点赞 （set集合中存放实体的userId）
     * @param userId
     * @param entityType
     * @param entityId
     */
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        //// 获得相应Key
        //String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        //// 判断是否已经存在
        //Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        //if (isMember) {
        //    //已点赞 取消点赞
        //    redisTemplate.opsForSet().remove(entityLikeKey, userId);
        //} else {
        //    // 未点赞 点赞
        //    redisTemplate.opsForSet().add(entityLikeKey,userId);
        //}

        // 重构 (要包含两次更新操作) 要保证事务性
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                // 判断帖子的点赞set有无当前userId
                Boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);

                //开启事务
                operations.multi();
                if (isMember) {
                    // 取消点赞
                    operations.opsForSet().remove(entityLikeKey, userId);
                    // 当前用户的点赞数减一
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    //点赞
                    operations.opsForSet().add(entityLikeKey, userId);
                    // 加一
                    operations.opsForValue().increment(userLikeKey);
                }
                // 执行事务
                return operations.exec();
            }
        });

    }

    /**
     * 查询某实体点赞的数量
     * @param entityType
     * @param entityId
     * @return
     */
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * 查询某人对某实体的点赞状态
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public int  findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    /**
     * 查询某个用户所获赞的总数
     * @param userId
     * @return
     */
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }



}
