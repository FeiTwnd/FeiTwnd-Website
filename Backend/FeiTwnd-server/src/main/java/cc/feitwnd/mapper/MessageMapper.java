package cc.feitwnd.mapper;

import cc.feitwnd.entity.Messages;
import org.apache.ibatis.annotations.Mapper;

/**
 * 留言Mapper
 */
@Mapper
public interface MessageMapper {

    /**
     * 新增留言
     * @param messages
     */
    void save(Messages messages);
}
