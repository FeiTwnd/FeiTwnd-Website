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
    icon varchar(100) comment '图标类名',
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
create table education_experiences(
    id int primary key auto_increment,
    badge varchar(255) not null comment '校徽url',
    school varchar(50) not null comment '学校名称',
    major varchar(50) not null comment '专业名称',
    foreign key (id) references experiences(id)
) comment '教育经历表';

-- 实习及工作经历表
create table work_experiences(
    id int primary key auto_increment,
    logo varchar(255) not null comment '公司logo',
    company varchar(50) not null comment '公司名称',
    position varchar(50) not null comment '职位名称',
    content text not null comment '工作内容',
    foreign key (id) references experiences(id)
) comment '实习及工作经历表';

-- 项目经历表
create table project_experiences(
    id int primary key auto_increment,
    project_name varchar(50) not null comment '项目名称',
    content text not null comment '项目内容',
    foreign key (id) references experiences(id)
) comment '项目经历表';

-- 技能表
create table skills(
    id int primary key auto_increment,
    name varchar(20) not null comment '技能名称',
    icon varchar(255) comment '图标url',
    create_time datetime default current_timestamp comment '创建时间',
    update_time datetime default current_timestamp on update current_timestamp comment '更新时间'
) comment '技能表';