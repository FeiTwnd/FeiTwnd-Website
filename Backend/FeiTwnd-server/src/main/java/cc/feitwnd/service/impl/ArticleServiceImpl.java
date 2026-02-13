package cc.feitwnd.service.impl;

import cc.feitwnd.constant.MessageConstant;
import cc.feitwnd.constant.StatusConstant;
import cc.feitwnd.dto.ArticleDTO;
import cc.feitwnd.dto.ArticlePageQueryDTO;
import cc.feitwnd.entity.Articles;
import cc.feitwnd.exception.ArticleException;
import cc.feitwnd.mapper.ArticleMapper;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.service.ArticleService;
import cc.feitwnd.utils.MarkdownUtil;
import cc.feitwnd.vo.ArticleVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章服务实现
 */
@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 创建文章
     * @param articleDTO
     */
    public void createArticle(ArticleDTO articleDTO) {
        Articles articles = new Articles();
        BeanUtils.copyProperties(articleDTO, articles);

        // Markdown转HTML
        String contentHtml = MarkdownUtil.toHtml(articleDTO.getContentMarkdown());
        articles.setContentHtml(contentHtml);

        // 计算字数和阅读时间
        String plainText = articleDTO.getContentMarkdown();
        long wordCount = countWords(plainText);
        long readingTime = Math.max(1, wordCount / 300); // 按每分钟300字估算
        articles.setWordCount(wordCount);
        articles.setReadingTime(readingTime);

        // 设置发布信息
        if (articleDTO.getIsPublished() != null && articleDTO.getIsPublished().equals(StatusConstant.ENABLE)) {
            articles.setPublishTime(LocalDateTime.now());
        }

        // 初始化统计字段
        articles.setViewCount(0L);
        articles.setLikeCount(0L);
        articles.setCommentCount(0L);

        articleMapper.insert(articles);
    }

    /**
     * 分页条件查询文章列表（含草稿）
     * @param articlePageQueryDTO
     * @return
     */
    public PageResult pageQuery(ArticlePageQueryDTO articlePageQueryDTO) {
        PageHelper.startPage(articlePageQueryDTO.getPage(), articlePageQueryDTO.getPageSize());
        Page<ArticleVO> page = articleMapper.pageQuery(articlePageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据ID获取文章详情
     * @param id
     * @return
     */
    public Articles getById(Long id) {
        Articles articles = articleMapper.getById(id);
        if (articles == null) {
            throw new ArticleException(MessageConstant.ARTICLE_NOT_FOUND);
        }
        return articles;
    }

    /**
     * 更新文章
     * @param articleDTO
     */
    public void updateArticle(ArticleDTO articleDTO) {
        Articles articles = articleMapper.getById(articleDTO.getId());
        if (articles == null) {
            throw new ArticleException(MessageConstant.ARTICLE_NOT_FOUND);
        }

        BeanUtils.copyProperties(articleDTO, articles);

        // 如果Markdown内容有更新，重新转HTML并计算字数
        if (articleDTO.getContentMarkdown() != null) {
            String contentHtml = MarkdownUtil.toHtml(articleDTO.getContentMarkdown());
            articles.setContentHtml(contentHtml);

            long wordCount = countWords(articleDTO.getContentMarkdown());
            long readingTime = Math.max(1, wordCount / 300);
            articles.setWordCount(wordCount);
            articles.setReadingTime(readingTime);
        }

        articleMapper.update(articles);
    }

    /**
     * 批量删除文章
     * @param ids
     */
    public void batchDelete(List<Long> ids) {
        articleMapper.batchDelete(ids);
    }

    /**
     * 发布/取消发布文章
     * @param id
     * @param isPublished
     */
    public void publishOrCancel(Long id, Integer isPublished) {
        Articles articles = articleMapper.getById(id);
        if (articles == null) {
            throw new ArticleException(MessageConstant.ARTICLE_NOT_FOUND);
        }

        Articles updateArticle = Articles.builder()
                .id(id)
                .isPublished(isPublished)
                .build();

        // 发布时设置发布时间（仅首次发布设置）
        if (isPublished.equals(StatusConstant.ENABLE) && articles.getPublishTime() == null) {
            updateArticle.setPublishTime(LocalDateTime.now());
        }

        articleMapper.update(updateArticle);
    }

    /**
     * 文章搜索（标题、内容）
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult search(String keyword, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<ArticleVO> pageResult = articleMapper.search(keyword);
        return new PageResult(pageResult.getTotal(), pageResult.getResult());
    }

    /**
     * 统计字数（中文算1字，英文单词算1字）
     * @param text
     * @return
     */
    private long countWords(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        // 去除Markdown语法符号
        String cleanText = text.replaceAll("[#*`>\\-\\[\\]()!|]", "");
        // 中文字符数
        long chineseCount = cleanText.chars()
                .filter(c -> Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN)
                .count();
        // 英文单词数
        String englishText = cleanText.replaceAll("[\\u4e00-\\u9fff]", " ");
        String[] words = englishText.trim().split("\\s+");
        long englishCount = 0;
        for (String word : words) {
            if (!word.isEmpty() && word.matches(".*[a-zA-Z0-9].*")) {
                englishCount++;
            }
        }
        return chineseCount + englishCount;
    }
}
