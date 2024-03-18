# 数据库初始化
# @author <a href="https://github.com/liyupi">程序员鱼皮</a>
# @from <a href="https://yupi.icu">编程导航知识星球</a>

-- 创建库
create database if not exists my_db;

-- 切换库
use my_db;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_userAccount (userAccount)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 代码生成器表
create table if not exists generator
(
    id              bigint auto_increment  comment 'id' primary key,
    name            varchar(128)                            null comment '名称',
    description     text                                    null comment '描述',
    basePackage     varchar(128)                            null comment '基础包',
    version         varchar(128)                            null comment '版本',
    author          varchar(128)                            null comment '作者',
    tags            varchar(1024)                           null comment '标签列表（json 数组）',
    picture         varchar(256)                            null comment '图片',
    fileConfig      text                                    null comment '文件配置（json字符串）',
    modelConfig     text                                    null comment '模型配置（json字符串）',
    distPath        text                                    null comment '代码生成器产物路径',
    status          int      default 0                      not null comment '状态',
    userId          bigint                                  not null comment '创建用户 id',
    createTime      datetime default CURRENT_TIMESTAMP      not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP      not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                      not null comment '是否删除',
    index idx_userId (userId)
) comment '代码生成器' collate = utf8mb4_unicode_ci;

-- 用户表插入数据
insert into user (userAccount, userPassword, userName, userAvatar, userProfile, userRole, createTime, updateTime)
values
    ('user1@example.com', 'password123', 'John Doe', 'avatar1.jpg', 'I am a regular user.', 'user', '2024-03-16 08:00:00', '2024-03-16 08:00:00'),
    ('admin@example.com', 'admin123', 'Admin', 'admin_avatar.jpg', 'I am the administrator.', 'admin', '2024-03-16 08:00:00', '2024-03-16 08:00:00');

-- 代码生成器表插入数据
-- 向代码生成器表插入数据
insert into generator (name, description, basePackage, version, author, tags, picture, fileConfig, modelConfig, distPath, status, userId, createTime, updateTime)
values
    ('Spring Boot Generator', 'Generate Spring Boot projects with ease.', 'com.example', '1.0.0', 'John Doe', '[ "Java", "Spring Boot", "Code Generation" ]', 'spring_boot_generator.png', '{"config1": "value1", "config2": "value2"}', '{"model1": "value1", "model2": "value2"}', '/path/to/generated/code1', 1, 1, '2024-03-16 08:00:00', '2024-03-16 08:00:00'),
    ('React Generator', 'Generate React projects quickly.', 'com.example.react', '1.0.0', 'Jane Smith', '[ "JavaScript", "React", "Code Generation" ]', 'react_generator.png', '{"config1": "value1", "config2": "value2"}', '{"model1": "value1", "model2": "value2"}', '/path/to/generated/code2', 1, 1, '2024-03-16 08:00:00', '2024-03-16 08:00:00'),
    ('Vue.js Generator', 'Generate Vue.js projects with ease.', 'com.example.vue', '1.0.0', 'Alice Johnson', '[ "JavaScript", "Vue.js", "Code Generation" ]', 'vuejs_generator.png', '{"config1": "value1", "config2": "value2"}', '{"model1": "value1", "model2": "value2"}', '/path/to/generated/code3', 1, 2, '2024-03-16 08:00:00', '2024-03-16 08:00:00'),
    ('Python Flask Generator', 'Generate Python Flask projects quickly.', 'com.example.flask', '1.0.0', 'Bob Williams', '[ "Python", "Flask", "Code Generation" ]', 'python_flask_generator.png', '{"config1": "value1", "config2": "value2"}', '{"model1": "value1", "model2": "value2"}', '/path/to/generated/code4', 1, 2, '2024-03-16 08:00:00', '2024-03-16 08:00:00'),
    ('Express.js Generator', 'Generate Express.js projects with ease.', 'com.example.express', '1.0.0', 'Emily Brown', '[ "JavaScript", "Express.js", "Code Generation" ]', 'expressjs_generator.png', '{"config1": "value1", "config2": "value2"}', '{"model1": "value1", "model2": "value2"}', '/path/to/generated/code5', 1, 3, '2024-03-16 08:00:00', '2024-03-16 08:00:00');

