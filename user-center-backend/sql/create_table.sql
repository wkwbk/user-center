CREATE TABLE `user`
(
    `username`      varchar(256)                       NULL COMMENT '用户昵称',
    `id`            bigint                             NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_account`  varchar(256)                       NULL COMMENT '账号',
    `avatar_url`    varchar(1024)                      NULL COMMENT '用户头像',
    `gender`        tinyint                            NULL COMMENT '性别',
    `user_password` varchar(512)                       NOT NULL COMMENT '密码',
    `phone`         varchar(128)                       NULL COMMENT '电话',
    `email`         varchar(512)                       NULL COMMENT '邮箱',
    `user_status`   int      DEFAULT '0'               NULL COMMENT '状态',
    `create_time`   datetime DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    `update_time`   datetime DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`     tinyint  DEFAULT '0'               NOT NULL COMMENT '是否删除',
    `user_role`     int      DEFAULT '0'               NOT NULL COMMENT '用户角色',
    PRIMARY KEY (`id`)
) COMMENT = '用户表';