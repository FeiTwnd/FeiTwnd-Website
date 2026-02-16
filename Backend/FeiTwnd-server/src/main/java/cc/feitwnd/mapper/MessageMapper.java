package cc.feitwnd.mapper;

import cc.feitwnd.dto.MessagePageQueryDTO;
import cc.feitwnd.entity.Messages;
import cc.feitwnd.vo.MessageVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

    // ===== 博客端方法 =====

    /**
     * 获取已审核的留言列表（用于构建树形结构）
     */
    List<MessageVO> getApprovedList();

    /**
     * 更新留言内容（访客编辑）
     */
    void updateContent(Messages messages);

    /**
     * 删除单条留言
     */
    @Delete("delete from messages where id = #{id}")
    void deleteById(Long id);

    /**
     * 统计总留言数
     */
    @Select("select count(*) from messages")
    Integer countTotal();

    /**
     * 统计待审核留言数
     */
    @Select("select count(*) from messages where is_approved = 0")
    Integer countPending();
}

