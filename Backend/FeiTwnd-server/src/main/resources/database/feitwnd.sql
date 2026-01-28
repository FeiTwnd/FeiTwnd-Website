-- FeiTwnd个人网站数据库

drop database if exists FeiTwnd;
create database FeiTwnd;

use FeiTwnd;

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
    create_time datetime default current_timestamp comment '创建时间',
    update_time datetime default current_timestamp on update current_timestamp comment '更新时间'
) comment '个人信息表';

--  社交媒体表
create table social_media(
    id int primary key auto_increment,
    name varchar(20) not null comment '名称',
    icon varchar(50) comment '图标类名',
    link varchar(100) comment '链接',
    is_visible tinyint default 1 comment '是否可见',
    create_time datetime default current_timestamp comment '创建时间',
    update_time datetime default current_timestamp on update current_timestamp comment '更新时间'
) comment '社交媒体表';

-- 经历表
create table experiences(
    id int primary key auto_increment,
    type tinyint not null comment '类型，0-教育经历，1-实习及工作经历,2-项目经历',
    start_date DATE NOT NULL comment '开始时间',
    end_date DATE comment '结束时间',
    create_time datetime default current_timestamp comment '创建时间',
    update_time datetime default current_timestamp on update current_timestamp comment '更新时间'
) comment '经历表';

-- 教育经历表
create table experiences_education(
    id int primary key auto_increment,
    experience_id int not null comment '经历ID',
    badge varchar(255) not null comment '校徽url',
    school varchar(50) not null comment '学校名称',
    major varchar(50) not null comment '专业名称'
) comment '教育经历表';

-- 实习及工作经历表
create table experiences_work(
    id int primary key auto_increment,
    experience_id int not null comment '经历ID',
    logo varchar(255) not null comment '公司logo',
    company varchar(50) not null comment '公司名称',
    position varchar(50) not null comment '职位名称',
    content text not null comment '工作内容'
) comment '实习及工作经历表';

-- 项目经历表
create table experiences_project(
    id int primary key auto_increment,
    experience_id int not null comment '经历ID',
    project_name varchar(50) not null comment '项目名称',
    content text not null comment '项目内容'
) comment '项目经历表';

-- 技能表
create table skills(
    id int primary key auto_increment,
    name varchar(20) not null comment '技能名称',
    icon varchar(255) comment '图标url',
    create_time datetime default current_timestamp comment '创建时间',
    update_time datetime default current_timestamp on update current_timestamp comment '更新时间'
) comment '技能表';

-- 访客表
create table visitors(
    id int primary key auto_increment,
    ip varchar(45) not null comment 'IP地址',
    user_agent varchar(255) comment '用户代理',
    address varchar(50) comment '地址',
    longitude varchar(50) comment '经度',
    latitude varchar(50) comment '纬度',
    views int default 0 comment '访问次数',
    is_blocked tinyint default 0 comment '是否被封禁,0-否，1-是',
    expires_at datetime comment '封禁结束时间',
    create_time datetime default current_timestamp comment '创建时间',
    update_time datetime default current_timestamp on update current_timestamp comment '更新时间'
) comment '访客表';

-- 文章分类表
create table article_categories(
    id int primary key auto_increment,
    name varchar(20) not null comment '分类名称,如：日常,心得,技术,面经',
    slug varchar(20) not null comment 'URL标识，如：daily, thinking',
    icon varchar(50) comment '图标类名',
    sort int default 0 comment '排序，越小越靠前',
    create_time datetime default current_timestamp comment '创建时间',
    update_time datetime default current_timestamp on update current_timestamp comment '更新时间'
) comment '文章分类表';

-- 文章表
create table articles(
    id int primary key auto_increment,

    -- 基础信息相关
    title varchar(50) not null comment '文章标题',
    slug varchar(50) unique NOT NULL COMMENT 'URL标识，如：what-is-slug-field',
    summary varchar(255) comment '文章摘要',
    cover_image varchar(255) comment '封面图片url',

    -- 内容相关
    content_markdown text not null comment 'Markdown内容',
    content_html text not null comment '转换后的HTML内容',

    -- 分类相关
    category_id int comment '分类ID',

    -- 统计信息相关
    view_count int default 0 comment '浏览次数',
    like_count int default 0 comment '点赞次数',
    comment_count int default 0 comment '评论数',
    word_count int default 0 comment '字数统计',
    reading_time int default 0 comment '预计阅读时间，单位：分钟',

    -- 发布信息相关
    is_published tinyint default 0 comment '是否发布,0-否，1-是',
    publish_time datetime comment '发布时间',

    -- 归档优化相关
    publish_year INT GENERATED ALWAYS AS (IFNULL(YEAR(publish_time), 0)) STORED COMMENT '发布年份',
    publish_month INT GENERATED ALWAYS AS (IFNULL(MONTH(publish_time), 0)) STORED COMMENT '发布月份',
    publish_day INT GENERATED ALWAYS AS (IFNULL(DAY(publish_time), 0)) STORED COMMENT '发布日期',
    publish_date DATE GENERATED ALWAYS AS (IFNULL(DATE(publish_time), NULL)) STORED COMMENT '发布日期（去掉时间）',

    create_time datetime default current_timestamp comment '创建时间',
    update_time datetime default current_timestamp on update current_timestamp comment '更新时间'
) comment '文章表';