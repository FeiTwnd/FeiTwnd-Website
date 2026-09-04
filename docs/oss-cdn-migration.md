# OSS 图片迁移到 CDN 指南

当配置了 OSS 防盗刷（CDN 私有回源，见根目录 README「OSS 防盗刷配置」章节）后，**历史已入库的 OSS 直链 URL 需要批量替换为你的 CDN 域名**，否则 Bucket 设为私有后这些图片会全部 403。

本文档给出完整迁移步骤与 SQL，适用于任何使用本项目的开源使用者。

---

## 一、迁移原理

- 上传接口返回的 URL 是 `https://{bucket}.{endpoint}/{分类}/{uuid}.{ext}`（如 `https://my-bucket.oss-cn-shanghai.aliyuncs.com/image/xxx.jpg`）。
- 配置 `feitwnd.alioss.cdn-domain` 后，**新上传**的文件返回 `https://{cdn域名}/{分类}/{uuid}.{ext}`。
- **历史数据**里存的还是旧 OSS 直链，需把整库前缀替换为 CDN 域名。对象 key（`{分类}/{uuid}.{ext}`）不变，CDN 私有回源会自动签名读取，无需重新上传。

## 二、前置条件（顺序重要）

1. OSS Bucket 读写权限已改为**私有**
2. CDN 已配置**私有回源**（回源自动签名），CDN 域名 HTTPS 已启用，`http://` 与 `https://` 访问均正常
3. 代码已配置 `feitwnd.alioss.cdn-domain`（新上传返回 CDN URL）
4. （可选加固）CDN 域名已开 **Referer 防盗链**（白名单填自己站点域名）；迁移验证通过后，可在 OSS 侧开启**阻止公共访问**作为最终保险（不影响 CDN 私有回源）
5. 先执行下方 **SQL 备份**，确认无误后再替换

> 完整的云侧配置清单（费用告警、RAM 子账号最小权限、CDN 私有回源步骤、低成本替代方案）见根目录 README「OSS 防盗刷配置」章节。

## 三、执行迁移

### 1. 备份（务必先做）

```bash
mysqldump -u root -p FeiTwnd > feitwnd_backup_$(date +%Y%m%d).sql
```

### 2. 替换 SQL（通用模板）

把下面的 `YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com` 换成你的 Bucket 外网域名，
把 `cdn.yourdomain.com` 换成你的 CDN 加速域名。逐条执行。

```sql
-- 个人信息头像
UPDATE personal_info
SET avatar = REPLACE(avatar, 'https://YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com', 'https://cdn.yourdomain.com')
WHERE avatar LIKE '%YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com%';

-- 技能图标
UPDATE skills
SET icon = REPLACE(icon, 'https://YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com', 'https://cdn.yourdomain.com')
WHERE icon LIKE '%YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com%';

-- 经历/履历 logo
UPDATE experiences
SET logo_url = REPLACE(logo_url, 'https://YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com', 'https://cdn.yourdomain.com')
WHERE logo_url LIKE '%YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com%';

-- 文章封面
UPDATE articles
SET cover_image = REPLACE(cover_image, 'https://YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com', 'https://cdn.yourdomain.com')
WHERE cover_image LIKE '%YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com%';

-- 文章正文（Markdown 源码里可能内嵌图片）
UPDATE articles
SET content_markdown = REPLACE(content_markdown, 'https://YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com', 'https://cdn.yourdomain.com')
WHERE content_markdown LIKE '%YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com%';

-- 文章正文（渲染 HTML 里可能内嵌图片）
UPDATE articles
SET content_html = REPLACE(content_html, 'https://YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com', 'https://cdn.yourdomain.com')
WHERE content_html LIKE '%YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com%';

-- 友链头像
UPDATE friend_links
SET avatar_url = REPLACE(avatar_url, 'https://YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com', 'https://cdn.yourdomain.com')
WHERE avatar_url LIKE '%YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com%';

-- 音乐封面
UPDATE music
SET cover_image = REPLACE(cover_image, 'https://YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com', 'https://cdn.yourdomain.com')
WHERE cover_image LIKE '%YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com%';

-- 音乐文件
UPDATE music
SET music_url = REPLACE(music_url, 'https://YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com', 'https://cdn.yourdomain.com')
WHERE music_url LIKE '%YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com%';

-- 歌词文件
UPDATE music
SET lyric_url = REPLACE(lyric_url, 'https://YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com', 'https://cdn.yourdomain.com')
WHERE lyric_url LIKE '%YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com%';

-- 城市足迹图片
UPDATE city_images
SET image_url = REPLACE(image_url, 'https://YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com', 'https://cdn.yourdomain.com')
WHERE image_url LIKE '%YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com%';

-- 文章评论正文（Markdown/HTML 可能内嵌图片，如有该表该列）
UPDATE article_comments
SET content_html = REPLACE(content_html, 'https://YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com', 'https://cdn.yourdomain.com')
WHERE content_html LIKE '%YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com%';

-- 留言正文（Markdown/HTML 可能内嵌图片，如有该表该列）
UPDATE messages
SET content_html = REPLACE(content_html, 'https://YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com', 'https://cdn.yourdomain.com')
WHERE content_html LIKE '%YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com%';
```

> 提示：
> - 若某张表不存在（例如你未使用该功能/表名不同），跳过对应语句即可。
> - 若你的数据库表前缀或列名不同，请按实际表结构调整。
> - `social_media.icon` 存的是图标**类名**不是图片 URL，**不要**替换。

### 3. 验证

```sql
-- 替换后应返回 0 行（全部完成）
SELECT COUNT(*) FROM articles WHERE cover_image LIKE '%YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com%';
SELECT COUNT(*) FROM articles WHERE content_html LIKE '%YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com%';
-- 抽查一条新 URL
SELECT cover_image FROM articles WHERE cover_image LIKE '%cdn.yourdomain.com%' LIMIT 1;
```

### 4. 清缓存（如有 CDN 刷新/回源缓存）

在 CDN 控制台对上述对象目录做一次「刷新」或等待缓存过期（私有回源一般实时回源，验证能打开即代表 OK）。

---

## 四、回滚

若迁移后图片无法访问（证书/回源配置问题），先把 Bucket 临时改回**公开读**，再反向 REPLACE 恢复旧域名即可：

```sql
UPDATE articles
SET content_html = REPLACE(content_html, 'https://cdn.yourdomain.com', 'https://YOUR_BUCKET.oss-cn-xxxx.aliyuncs.com')
WHERE content_html LIKE '%cdn.yourdomain.com%';
-- 其余表同理
```
