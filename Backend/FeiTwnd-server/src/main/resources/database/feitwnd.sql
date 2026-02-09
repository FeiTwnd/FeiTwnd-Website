-- FeiTwnd个人网站数据库
-- 包含四个网站的后端数据库
-- 主页home.feitwnd.cc
-- 管理admin.feitwnd.cc
-- 简历cv.feitwnd.cc
-- 博客blog.feitwnd.cc

drop database if exists FeiTwnd;
create database FeiTwnd;

use FeiTwnd;

-- ===========管理端(admin.feitwnd.cc)相关表====================
-- 管理员表
create table admin(
    id int primary key auto_increment,
    username varchar(20) not null comment '用户名',
    password varchar(255) not null comment '加密后的密码',
    salt varchar(50) not null comment '盐值',
    nickname varchar(20) comment '昵称',
    email varchar(50) comment '电子邮箱',
    role tinyint comment '角色,1-管理员,0-游客',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间'
) comment '管理员表';

-- 操作日志表
create table operation_logs(
    id int primary key auto_increment,
    admin_id int comment '管理员ID',
    operation_type varchar(20) comment '操作类型',
    operation_target varchar(100) comment '操作目标(模块)',
    target_id int comment '目标ID',
    operate_data JSON comment '操作数据',
    result tinyint comment '操作结果，0-失败，1-成功',
    error_message text comment '错误信息',
    operation_time datetime comment '操作时间',
    index idx_admin_time(admin_id,operation_time desc),
    index idx_type_time (operation_type, operation_time desc)
)comment '操作日志表';

-- 系统配置表
create table system_config(
    id int primary key auto_increment,
    config_key varchar(50) unique not null comment '配置键',
    config_value text comment '配置值',
    config_type varchar(20) comment '配置类型,string,number,boolean,json,date',
    description varchar(255) comment '配置描述',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间'
) comment '系统配置表';
-- ==========================================================

-- ================主页(home.feitwnd.cc)相关表================
-- 个人信息表
create table personal_info(
    id int primary key auto_increment,
    nickname varchar(20) not null comment '昵称',
    tag varchar(30) not null comment '标签',
    description varchar(50) comment '个人简介',
    avatar varchar(255) comment '头像url',
    website varchar(100) comment '个人网站',
    email varchar(50) comment '电子邮箱',
    github varchar(100) comment 'GitHub',
    location varchar(50) comment '所在地',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间'
) comment '个人信息表';

--  社交媒体表
create table social_media(
    id int primary key auto_increment,
    name varchar(20) not null comment '名称',
    icon varchar(50) comment '图标类名',
    link varchar(100) comment '链接',
    sort int comment '排序，越小越靠前',
    is_visible tinyint default 1 comment '是否可见',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间'
) comment '社交媒体表';
-- ==========================================================


-- ====================简历(cv.feitwnd.cc)相关表===============
-- 经历表
create table experiences(
    id int primary key auto_increment,
    type tinyint not null comment '类型，0-教育经历，1-实习及工作经历,2-项目经历',

    --  内容基本信息
    title varchar(50) not null comment '标题,公司名/学校名/项目名',
    subtitle varchar(100) comment '副标题,职位/专业/项目角色',
    logo_url varchar(255) comment 'logo/校徽',
    content text not null comment '内容',
    start_date DATE NOT NULL comment '开始时间',
    end_date DATE comment '结束时间',

    is_visible tinyint default 1 comment '是否可见',

    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',

    index idx_time(start_date desc)
) comment '经历表';

-- 技能表
create table skills(
    id int primary key auto_increment,
    name varchar(20) not null comment '技能名称',
    description varchar(255) comment '技能描述',
    icon varchar(255) comment '图标url',
    sort int comment '排序，越小越靠前',
    is_visible tinyint default 1 comment '是否可见',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间'
) comment '技能表';
-- ==========================================================

-- ================博客(blog.feitwnd.cc)相关表=================
-- 访客表
create table visitors(
    id int primary key auto_increment comment '访客ID',
    fingerprint varchar(150) not null comment '访客指纹,用于唯一标识访客',
    session_id varchar(100) comment '会话ID(当前浏览器会话)',
    ip varchar(45) not null comment 'IP地址',
    user_agent varchar(255) comment '用户代理',
    country varchar(25) comment '国家',
    province varchar(25) comment '省份',
    city varchar(25) comment '城市',
    longitude varchar(50) comment '经度',
    latitude varchar(50) comment '纬度',
    -- 访问信息
    first_visit_time datetime comment '首次访问时间',
    last_visit_time datetime comment '最后访问时间',
    total_views int comment '访问次数',
    is_blocked tinyint comment '是否被封禁,0-否，1-是',
    expires_at datetime comment '封禁结束时间',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',

    unique index uk_visitor_fingerprint(fingerprint),
    index idx_session_id(session_id),
    index idx_last_visit(last_visit_time desc)
) comment '访客表';

-- 浏览表
create table views(
    id int primary key auto_increment,
    visitor_id int comment '访客ID',
    page_path varchar(100) comment '页面路径',
    referer varchar(255) comment '来源URL',
    page_title varchar(100) comment '页面标题',
    ip_address varchar(45) comment 'IP地址',
    user_agent varchar(255) comment '用户代理',
    view_time datetime comment '访问时间',

    index idx_view_time(view_time desc),
    index idx_visitor_time(visitor_id,view_time desc),
    index idx_page_date(page_path(50),view_time)
) comment '浏览表';

-- 文章分类表
create table article_categories(
    id int primary key auto_increment,
    name varchar(20) not null comment '分类名称,如：日常,心得,年度总结,编程,面经',
    slug varchar(20) not null comment 'URL标识，如：daily, thinking, year-summary, programming, interview',
    icon varchar(50) comment '图标类名',
    sort int default 0 comment '排序，越小越靠前',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间'
) comment '文章分类表';

-- 文章表
create table articles(
    id int primary key auto_increment,

    -- 基础信息
    title varchar(50) not null comment '文章标题',
    slug varchar(50) unique not null comment 'URL标识，如：what-is-slug-field',
    summary varchar(255) comment '文章摘要',
    cover_image varchar(255) comment '封面图片url',

    -- 内容
    content_markdown text not null comment 'Markdown内容',
    content_html text not null comment '转换后的HTML内容',

    -- 分类
    category_id int comment '分类ID',

    -- 统计信息
    view_count int default 0 comment '浏览次数',
    like_count int default 0 comment '点赞次数',
    comment_count int default 0 comment '评论数',
    word_count int default 0 comment '字数统计',
    reading_time int default 0 comment '预计阅读时间，单位：分钟',

    -- 发布信息
    is_published tinyint default 0 comment '是否发布,0-否，1-是',
    publish_time datetime comment '发布时间',

    -- 归档
    publish_year INT GENERATED ALWAYS AS (IFNULL(YEAR(publish_time), 0)) STORED COMMENT '发布年份',
    publish_month INT GENERATED ALWAYS AS (IFNULL(MONTH(publish_time), 0)) STORED COMMENT '发布月份',
    publish_day INT GENERATED ALWAYS AS (IFNULL(DAY(publish_time), 0)) STORED COMMENT '发布日期',
    publish_date DATE GENERATED ALWAYS AS (IFNULL(DATE(publish_time), NULL)) STORED COMMENT '发布日期（去掉时间）',

    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',

    index idx_published_time (is_published, publish_time desc),
    index idx_publish_date (publish_date desc),
    index idx_category_status (category_id, is_published, publish_time desc),
    index idx_slug(slug),
    index idx_view_count (view_count desc),
    -- 全文索引，用于搜索
    fulltext idx_fulltext(title,summary,content_markdown(500))
) comment '文章表';

-- 文章评论表
create table article_comments(
    id int primary key auto_increment,

    -- 评论信息
    article_id int not null comment '文章ID',
    root_id int comment '根评论ID,null是一级评论',
    parent_id int comment '父评论ID,null是一级评论',
    content text not null comment '评论内容',
    content_html text not null comment '转换后的HTML内容,(如果是markdown)',

    -- 评论者信息
    visitor_id int comment '访客ID',
    nickname varchar(15) comment '昵称',
    email_or_qq varchar(50) comment '邮箱或qq',
    location varchar(30) comment '地址',
    user_agent_os varchar(20) comment '操作系统名称',
    user_agent_browser varchar(20) comment '浏览器名称',

    -- 状态信息
    is_approved tinyint default 0 comment '是否审核通过，0-否，1-是',
    is_markdown tinyint default 0 comment '是否使用markdown，0-否，1-是',
    is_secret tinyint default 0 comment '是否匿名，0-否，1-是',
    is_notice tinyint default 0 comment '有回复是否通知，0-否，1-是',
    is_edited tinyint default 0 comment '是否编辑过，0-否，1-是',

    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',

    -- 文章评论列表
    index idx_article_status (article_id, is_approved, create_time desc),
    -- 回复列表
    index idx_parent (parent_id desc),
    index idx_root (root_id desc),
    -- 审核列表
    index idx_approved (is_approved, create_time desc),
    -- 用户识别
    index idx_fingerprint (visitor_id)
) comment '文章评论表';

-- 文章点赞表
create table article_likes(
    id int primary key auto_increment,
    article_id int not null comment '文章ID',
    visitor_id int not null comment '访客ID',
    like_time datetime comment '点赞时间',
    unique index uk_article_visitor (article_id, visitor_id),
    index idx_article (article_id, like_time desc)
) comment '文章点赞表';

-- Rss订阅记录表
create table rss_subscriptions(
    id int primary key auto_increment,
    visitor_id int not null comment '访客ID',
    nickname varchar(15) not null comment '昵称',
    email varchar(50) not null comment '邮箱',
    is_active tinyint default 1 comment '是否激活，0-否，1-是',
    subscribe_time datetime comment '订阅时间',
    un_subscribe_time datetime comment '取消订阅时间',
    index idx_email(email),
    index idx_visitor_id(visitor_id)
) comment 'Rss订阅记录表';

-- 友链表
create table friend_links(
    id int primary key auto_increment,
    name varchar(20) not null comment '网站名称',
    url varchar(100) not null comment '网站地址',
    avatar_url varchar(255) comment '头像url',
    description varchar(255) comment '网站描述',
    is_visible tinyint default 1 comment '是否可见',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间'
) comment '友情链接表';

-- 留言表
create table messages(
    id int primary key auto_increment,

    -- 留言信息
    content text not null comment '留言内容',
    content_html text not null comment '转换后的HTML内容,(如果是markdown)',
    root_id int comment '根留言ID,null是一级留言',
    parent_id int comment '父留言ID,null是一级留言',

    -- 留言者信息
    visitor_id int comment '访客ID',
    nickname varchar(15) comment '昵称',
    email_or_qq varchar(50) comment '邮箱或qq',
    location varchar(30) comment '地址',
    user_agent_os varchar(20) comment '操作系统名称',
    user_agent_browser varchar(20) comment '浏览器名称',

    -- 状态信息
    is_approved tinyint default 0 comment '是否审核通过，0-否，1-是',
    is_markdown tinyint default 0 comment '是否使用markdown，0-否，1-是',
    is_secret tinyint default 0 comment '是否匿名，0-否，1-是',
    is_notice tinyint default 0 comment '有回复是否通知，0-否，1-是',
    is_edited tinyint default 0 comment '是否编辑过，0-否，1-是',

    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间'
) comment '留言表';

-- 音乐表
create table music(
    id int primary key auto_increment,
    title varchar(50) not null comment '音乐标题',
    artist varchar(50) comment '作者',
    duration int comment '时长，单位：秒',
    -- 文件信息url
    cover_image varchar(255) comment '封面图片url',
    music_url varchar(255) not null comment '音频文件url',
    lyric_url varchar(255) comment '歌词文件url',

    has_lyric tinyint default 0 comment '是否有歌词，0-否，1-是',
    lyric_type varchar(10) comment '歌词类型,lrc,json,txt',
    sort int comment '排序，越小越靠前',
    is_visible tinyint default 1 comment '是否可见',

    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',

    index idx_sort_visible (sort, is_visible,id desc)
) comment '音乐表';
-- ==========================================================