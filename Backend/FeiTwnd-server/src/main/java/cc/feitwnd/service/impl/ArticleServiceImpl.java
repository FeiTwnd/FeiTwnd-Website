package cc.feitwnd.service.impl;

import cc.feitwnd.constant.MessageConstant;
import cc.feitwnd.constant.StatusConstant;
import cc.feitwnd.dto.ArticleDTO;
import cc.feitwnd.dto.ArticlePageQueryDTO;
import cc.feitwnd.entity.Articles;
import cc.feitwnd.entity.ArticleTags;
import cc.feitwnd.entity.RssSubscriptions;
import cc.feitwnd.exception.ArticleException;
import cc.feitwnd.mapper.ArticleMapper;
import cc.feitwnd.mapper.ArticleTagMapper;
import cc.feitwnd.mapper.RssSubscriptionMapper;
import cc.feitwnd.properties.WebsiteProperties;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.service.ArticleService;
import cc.feitwnd.service.AsyncEmailService;
import cc.feitwnd.utils.MarkdownUtil;
import cc.feitwnd.vo.ArticleArchiveItemVO;
import cc.feitwnd.vo.ArticleArchiveVO;
import cc.feitwnd.vo.ArticleVO;
import cc.feitwnd.vo.BlogArticleDetailVO;
import cc.feitwnd.vo.BlogArticleVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 文章服务实现
 */
@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private AsyncEmailService asyncEmailService;

    @Autowired
    private RssSubscriptionMapper rssSubscriptionMapper;

    @Autowired
    private WebsiteProperties websiteProperties;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String VIEW_COUNT_KEY = "article:viewCount";

    /**
     * 创建文章
     * @param articleDTO
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "articleList", allEntries = true),
            @CacheEvict(value = "articleDetail", allEntries = true),
            @CacheEvict(value = "articleArchive", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void createArticle(ArticleDTO articleDTO) {
        Articles articles = new Articles();
        BeanUtils.copyProperties(articleDTO, articles);

        // Markdown转HTML（如果是富文本编辑器输出的HTML，直接清洗）
        String rawContent = articleDTO.getContentMarkdown();
        String contentHtml = MarkdownUtil.isHtml(rawContent)
                ? MarkdownUtil.sanitize(rawContent)
                : MarkdownUtil.toHtml(rawContent);
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

        // 保存文章-标签关联
        if (articleDTO.getTagIds() != null && !articleDTO.getTagIds().isEmpty()) {
            articleTagMapper.batchInsertRelations(articles.getId(), articleDTO.getTagIds());
        }
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
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "articleList", allEntries = true),
            @CacheEvict(value = "articleDetail", allEntries = true),
            @CacheEvict(value = "articleArchive", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void updateArticle(ArticleDTO articleDTO) {
        Articles articles = articleMapper.getById(articleDTO.getId());
        if (articles == null) {
            throw new ArticleException(MessageConstant.ARTICLE_NOT_FOUND);
        }

        BeanUtils.copyProperties(articleDTO, articles);

        // 如果Markdown内容有更新，重新转HTML并计算字数
        if (articleDTO.getContentMarkdown() != null) {
            String raw = articleDTO.getContentMarkdown();
            String contentHtml = MarkdownUtil.isHtml(raw)
                    ? MarkdownUtil.sanitize(raw)
                    : MarkdownUtil.toHtml(raw);
            articles.setContentHtml(contentHtml);

            long wordCount = countWords(articleDTO.getContentMarkdown());
            long readingTime = Math.max(1, wordCount / 300);
            articles.setWordCount(wordCount);
            articles.setReadingTime(readingTime);
        }

        articleMapper.update(articles);

        // 更新文章-标签关联
        if (articleDTO.getTagIds() != null) {
            articleTagMapper.deleteRelationsByArticleId(articleDTO.getId());
            if (!articleDTO.getTagIds().isEmpty()) {
                articleTagMapper.batchInsertRelations(articleDTO.getId(), articleDTO.getTagIds());
            }
        }
    }

    /**
     * 批量删除文章
     * @param ids
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "articleList", allEntries = true),
            @CacheEvict(value = "articleDetail", allEntries = true),
            @CacheEvict(value = "articleArchive", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
    public void batchDelete(List<Long> ids) {
        articleTagMapper.batchDeleteRelationsByArticleIds(ids);
        articleMapper.batchDelete(ids);
    }

    /**
     * 发布/取消发布文章
     * @param id
     * @param isPublished
     */
    @Caching(evict = {
            @CacheEvict(value = "articleList", allEntries = true),
            @CacheEvict(value = "articleDetail", allEntries = true),
            @CacheEvict(value = "articleArchive", allEntries = true),
            @CacheEvict(value = "blogReport", allEntries = true)
    })
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

        // 发布时通知RSS订阅者
        if (isPublished.equals(StatusConstant.ENABLE)) {
            notifyRssSubscribers(articles);
        }
    }

    /**
     * 通知RSS订阅者新文章发布
     */
    private void notifyRssSubscribers(Articles article) {
        try {
            List<RssSubscriptions> subscribers = rssSubscriptionMapper.getAllActiveSubscriptions();
            if (subscribers == null || subscribers.isEmpty()) {
                return;
            }
            String articleUrl = websiteProperties.getBlog() + "/article/" + article.getSlug();
            for (RssSubscriptions subscriber : subscribers) {
                asyncEmailService.sendNewArticleNotificationAsync(
                        subscriber.getEmail(),
                        subscriber.getNickname() != null ? subscriber.getNickname() : "订阅者",
                        article.getTitle(),
                        article.getSummary(),
                        articleUrl
                );
            }
            log.info("已向 {} 个RSS订阅者发送新文章通知: title={}", subscribers.size(), article.getTitle());
        } catch (Exception e) {
            log.error("通知RSS订阅者异常: title={}, ex={}", article.getTitle(), e.getMessage());
        }
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

    // ===== 博客端方法 =====

    public PageResult getPublishedPage(int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<BlogArticleVO> pageResult = articleMapper.getPublishedPage();
        return new PageResult(pageResult.getTotal(), pageResult.getResult());
    }

    @Cacheable(value = "articleDetail", key = "#slug")
    public BlogArticleDetailVO getBySlug(String slug) {
        BlogArticleDetailVO articleDetail = articleMapper.getBySlug(slug);
        if (articleDetail == null) {
            throw new ArticleException(MessageConstant.ARTICLE_NOT_FOUND);
        }

        // 填充标签名称列表
        List<ArticleTags> tags = articleTagMapper.getTagsByArticleId(articleDetail.getId());
        if (tags != null && !tags.isEmpty()) {
            articleDetail.setTagNames(tags.stream().map(ArticleTags::getName).toList());
        }

        // 填充上一篇/下一篇导航
        articleDetail.setPrevArticle(articleMapper.getPrevArticle(articleDetail.getId()));
        articleDetail.setNextArticle(articleMapper.getNextArticle(articleDetail.getId()));

        // 填充相关文章推荐（同分类，排除当前文章，最多6篇）
        if (articleDetail.getCategoryId() != null) {
            articleDetail.setRelatedArticles(
                    articleMapper.getRelatedArticles(articleDetail.getId(), articleDetail.getCategoryId()));
        }

        return articleDetail;
    }

    /**
     * 文章浏览量+1（写入Redis，定时同步MySQL）
     */
    public void incrementViewCount(String slug) {
        BlogArticleDetailVO articleDetail = articleMapper.getBySlug(slug);
        if (articleDetail != null) {
            redisTemplate.opsForHash().increment(VIEW_COUNT_KEY, articleDetail.getId().toString(), 1);
        }
    }

    public PageResult getPublishedByCategoryId(Long categoryId, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<BlogArticleVO> pageResult = articleMapper.getPublishedByCategoryId(categoryId);
        return new PageResult(pageResult.getTotal(), pageResult.getResult());
    }

    @Cacheable(value = "articleArchive", key = "'all'")
    public List<ArticleArchiveVO> getArchive() {
        List<ArticleArchiveItemVO> allArticles = articleMapper.getArchiveList();
        // 按年月分组（利用数据库的 publish_year, publish_month 生成列）
        Map<String, ArticleArchiveVO> archiveMap = new LinkedHashMap<>();
        for (ArticleArchiveItemVO item : allArticles) {
            if (item.getPublishTime() == null) {
                continue;
            }
            int year = item.getPublishTime().getYear();
            int month = item.getPublishTime().getMonthValue();
            String key = year + "-" + month;
            ArticleArchiveVO archiveVO = archiveMap.computeIfAbsent(key, k ->
                    ArticleArchiveVO.builder()
                            .year(year)
                            .month(month)
                            .articles(new ArrayList<>())
                            .build()
            );
            archiveVO.getArticles().add(item);
        }
        return new ArrayList<>(archiveMap.values());
    }

    public PageResult searchPublished(String keyword, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<BlogArticleVO> pageResult = articleMapper.searchPublished(keyword);
        return new PageResult(pageResult.getTotal(), pageResult.getResult());
    }

    public PageResult getPublishedByTagId(Long tagId, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<BlogArticleVO> pageResult = articleMapper.getPublishedByTagId(tagId);
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
