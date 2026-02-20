package cc.feitwnd.mapper;

import cc.feitwnd.annotation.AutoFill;
import cc.feitwnd.entity.ArticleCategories;
import cc.feitwnd.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleCategoryMapper {
    /**
     * 获取所有文章分类
     * @return
     */
    @Select("select * from article_categories order by sort asc, id desc")
    List<ArticleCategories> listAll();

    /**
     * 添加文章分类
     * @param articleCategories
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(ArticleCategories articleCategories);

    /**
     * 更新文章分类
     * @param articleCategories
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(ArticleCategories articleCategories);

    /**
     * 删除文章分类
     * @param id
     */
    @Delete("delete from article_categories where id = #{id}")
    void deleteById(Long id);

    /**
     * 批量删除文章分类
     * @param ids
     */
    void batchDelete(List<Long> ids);

    // ===== 博客端方法 =====

    /**
     * 获取所有有已发布文章的分类
     */
    @Select("select ac.*, count(a.id) as article_count from article_categories ac " +
            "inner join articles a on ac.id = a.category_id " +
            "where a.is_published = 1 " +
            "group by ac.id " +
            "order by ac.sort asc, ac.id desc")
    List<ArticleCategories> getVisibleCategories();

    /**
     * 统计文章分类数
     */
    @Select("select count(*) from article_categories")
    Integer countTotal();
}
