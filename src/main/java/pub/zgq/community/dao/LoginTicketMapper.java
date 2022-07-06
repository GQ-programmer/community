package pub.zgq.community.dao;

import org.apache.ibatis.annotations.*;
import pub.zgq.community.entity.LoginTicket;

/**
 * @Author 孑然
 */
@Mapper
public interface LoginTicketMapper {

    @Insert({
            "insert into login_ticket (user_id,ticket,status,expired) ",
            "values (#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    /**
     * 根据 ticket 查询数据
     * @param ticket
     * @return
     */
    @Select({
            "select id, user_id, ticket, status, expired",
            "from login_ticket where ticket = #{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    /**
     * 根据 ticket 修改对应的状态
     * @param ticket
     * @param status
     * @return
     */
    @Update({
            "<script>",
            "update login_ticket set status = #{status} where ticket = #{ticket}",
            "<if test=\"ticket!=null\"> ",
            "and 1=1",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);
}
