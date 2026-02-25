-- 为 sys_user 表添加 api_key 字段
ALTER TABLE `sys_user` ADD COLUMN `api_key` VARCHAR(255) DEFAULT NULL COMMENT 'API Key' AFTER `email`;
