package cc.feitwnd.mapper;

import cc.feitwnd.annotation.AutoFill;
import cc.feitwnd.dto.ArticlePageQueryDTO;
import cc.feitwnd.entity.Articles;
import cc.feitwnd.enumeration.OperationType;
import cc.feitwnd.vo.ArticleVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleMapper {

    /**
     * 插入文章
     * @param articles
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Articles articles);

    /**
     * 分页条件查询文章（含分类名称）
     * @param articlePageQueryDTO
     * @return
     */
    Page<ArticleVO> pageQuery(ArticlePageQueryDTO articlePageQueryDTO);

    /**
     * 根据ID查询文章
     * @param id
     * @return
     */
    @Select("select * from articles where id = #{id}")
    Articles getById(Long id);

    /**
     * 更新文章
     * @param articles
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Articles articles);

    /**
     * 批量删除文章
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 全文搜索文章（标题、内容）
     * @param keyword
     * @return
     */
    Page<ArticleVO> search(String keyword);
}
