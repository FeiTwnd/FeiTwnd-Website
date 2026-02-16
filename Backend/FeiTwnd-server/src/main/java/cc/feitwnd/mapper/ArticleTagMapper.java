package cc.feitwnd.mapper;

import cc.feitwnd.annotation.AutoFill;
import cc.feitwnd.entity.ArticleTags;
import cc.feitwnd.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleTagMapper {

    /**
     * 获取所有标签
     */
    @Select("select * from article_tags order by id desc")
    List<ArticleTags> listAll();

    /**
     * 添加标签
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(ArticleTags articleTag);

    /**
     * 更新标签
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(ArticleTags articleTag);

    /**
     * 批量删除标签
     */
    void batchDelete(List<Long> ids);

    /**
     * 根据ID查询标签
     */
    @Select("select * from article_tags where id = #{id}")
    ArticleTags getById(Long id);

    /**
     * 根据文章ID查询关联的标签列表
     */
    List<ArticleTags> getTagsByArticleId(Long articleId);

    /**
     * 批量插入文章-标签关联
     */
    void batchInsertRelations(Long articleId, List<Long> tagIds);

    /**
     * 删除文章的所有标签关联
     */
    @Delete("delete from article_tag_relations where article_id = #{articleId}")
    void deleteRelationsByArticleId(Long articleId);

    /**
     * 批量删除文章的标签关联（按文章ID列表）
     */
    void batchDeleteRelationsByArticleIds(List<Long> articleIds);

    /**
     * 获取有已发布文章的标签列表（博客端）
     */
    List<ArticleTags> getVisibleTags();

    /**
     * 获取标签总数
     */
    @Select("select count(*) from article_tags")
    Integer countTotal();
}
