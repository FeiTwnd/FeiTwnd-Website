package cc.feitwnd.mapper;

import cc.feitwnd.dto.MessagePageQueryDTO;
import cc.feitwnd.entity.Messages;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

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

    /**
     * 分页条件查询留言
     * @param messagePageQueryDTO
     * @return
     */
    List<Messages> pageQuery(MessagePageQueryDTO messagePageQueryDTO);

    /**
     * 批量审核通过留言
     * @param ids
     */
    void batchApprove(List<Long> ids);

    /**
     * 批量删除留言
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 根据ID查询留言
     * @param id
     * @return
     */
    Messages getById(Long id);
}

