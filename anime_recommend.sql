/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45)
 Source Host           : localhost:3306
 Source Schema         : anime_recommend

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45)
 File Encoding         : 65001

 Date: 11/06/2026 00:52:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `admin_operation_log`;
CREATE TABLE `admin_operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_id` bigint NOT NULL,
  `admin_username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `execution_time` bigint NULL DEFAULT NULL,
  `ip_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `operation_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `operation_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `request_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `request_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_operation_log
-- ----------------------------
INSERT INTO `admin_operation_log` VALUES (1, -1, 'admin_admin_ad', '2026-04-01 18:56:26.070936', NULL, 280, '127.0.0.1', '获取仪表盘数据', 'DASHBOARD_DATA', 'GET', '', '/api/admin/dashboard', 1);
INSERT INTO `admin_operation_log` VALUES (2, -1, 'admin_admin_ad', '2026-04-01 18:56:31.390238', NULL, 25, '127.0.0.1', '查询动漫列表', 'ANIME_LIST', 'GET', 'page=0&size=10', '/api/admin/anime/list', 1);
INSERT INTO `admin_operation_log` VALUES (3, -1, 'admin_admin_ad', '2026-04-01 18:56:35.637752', NULL, 18, '127.0.0.1', '查询用户列表', 'USER_LIST', 'GET', 'page=0&size=10', '/api/admin/user/list', 1);
INSERT INTO `admin_operation_log` VALUES (4, -1, 'admin_admin_ad', '2026-04-01 18:56:38.403069', NULL, 57, '127.0.0.1', '查询日程列表', 'SCHEDULE_LIST', 'GET', 'page=0&size=10', '/api/admin/schedule/list', 1);
INSERT INTO `admin_operation_log` VALUES (5, -1, 'admin_admin_ad', '2026-04-01 18:56:41.296652', NULL, 37, '127.0.0.1', '查询评论列表', 'REVIEW_LIST', 'GET', 'page=0&size=10', '/api/admin/review/list', 1);
INSERT INTO `admin_operation_log` VALUES (6, -1, 'admin_admin_ad', '2026-04-01 18:56:49.696680', NULL, 158, '127.0.0.1', '获取仪表盘数据', 'DASHBOARD_DATA', 'GET', '', '/api/admin/dashboard', 1);
INSERT INTO `admin_operation_log` VALUES (7, -1, 'admin_admin_ad', '2026-04-02 08:41:52.262334', NULL, 1380, '127.0.0.1', '获取仪表盘数据', 'DASHBOARD_DATA', 'GET', '', '/api/admin/dashboard', 1);
INSERT INTO `admin_operation_log` VALUES (8, -1, 'admin_admin_ad', '2026-04-02 08:41:52.714090', NULL, 208, '127.0.0.1', '获取仪表盘数据', 'DASHBOARD_DATA', 'GET', '', '/api/admin/dashboard', 1);
INSERT INTO `admin_operation_log` VALUES (9, -1, 'admin_admin_ad', '2026-04-02 08:42:03.800873', NULL, 22, '127.0.0.1', '查询动漫列表', 'ANIME_LIST', 'GET', 'page=0&size=10', '/api/admin/anime/list', 1);
INSERT INTO `admin_operation_log` VALUES (10, -1, 'admin_admin_ad', '2026-04-02 08:42:09.952499', NULL, 21, '127.0.0.1', '查询评论列表', 'REVIEW_LIST', 'GET', 'page=0&size=10', '/api/admin/review/list', 1);
INSERT INTO `admin_operation_log` VALUES (11, -1, 'admin_admin_ad', '2026-04-02 08:42:18.003727', NULL, 12, '127.0.0.1', '查询日程列表', 'SCHEDULE_LIST', 'GET', 'page=0&size=10', '/api/admin/schedule/list', 1);
INSERT INTO `admin_operation_log` VALUES (12, -1, 'admin_admin_ad', '2026-05-13 13:53:58.047481', NULL, 344, '127.0.0.1', '获取仪表盘数据', 'DASHBOARD_DATA', 'GET', '', '/api/admin/dashboard', 1);
INSERT INTO `admin_operation_log` VALUES (13, -1, 'admin_admin_ad', '2026-05-13 13:54:00.604250', NULL, 22, '127.0.0.1', '查询动漫列表', 'ANIME_LIST', 'GET', 'page=0&size=10', '/api/admin/anime/list', 1);
INSERT INTO `admin_operation_log` VALUES (14, -1, 'admin_admin_ad', '2026-05-13 13:54:03.460580', NULL, 17, '127.0.0.1', '查询用户列表', 'USER_LIST', 'GET', 'page=0&size=10', '/api/admin/user/list', 1);
INSERT INTO `admin_operation_log` VALUES (15, -1, 'admin_admin_ad', '2026-05-13 13:54:04.493264', NULL, 65, '127.0.0.1', '查询日程列表', 'SCHEDULE_LIST', 'GET', 'page=0&size=10', '/api/admin/schedule/list', 1);
INSERT INTO `admin_operation_log` VALUES (16, -1, 'admin_admin_ad', '2026-05-13 13:54:05.577491', NULL, 23, '127.0.0.1', '查询动漫列表', 'ANIME_LIST', 'GET', 'page=0&size=10', '/api/admin/anime/list', 1);
INSERT INTO `admin_operation_log` VALUES (17, -1, 'admin_admin_ad', '2026-05-13 16:04:24.277577', NULL, 19, '127.0.0.1', '查询动漫列表', 'ANIME_LIST', 'GET', 'page=1&size=10', '/api/admin/anime/list', 1);
INSERT INTO `admin_operation_log` VALUES (18, -1, 'admin_admin_ad', '2026-05-13 16:04:25.390494', NULL, 6, '127.0.0.1', '查询动漫列表', 'ANIME_LIST', 'GET', 'page=2&size=10', '/api/admin/anime/list', 1);
INSERT INTO `admin_operation_log` VALUES (19, -1, 'admin_admin_ad', '2026-05-13 16:04:26.566065', NULL, 9, '127.0.0.1', '查询动漫列表', 'ANIME_LIST', 'GET', 'page=3&size=10', '/api/admin/anime/list', 1);
INSERT INTO `admin_operation_log` VALUES (20, -1, 'admin_admin_ad', '2026-05-13 16:04:28.546067', NULL, 10, '127.0.0.1', '查询用户列表', 'USER_LIST', 'GET', 'page=0&size=10', '/api/admin/user/list', 1);
INSERT INTO `admin_operation_log` VALUES (21, -1, 'admin_admin_ad', '2026-05-13 16:04:33.936559', NULL, 7, '127.0.0.1', '查询日程列表', 'SCHEDULE_LIST', 'GET', 'page=0&size=10', '/api/admin/schedule/list', 1);
INSERT INTO `admin_operation_log` VALUES (22, -1, 'admin_admin_ad', '2026-05-13 16:04:35.721377', NULL, 73, '127.0.0.1', '查询评论列表', 'REVIEW_LIST', 'GET', 'page=0&size=10', '/api/admin/review/list', 1);
INSERT INTO `admin_operation_log` VALUES (23, -1, 'admin_admin_ad', '2026-05-13 16:04:43.999408', NULL, 7, '127.0.0.1', '查询用户列表', 'USER_LIST', 'GET', 'page=0&size=10', '/api/admin/user/list', 1);
INSERT INTO `admin_operation_log` VALUES (24, -1, 'admin_admin_ad', '2026-05-13 16:04:44.407171', NULL, 14, '127.0.0.1', '查询动漫列表', 'ANIME_LIST', 'GET', 'page=0&size=10', '/api/admin/anime/list', 1);
INSERT INTO `admin_operation_log` VALUES (25, -1, 'admin_admin_ad', '2026-05-13 16:04:44.877096', NULL, 102, '127.0.0.1', '获取仪表盘数据', 'DASHBOARD_DATA', 'GET', '', '/api/admin/dashboard', 1);

-- ----------------------------
-- Table structure for anime
-- ----------------------------
DROP TABLE IF EXISTS `anime`;
CREATE TABLE `anime`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `cover_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `score` decimal(3, 1) NULL DEFAULT 0.0,
  `episode_count` int NULL DEFAULT 0,
  `status` int NULL DEFAULT 0 COMMENT '0:连载,1:完结,2:未开播',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `heat` int NULL DEFAULT 0,
  `release_year` int NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `recommend_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of anime
-- ----------------------------
INSERT INTO `anime` VALUES (1, '鬼灭之刃', '/images/1.webp', '日本漫画家吾峠呼世晴所著的奇幻漫画', 9.5, 26, 1, '热血,战斗,奇幻,友情', 11158, 2019, '2026-01-10 11:06:40', '2026-06-04 00:39:38', NULL);
INSERT INTO `anime` VALUES (2, '咒术回战', '/images/2.webp', '日本漫画家芥见下下创作的漫画作品', 9.2, 24, 0, '热血,战斗,校园,超能力', 13110, 2020, '2026-01-10 11:06:40', '2026-06-04 00:39:38', NULL);
INSERT INTO `anime` VALUES (3, '别当欧尼酱了！', '/images/3.webp', '日本漫画家猫豆腐创作的漫画作品', 9.8, 75, 1, '日常,搞笑,奇幻', 12112, 2013, '2026-01-10 11:06:40', '2026-06-04 00:41:09', NULL);
INSERT INTO `anime` VALUES (4, '间谍过家家', '/images/4.webp', '日本漫画家远藤达哉创作的漫画作品', 9.1, 25, 0, '喜剧,家庭,间谍,日常', 7000, 2022, '2026-01-10 11:06:40', '2026-05-14 11:01:09', NULL);
INSERT INTO `anime` VALUES (5, 'BanG Dream！', '/images/5.webp', '以次时代少女乐队为主题的跨媒体企划', 8.9, 138, 0, '音乐,校园,社团,日常', 6013, 2016, '2026-01-10 11:06:40', '2026-06-04 00:39:38', NULL);
INSERT INTO `anime` VALUES (6, '航海王', '/images/6.webp', '日本漫画家芥见下下创作的漫画作品', 9.7, 1100, 0, '热血,冒险,友谊,梦想', 15004, 1999, '2026-01-10 11:06:40', '2026-05-14 15:04:10', NULL);
INSERT INTO `anime` VALUES (7, '火影忍者', '/images/7.webp', '岸本齐史创作的忍者题材漫画', 9.6, 720, 1, '热血,忍者,战斗,成长', 13000, 2002, '2026-01-10 11:06:40', '2026-05-14 11:01:09', NULL);
INSERT INTO `anime` VALUES (8, 'BanG Dream！第二季', '/images/8.webp', '以次时代少女乐队为主题的跨媒体企划', 9.0, 366, 1, '音乐,校园,社团,日常', 5001, 2004, '2026-01-10 11:06:40', '2026-05-22 13:39:22', NULL);
INSERT INTO `anime` VALUES (9, 'BanG Dream! 梦想协奏曲 第三季 ', '/images/9.webp', '以次时代少女乐队为主题的跨媒体企划', 9.9, 64, 1, '音乐,校园,社团,日常', 9000, 2003, '2026-01-10 11:06:40', '2026-05-14 12:19:25', NULL);
INSERT INTO `anime` VALUES (10, '命运石之门', '/images/10.webp', '5pb.制作的科学幻想系列游戏', 9.7, 24, 1, '科幻,悬疑,时间旅行', 4000, 2011, '2026-01-10 11:06:40', '2026-05-14 11:01:09', NULL);
INSERT INTO `anime` VALUES (11, '鬼灭之刃 游郭篇', '/images/11.webp', '灶门炭治郎与同伴前往游郭调查', 9.5, 11, 1, '热血,战斗,奇幻', 10000, 2021, '2026-01-10 11:24:20', '2026-05-14 11:01:09', NULL);
INSERT INTO `anime` VALUES (12, '间谍过家家', '/images/12.webp', '间谍、杀手、超能力者组成的伪装家庭', 9.2, 25, 0, '喜剧,家庭,间谍', 8500, 2022, '2026-01-10 11:24:20', '2026-05-14 11:01:09', NULL);
INSERT INTO `anime` VALUES (13, '链锯人', '/images/13.webp', '与链锯恶魔波奇塔一起成为恶魔猎人', 8.9, 12, 1, '黑暗,奇幻,战斗', 7500, 2022, '2026-01-10 11:24:20', '2026-05-14 11:01:09', NULL);
INSERT INTO `anime` VALUES (14, '孤独摇滚！', '/images/14.webp', '孤独少女组建乐队的成长故事', 9.0, 12, 1, '音乐,校园,治愈', 16016, 2022, '2026-01-10 11:24:20', '2026-06-04 00:40:31', NULL);
INSERT INTO `anime` VALUES (15, '葬送的芙莉莲', '/images/15.webp', '精灵魔法师芙莉莲的千年之旅', 9.3, 28, 0, '奇幻,冒险,治愈', 9000, 2023, '2026-01-10 11:24:20', '2026-05-14 11:01:09', NULL);
INSERT INTO `anime` VALUES (16, '测试新番：异世界程序员', '/images/16.webp', '程序员穿越到异世界，用代码拯救世界的奇幻故事', 8.5, 12, 0, '奇幻,穿越,程序员,搞笑', 5127, 2026, '2026-01-11 16:10:16', '2026-05-22 13:28:53', NULL);
INSERT INTO `anime` VALUES (17, '斗破苍穹', '/images/17.webp', '改编自天蚕土豆同名小说，少年萧炎的逆袭之路', 9.0, 96, 0, '热血,玄幻,战斗,国漫,修仙', 11230, 2017, '2026-04-01 18:22:34', '2026-06-04 00:39:38', NULL);
INSERT INTO `anime` VALUES (18, '斗罗大陆', '/images/18.webp', '唐门外门弟子唐三的斗罗世界冒险', 8.5, 263, 1, '热血,玄幻,战斗,国漫,穿越', 25014, 2018, '2026-04-01 18:22:34', '2026-05-22 13:31:50', NULL);
INSERT INTO `anime` VALUES (19, '完美世界', '/images/19.webp', '石昊从大荒走出的传奇故事', 8.5, 190, 0, '热血,玄幻,战斗,国漫,修仙', 18008, 2021, '2026-04-01 18:22:34', '2026-05-22 13:37:39', NULL);
INSERT INTO `anime` VALUES (20, '一人之下', '/images/20.webp', '现代都市中的异人世界，冯宝宝的奇妙冒险', 9.2, 48, 0, '热血,战斗,奇幻,国漫,搞笑', 12000, 2016, '2026-04-01 18:22:34', '2026-05-14 11:01:09', NULL);
INSERT INTO `anime` VALUES (21, '狐妖小红娘', '/images/21.webp', '涂山狐妖与人类转世续缘的感人故事', 9.1, 156, 0, '爱情,奇幻,国漫,治愈,战斗', 11000, 2015, '2026-04-01 18:22:34', '2026-05-14 11:01:09', NULL);
INSERT INTO `anime` VALUES (22, '灵笼', '/images/22.webp', '末日世界，人类最后的生存希望', 9.3, 16, 1, '科幻,末日,战斗,国漫,悬疑', 19501, 2019, '2026-04-01 18:22:34', '2026-06-04 00:40:39', NULL);
INSERT INTO `anime` VALUES (23, '凡人修仙传', '/images/23.webp', '凡人韩立的修仙之路，平凡中见伟大', 9.9, 86, 0, '修仙,战斗,国漫,奇幻', 58509, 2020, '2026-04-01 18:22:34', '2026-06-04 00:40:44', NULL);
INSERT INTO `anime` VALUES (24, '吞噬星空', '/images/24.webp', '罗峰在武者世界的崛起之路', 8.6, 130, 0, '科幻,战斗,国漫,热血', 7001, 2020, '2026-04-01 18:22:34', '2026-05-14 11:01:09', NULL);
INSERT INTO `anime` VALUES (25, 'Ave Mujica', '/images/25.webp', '「我说过了吧？请你们将剩余的人生交给我」 丰川祥子招募组建的乐队', 7.1, 12, 1, '音乐,校园,治愈,喜剧,少女乐队', 21223, 2022, '2026-04-01 18:22:34', '2026-06-04 00:40:20', NULL);
INSERT INTO `anime` VALUES (26, 'MyGO!!!!!', '/images/26.webp', '迷路少女们的乐队羁绊故事', 9.8, 13, 1, '音乐,校园,青春,少女乐队,剧情', 18164, 2023, '2026-04-01 18:22:34', '2026-06-04 00:40:52', NULL);
INSERT INTO `anime` VALUES (27, 'Girls Band Cry', '/images/27.webp', '少女们的摇滚乐队物语', 8.8, 13, 1, '音乐,摇滚,青春,少女乐队', 10000, 2024, '2026-04-01 18:22:34', '2026-05-14 12:19:56', NULL);
INSERT INTO `anime` VALUES (28, 'BanG Dream! 梦想协奏曲 电影演唱会', '/images/28.webp', '少女们追逐音乐梦想的故事', 8.5, 156, 0, '音乐,偶像,校园,少女乐队', 6000, 2017, '2026-04-01 18:22:34', '2026-05-14 11:01:10', NULL);
INSERT INTO `anime` VALUES (29, 'K-ON! 轻音少女', '/images/29.webp', '轻音部少女们的日常与音乐', 9.0, 36, 1, '音乐,校园,日常,治愈,少女乐队', 8500, 2009, '2026-04-01 18:22:34', '2026-05-14 11:01:10', NULL);
INSERT INTO `anime` VALUES (30, '佐贺偶像是传奇', '/images/30.webp', '僵尸偶像团体创造奇迹的故事', 8.9, 24, 1, '偶像,搞笑,僵尸,音乐,少女乐队', 7000, 2018, '2026-04-01 18:22:34', '2026-05-14 11:01:10', NULL);
INSERT INTO `anime` VALUES (31, '莉可丽丝', '/images/31.webp', '特工少女们的日常与战斗', 8.9, 13, 1, '战斗,日常,轻百合,原创', 8500, 2022, '2026-04-01 18:22:34', '2026-05-14 11:01:10', NULL);
INSERT INTO `anime` VALUES (32, '街角魔族', '/images/32.webp', '魔族少女与魔法少女的日常', 8.7, 24, 0, '日常,搞笑,奇幻,轻百合,治愈', 5500, 2019, '2026-04-01 18:22:34', '2026-05-14 11:01:10', NULL);
INSERT INTO `anime` VALUES (33, '安达与岛村', '/images/33.webp', '两位少女的青春恋爱故事', 8.6, 12, 1, '恋爱,校园,轻百合,治愈', 5000, 2020, '2026-04-01 18:22:34', '2026-05-14 11:01:10', NULL);
INSERT INTO `anime` VALUES (34, '转生王女与天才千金的魔法革命', '/images/34.webp', '王女与天才少女的魔法冒险', 8.4, 12, 1, '奇幻,战斗,轻百合,恋爱', 5800, 2023, '2026-04-01 18:22:34', '2026-05-14 11:01:10', NULL);
INSERT INTO `anime` VALUES (35, '摇曳露营△', '/images/35.webp', '少女们的户外露营日常', 9.1, 25, 0, '日常,治愈,轻百合,户外', 7000, 2018, '2026-04-01 18:22:34', '2026-05-14 11:01:10', NULL);
INSERT INTO `anime` VALUES (36, '向山进发', '/images/36.webp', '少女们的登山物语', 8.5, 52, 0, '日常,治愈,轻百合,运动', 4500, 2013, '2026-04-01 18:22:34', '2026-05-14 11:01:10', NULL);
INSERT INTO `anime` VALUES (37, 'Slow Loop', '/images/37.webp', '钓鱼少女的温馨日常', 8.3, 12, 1, '日常,治愈,轻百合,钓鱼', 4000, 2022, '2026-04-01 18:22:34', '2026-05-14 11:01:10', NULL);
INSERT INTO `anime` VALUES (38, '明日酱的水手服', '/images/38.webp', '少女的青春校园故事', 8.6, 12, 1, '校园,日常,轻百合,治愈', 4800, 2022, '2026-04-01 18:22:34', '2026-05-14 11:01:10', NULL);

-- ----------------------------
-- Table structure for anime_schedule
-- ----------------------------
DROP TABLE IF EXISTS `anime_schedule`;
CREATE TABLE `anime_schedule`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `anime_id` bigint NOT NULL,
  `episode` int NOT NULL,
  `air_time` datetime NOT NULL,
  `platform` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `platform_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` int NULL DEFAULT 0 COMMENT '0:未播,1:已播',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_air_time`(`air_time` ASC) USING BTREE,
  INDEX `idx_anime_id`(`anime_id` ASC) USING BTREE,
  CONSTRAINT `anime_schedule_ibfk_1` FOREIGN KEY (`anime_id`) REFERENCES `anime` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of anime_schedule
-- ----------------------------
INSERT INTO `anime_schedule` VALUES (1, 1, 1, '2019-04-06 00:00:00', 'Bilibili', 'https://www.bilibili.com/bangumi/play/ss12345', 1, '2026-01-10 11:24:20');
INSERT INTO `anime_schedule` VALUES (2, 1, 2, '2019-04-06 00:00:00', 'Bilibili', 'https://www.bilibili.com/bangumi/play/ss12345', 1, '2026-01-10 11:24:20');
INSERT INTO `anime_schedule` VALUES (3, 2, 10, '2020-10-03 00:00:00', 'Bilibili', 'https://www.bilibili.com/bangumi/play/ss23456', 1, '2026-01-10 11:24:20');
INSERT INTO `anime_schedule` VALUES (4, 2, 11, '2020-10-03 00:00:00', 'Bilibili', 'https://www.bilibili.com/bangumi/play/ss23456', 0, '2026-01-10 11:24:20');
INSERT INTO `anime_schedule` VALUES (5, 3, 5, '2013-04-07 00:00:00', '爱奇艺', 'https://www.iqiyi.com/xxx', 1, '2026-01-10 11:24:20');
INSERT INTO `anime_schedule` VALUES (6, 4, 8, '2022-04-09 00:00:00', '腾讯视频', 'https://v.qq.com/xxx', 1, '2026-01-10 11:24:20');
INSERT INTO `anime_schedule` VALUES (7, 16, 1, '2026-05-22 00:00:00', 'Bilibili', 'https://www.bilibili.com/bangumi/play/ss99999', 0, '2026-01-11 16:10:16');
INSERT INTO `anime_schedule` VALUES (8, 16, 2, '2026-05-23 00:00:00', 'Bilibili', 'https://www.bilibili.com/bangumi/play/ss99999', 0, '2026-01-11 16:10:16');
INSERT INTO `anime_schedule` VALUES (9, 16, 3, '2026-05-24 00:00:00', 'Bilibili', 'https://www.bilibili.com/bangumi/play/ss99999', 0, '2026-01-11 16:10:16');
INSERT INTO `anime_schedule` VALUES (10, 5, 1, '2016-04-03 00:00:00', 'Bilibili', 'https://www.bilibili.com/bangumi/play/ss55555', 1, '2026-05-13 21:11:10');
INSERT INTO `anime_schedule` VALUES (11, 6, 1, '1999-10-20 00:00:00', '腾讯视频', 'https://v.qq.com/xxx', 1, '2026-05-13 21:11:10');
INSERT INTO `anime_schedule` VALUES (12, 7, 1, '2002-10-03 00:00:00', '爱奇艺', 'https://www.iqiyi.com/xxx', 1, '2026-05-13 21:11:10');
INSERT INTO `anime_schedule` VALUES (13, 8, 1, '2004-10-05 00:00:00', 'Bilibili', 'https://www.bilibili.com/bangumi/play/ss88888', 1, '2026-05-13 21:11:10');
INSERT INTO `anime_schedule` VALUES (14, 9, 1, '2003-10-04 00:00:00', 'Bilibili', 'https://www.bilibili.com/bangumi/play/ss99999', 1, '2026-05-13 21:11:10');
INSERT INTO `anime_schedule` VALUES (15, 10, 1, '2011-04-06 00:00:00', 'Bilibili', 'https://www.bilibili.com/bangumi/play/ss101010', 1, '2026-05-13 21:11:10');
INSERT INTO `anime_schedule` VALUES (16, 11, 1, '2021-12-05 00:00:00', 'Bilibili', 'https://www.bilibili.com/bangumi/play/ss111111', 1, '2026-05-13 21:11:10');
INSERT INTO `anime_schedule` VALUES (17, 12, 1, '2022-01-08 00:00:00', 'Bilibili', 'https://www.bilibili.com/bangumi/play/ss121212', 1, '2026-05-13 21:11:10');
INSERT INTO `anime_schedule` VALUES (18, 13, 1, '2022-10-11 00:00:00', 'Bilibili', 'https://www.bilibili.com/bangumi/play/ss131313', 1, '2026-05-13 21:11:10');
INSERT INTO `anime_schedule` VALUES (19, 14, 1, '2022-10-08 00:00:00', 'Bilibili', 'https://www.bilibili.com/bangumi/play/ss141414', 1, '2026-05-13 21:11:10');
INSERT INTO `anime_schedule` VALUES (20, 15, 1, '2023-09-29 00:00:00', 'Bilibili', 'https://www.bilibili.com/bangumi/play/ss151515', 1, '2026-05-13 21:11:10');

-- ----------------------------
-- Table structure for recommendation
-- ----------------------------
DROP TABLE IF EXISTS `recommendation`;
CREATE TABLE `recommendation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `anime_id` bigint NOT NULL,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `is_clicked` bit(1) NULL DEFAULT NULL,
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `score` decimal(5, 4) NULL DEFAULT NULL,
  `type` int NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 261 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recommendation
-- ----------------------------
INSERT INTO `recommendation` VALUES (1, 6, '2026-01-10 16:00:36.691507', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (2, 7, '2026-01-10 16:00:36.691507', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (3, 3, '2026-01-10 16:00:36.691507', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (4, 1, '2026-01-10 16:00:36.691507', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (5, 11, '2026-01-10 16:00:36.691507', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (6, 9, '2026-01-10 16:00:36.691507', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (7, 15, '2026-01-10 16:00:36.691507', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (8, 12, '2026-01-10 16:00:36.691507', b'0', '当前热门动漫', 0.8500, 3, 3);
INSERT INTO `recommendation` VALUES (9, 2, '2026-01-10 16:00:36.691507', b'0', '当前热门动漫', 0.8000, 3, 3);
INSERT INTO `recommendation` VALUES (10, 13, '2026-01-10 16:00:36.691507', b'0', '当前热门动漫', 0.7500, 3, 3);
INSERT INTO `recommendation` VALUES (11, 6, '2026-01-10 16:00:45.242129', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (12, 7, '2026-01-10 16:00:45.242129', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (13, 3, '2026-01-10 16:00:45.242129', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (14, 1, '2026-01-10 16:00:45.242129', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (15, 11, '2026-01-10 16:00:45.242129', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (16, 9, '2026-01-10 16:00:45.242129', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (17, 15, '2026-01-10 16:00:45.242129', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (18, 12, '2026-01-10 16:00:45.242129', b'0', '当前热门动漫', 0.8500, 3, 3);
INSERT INTO `recommendation` VALUES (19, 2, '2026-01-10 16:00:45.242129', b'0', '当前热门动漫', 0.8000, 3, 3);
INSERT INTO `recommendation` VALUES (20, 13, '2026-01-10 16:00:45.242129', b'0', '当前热门动漫', 0.7500, 3, 3);
INSERT INTO `recommendation` VALUES (21, 6, '2026-01-10 16:06:30.578811', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (22, 7, '2026-01-10 16:06:30.578811', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (23, 3, '2026-01-10 16:06:30.578811', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (24, 1, '2026-01-10 16:06:30.578811', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (25, 11, '2026-01-10 16:06:30.578811', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (26, 9, '2026-01-10 16:06:30.578811', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (27, 15, '2026-01-10 16:06:30.578811', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (28, 12, '2026-01-10 16:06:30.578811', b'0', '当前热门动漫', 0.8500, 3, 3);
INSERT INTO `recommendation` VALUES (29, 2, '2026-01-10 16:06:30.578811', b'0', '当前热门动漫', 0.8000, 3, 3);
INSERT INTO `recommendation` VALUES (30, 13, '2026-01-10 16:06:30.578811', b'0', '当前热门动漫', 0.7500, 3, 3);
INSERT INTO `recommendation` VALUES (31, 6, '2026-01-10 16:06:50.523955', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (32, 7, '2026-01-10 16:06:50.523955', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (33, 3, '2026-01-10 16:06:50.523955', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (34, 1, '2026-01-10 16:06:50.523955', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (35, 11, '2026-01-10 16:06:50.523955', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (36, 9, '2026-01-10 16:06:50.523955', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (37, 15, '2026-01-10 16:06:50.523955', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (38, 12, '2026-01-10 16:06:50.523955', b'0', '当前热门动漫', 0.8500, 3, 3);
INSERT INTO `recommendation` VALUES (39, 2, '2026-01-10 16:06:50.523955', b'0', '当前热门动漫', 0.8000, 3, 3);
INSERT INTO `recommendation` VALUES (40, 13, '2026-01-10 16:06:50.523955', b'0', '当前热门动漫', 0.7500, 3, 3);
INSERT INTO `recommendation` VALUES (41, 6, '2026-01-10 16:09:45.104299', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (42, 7, '2026-01-10 16:09:45.104299', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (43, 3, '2026-01-10 16:09:45.104299', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (44, 1, '2026-01-10 16:09:45.104299', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (45, 11, '2026-01-10 16:09:45.104299', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (46, 9, '2026-01-10 16:09:45.104299', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (47, 15, '2026-01-10 16:09:45.104299', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (48, 12, '2026-01-10 16:09:45.104299', b'0', '当前热门动漫', 0.8500, 3, 3);
INSERT INTO `recommendation` VALUES (49, 2, '2026-01-10 16:09:45.104299', b'0', '当前热门动漫', 0.8000, 3, 3);
INSERT INTO `recommendation` VALUES (50, 13, '2026-01-10 16:09:45.104299', b'0', '当前热门动漫', 0.7500, 3, 3);
INSERT INTO `recommendation` VALUES (51, 6, '2026-01-10 16:10:08.573647', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (52, 7, '2026-01-10 16:10:08.573647', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (53, 3, '2026-01-10 16:10:08.573647', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (54, 1, '2026-01-10 16:10:08.573647', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (55, 11, '2026-01-10 16:10:08.573647', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (56, 9, '2026-01-10 16:10:08.573647', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (57, 15, '2026-01-10 16:10:08.573647', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (58, 12, '2026-01-10 16:10:08.573647', b'0', '当前热门动漫', 0.8500, 3, 3);
INSERT INTO `recommendation` VALUES (59, 2, '2026-01-10 16:10:08.573647', b'0', '当前热门动漫', 0.8000, 3, 3);
INSERT INTO `recommendation` VALUES (60, 13, '2026-01-10 16:10:08.573647', b'0', '当前热门动漫', 0.7500, 3, 3);
INSERT INTO `recommendation` VALUES (61, 6, '2026-01-10 16:20:30.342895', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (62, 7, '2026-01-10 16:20:30.342895', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (63, 3, '2026-01-10 16:20:30.342895', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (64, 1, '2026-01-10 16:20:30.342895', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (65, 11, '2026-01-10 16:20:30.342895', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (66, 9, '2026-01-10 16:20:30.342895', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (67, 6, '2026-01-10 16:23:05.556328', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (68, 7, '2026-01-10 16:23:05.556328', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (69, 3, '2026-01-10 16:23:05.556328', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (70, 1, '2026-01-10 16:23:05.556328', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (71, 11, '2026-01-10 16:23:05.556328', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (72, 9, '2026-01-10 16:23:05.556328', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (73, 6, '2026-01-10 16:29:40.495212', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (74, 7, '2026-01-10 16:29:40.495212', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (75, 3, '2026-01-10 16:29:40.495212', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (76, 1, '2026-01-10 16:29:40.495212', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (77, 11, '2026-01-10 16:29:40.495212', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (78, 9, '2026-01-10 16:29:40.495212', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (79, 6, '2026-01-10 16:34:17.626746', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (80, 7, '2026-01-10 16:34:17.626746', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (81, 3, '2026-01-10 16:34:17.626746', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (82, 1, '2026-01-10 16:34:17.626746', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (83, 11, '2026-01-10 16:34:17.626746', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (84, 9, '2026-01-10 16:34:17.626746', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (85, 6, '2026-01-10 16:34:41.913053', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (86, 7, '2026-01-10 16:34:41.913053', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (87, 3, '2026-01-10 16:34:41.913053', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (88, 1, '2026-01-10 16:34:41.913053', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (89, 11, '2026-01-10 16:34:41.913053', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (90, 9, '2026-01-10 16:34:41.913053', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (91, 6, '2026-01-10 16:40:00.583390', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (92, 7, '2026-01-10 16:40:00.583390', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (93, 3, '2026-01-10 16:40:00.583390', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (94, 1, '2026-01-10 16:40:00.583390', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (95, 11, '2026-01-10 16:40:00.583390', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (96, 9, '2026-01-10 16:40:00.583390', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (97, 6, '2026-01-10 16:40:09.315029', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (98, 7, '2026-01-10 16:40:09.315029', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (99, 3, '2026-01-10 16:40:09.315029', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (100, 1, '2026-01-10 16:40:09.315029', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (101, 11, '2026-01-10 16:40:09.315029', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (102, 9, '2026-01-10 16:40:09.315029', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (103, 6, '2026-01-10 16:40:22.581902', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (104, 7, '2026-01-10 16:40:22.581902', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (105, 3, '2026-01-10 16:40:22.581902', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (106, 1, '2026-01-10 16:40:22.581902', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (107, 11, '2026-01-10 16:40:22.581902', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (108, 9, '2026-01-10 16:40:22.581902', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (109, 6, '2026-01-10 16:40:24.272598', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (110, 7, '2026-01-10 16:40:24.272598', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (111, 3, '2026-01-10 16:40:24.272598', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (112, 1, '2026-01-10 16:40:24.272598', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (113, 11, '2026-01-10 16:40:24.272598', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (114, 9, '2026-01-10 16:40:24.272598', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (115, 6, '2026-01-10 16:40:28.368066', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (116, 7, '2026-01-10 16:40:28.368066', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (117, 3, '2026-01-10 16:40:28.368066', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (118, 1, '2026-01-10 16:40:28.368066', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (119, 11, '2026-01-10 16:40:28.368066', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (120, 9, '2026-01-10 16:40:28.368066', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (121, 6, '2026-01-10 16:54:16.277317', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (122, 7, '2026-01-10 16:54:16.277317', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (123, 3, '2026-01-10 16:54:16.277317', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (124, 1, '2026-01-10 16:54:16.277317', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (125, 11, '2026-01-10 16:54:16.277317', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (126, 6, '2026-01-10 16:54:16.291211', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (127, 7, '2026-01-10 16:54:16.291211', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (128, 3, '2026-01-10 16:54:16.291211', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (129, 1, '2026-01-10 16:54:16.291211', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (130, 11, '2026-01-10 16:54:16.291211', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (131, 6, '2026-01-10 16:54:27.679850', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (132, 7, '2026-01-10 16:54:27.679850', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (133, 3, '2026-01-10 16:54:27.679850', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (134, 1, '2026-01-10 16:54:27.679850', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (135, 11, '2026-01-10 16:54:27.679850', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (136, 6, '2026-01-10 16:55:14.017008', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (137, 7, '2026-01-10 16:55:14.017008', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (138, 3, '2026-01-10 16:55:14.017008', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (139, 1, '2026-01-10 16:55:14.017008', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (140, 11, '2026-01-10 16:55:14.017008', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (141, 6, '2026-01-10 16:55:51.186974', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (142, 7, '2026-01-10 16:55:51.186974', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (143, 3, '2026-01-10 16:55:51.186974', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (144, 1, '2026-01-10 16:55:51.186974', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (145, 11, '2026-01-10 16:55:51.186974', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (146, 6, '2026-01-10 16:55:53.071044', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (147, 7, '2026-01-10 16:55:53.071044', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (148, 3, '2026-01-10 16:55:53.071044', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (149, 1, '2026-01-10 16:55:53.071044', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (150, 11, '2026-01-10 16:55:53.071044', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (151, 6, '2026-01-10 16:55:57.742803', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (152, 7, '2026-01-10 16:55:57.742803', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (153, 3, '2026-01-10 16:55:57.742803', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (154, 1, '2026-01-10 16:55:57.742803', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (155, 11, '2026-01-10 16:55:57.742803', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (156, 6, '2026-01-10 16:56:02.456579', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (157, 7, '2026-01-10 16:56:02.456579', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (158, 3, '2026-01-10 16:56:02.456579', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (159, 1, '2026-01-10 16:56:02.456579', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (160, 11, '2026-01-10 16:56:02.456579', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (161, 6, '2026-01-10 16:56:05.579485', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (162, 7, '2026-01-10 16:56:05.579485', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (163, 3, '2026-01-10 16:56:05.579485', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (164, 1, '2026-01-10 16:56:05.579485', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (165, 11, '2026-01-10 16:56:05.579485', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (166, 6, '2026-01-10 16:56:07.186908', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (167, 7, '2026-01-10 16:56:07.186908', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (168, 3, '2026-01-10 16:56:07.186908', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (169, 1, '2026-01-10 16:56:07.186908', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (170, 11, '2026-01-10 16:56:07.186908', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (171, 6, '2026-01-10 16:56:10.727088', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (172, 7, '2026-01-10 16:56:10.727088', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (173, 3, '2026-01-10 16:56:10.727088', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (174, 1, '2026-01-10 16:56:10.727088', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (175, 11, '2026-01-10 16:56:10.727088', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (176, 6, '2026-01-10 16:56:18.798590', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (177, 7, '2026-01-10 16:56:18.798590', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (178, 3, '2026-01-10 16:56:18.798590', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (179, 1, '2026-01-10 16:56:18.798590', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (180, 11, '2026-01-10 16:56:18.798590', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (181, 6, '2026-01-11 10:38:46.197117', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (182, 7, '2026-01-11 10:38:46.197117', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (183, 3, '2026-01-11 10:38:46.197117', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (184, 1, '2026-01-11 10:38:46.197117', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (185, 11, '2026-01-11 10:38:46.197117', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (186, 11, '2026-01-11 14:37:48.338945', b'0', '因为你喜欢《鬼灭之刃》; 因为你喜欢《咒术回战》', 0.7485, 2, 1);
INSERT INTO `recommendation` VALUES (187, 5, '2026-01-11 14:37:48.340048', b'0', '因为你喜欢《鬼灭之刃》; 因为你喜欢《咒术回战》', 0.7363, 2, 1);
INSERT INTO `recommendation` VALUES (188, 3, '2026-01-11 14:37:48.340048', b'0', '因为你喜欢《鬼灭之刃》; 因为你喜欢《咒术回战》', 0.5763, 2, 1);
INSERT INTO `recommendation` VALUES (189, 8, '2026-01-11 14:37:48.340048', b'0', '因为你喜欢《鬼灭之刃》', 0.5700, 2, 1);
INSERT INTO `recommendation` VALUES (190, 13, '2026-01-11 14:37:48.340048', b'0', '因为你喜欢《鬼灭之刃》; 因为你喜欢《咒术回战》', 0.5647, 2, 1);
INSERT INTO `recommendation` VALUES (191, 6, '2026-01-11 15:59:24.742574', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (192, 7, '2026-01-11 15:59:24.742574', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (193, 3, '2026-01-11 15:59:24.742574', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (194, 1, '2026-01-11 15:59:24.742574', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (195, 11, '2026-01-11 15:59:24.742574', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (196, 6, '2026-01-11 16:57:26.460860', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (197, 6, '2026-01-11 16:57:26.460860', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (198, 7, '2026-01-11 16:57:26.460860', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (199, 7, '2026-01-11 16:57:26.460860', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (200, 3, '2026-01-11 16:57:26.460860', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (201, 3, '2026-01-11 16:57:26.460860', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (202, 1, '2026-01-11 16:57:26.460860', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (203, 1, '2026-01-11 16:57:26.460860', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (204, 11, '2026-01-11 16:57:26.460860', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (205, 11, '2026-01-11 16:57:26.460860', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (206, 9, '2026-01-11 16:57:26.460860', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (207, 9, '2026-01-11 16:57:26.460860', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (208, 6, '2026-01-11 16:57:37.825952', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (209, 7, '2026-01-11 16:57:37.825952', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (210, 3, '2026-01-11 16:57:37.825952', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (211, 1, '2026-01-11 16:57:37.825952', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (212, 11, '2026-01-11 16:57:37.825952', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (213, 9, '2026-01-11 16:57:37.825952', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (214, 6, '2026-01-11 16:57:52.335902', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (215, 7, '2026-01-11 16:57:52.335902', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (216, 3, '2026-01-11 16:57:52.335902', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (217, 1, '2026-01-11 16:57:52.335902', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (218, 11, '2026-01-11 16:57:52.335902', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (219, 9, '2026-01-11 16:57:52.335902', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (220, 6, '2026-01-11 16:57:56.067290', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (221, 7, '2026-01-11 16:57:56.067290', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (222, 3, '2026-01-11 16:57:56.067290', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (223, 1, '2026-01-11 16:57:56.067290', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (224, 11, '2026-01-11 16:57:56.067290', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (225, 9, '2026-01-11 16:57:56.067290', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (226, 6, '2026-01-11 16:58:00.045820', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (227, 7, '2026-01-11 16:58:00.045820', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (228, 3, '2026-01-11 16:58:00.045820', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (229, 1, '2026-01-11 16:58:00.045820', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (230, 11, '2026-01-11 16:58:00.045820', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (231, 9, '2026-01-11 16:58:00.045820', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (232, 6, '2026-01-11 16:58:01.669113', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (233, 7, '2026-01-11 16:58:01.669113', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (234, 3, '2026-01-11 16:58:01.669113', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (235, 1, '2026-01-11 16:58:01.669113', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (236, 11, '2026-01-11 16:58:01.669113', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (237, 9, '2026-01-11 16:58:01.669113', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (238, 6, '2026-01-11 16:58:03.716318', b'0', '当前热门动漫', 1.5000, 3, 3);
INSERT INTO `recommendation` VALUES (239, 7, '2026-01-11 16:58:03.716318', b'0', '当前热门动漫', 1.3000, 3, 3);
INSERT INTO `recommendation` VALUES (240, 3, '2026-01-11 16:58:03.716318', b'0', '当前热门动漫', 1.2000, 3, 3);
INSERT INTO `recommendation` VALUES (241, 1, '2026-01-11 16:58:03.716318', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (242, 11, '2026-01-11 16:58:03.716318', b'0', '当前热门动漫', 1.0000, 3, 3);
INSERT INTO `recommendation` VALUES (243, 9, '2026-01-11 16:58:03.716318', b'0', '当前热门动漫', 0.9000, 3, 3);
INSERT INTO `recommendation` VALUES (244, 11, '2026-04-01 17:47:02.784522', b'0', '因为你喜欢《鬼灭之刃》; 因为你喜欢《咒术回战》; 因为你喜欢《进击的巨人》', 0.7227, 2, 1);
INSERT INTO `recommendation` VALUES (245, 5, '2026-04-01 17:47:02.784522', b'0', '因为你喜欢《鬼灭之刃》; 因为你喜欢《咒术回战》; 因为你喜欢《进击的巨人》', 0.6445, 2, 1);
INSERT INTO `recommendation` VALUES (246, 8, '2026-04-01 17:47:02.784522', b'0', '因为你喜欢《鬼灭之刃》; 因为你喜欢《进击的巨人》', 0.5710, 2, 1);
INSERT INTO `recommendation` VALUES (247, 11, '2026-04-01 18:18:03.386690', b'0', '因为你喜欢《鬼灭之刃》; 因为你喜欢《咒术回战》; 因为你喜欢《进击的巨人》', 0.7227, 2, 1);
INSERT INTO `recommendation` VALUES (248, 5, '2026-04-01 18:18:03.386690', b'0', '因为你喜欢《鬼灭之刃》; 因为你喜欢《咒术回战》; 因为你喜欢《进击的巨人》', 0.6445, 2, 1);
INSERT INTO `recommendation` VALUES (249, 8, '2026-04-01 18:18:03.386690', b'0', '因为你喜欢《鬼灭之刃》; 因为你喜欢《进击的巨人》', 0.5710, 2, 1);
INSERT INTO `recommendation` VALUES (250, 5, '2026-04-02 08:42:58.270911', b'0', '因为你喜欢《咒术回战》; 因为你喜欢《进击的巨人》', 0.7273, 2, 1);
INSERT INTO `recommendation` VALUES (251, 11, '2026-04-02 08:42:58.259286', b'0', '因为你喜欢《鬼灭之刃》; 因为你喜欢《咒术回战》; 因为你喜欢《进击的巨人》', 0.7227, 2, 1);
INSERT INTO `recommendation` VALUES (252, 20, '2026-04-02 08:42:58.259286', b'0', '因为你喜欢《鬼灭之刃》; 因为你喜欢《进击的巨人》', 0.6630, 2, 1);
INSERT INTO `recommendation` VALUES (253, 5, '2026-05-13 14:09:50.175532', b'0', '因为你喜欢《咒术回战》; 因为你喜欢《进击的巨人》', 0.7273, 2, 1);
INSERT INTO `recommendation` VALUES (254, 11, '2026-05-13 14:09:50.161814', b'0', '因为你喜欢《鬼灭之刃》; 因为你喜欢《咒术回战》; 因为你喜欢《进击的巨人》', 0.7227, 2, 1);
INSERT INTO `recommendation` VALUES (255, 20, '2026-05-13 14:09:50.161814', b'0', '因为你喜欢《鬼灭之刃》; 因为你喜欢《进击的巨人》', 0.6630, 2, 1);
INSERT INTO `recommendation` VALUES (256, 5, '2026-05-13 17:33:20.458532', b'0', '因为你喜欢《咒术回战》; 因为你喜欢《进击的巨人》', 0.7273, 2, 1);
INSERT INTO `recommendation` VALUES (257, 11, '2026-05-13 17:33:20.445597', b'0', '因为你喜欢《鬼灭之刃》; 因为你喜欢《咒术回战》; 因为你喜欢《进击的巨人》', 0.7227, 2, 1);
INSERT INTO `recommendation` VALUES (258, 20, '2026-05-13 17:33:20.445597', b'0', '因为你喜欢《鬼灭之刃》; 因为你喜欢《进击的巨人》', 0.6630, 2, 1);
INSERT INTO `recommendation` VALUES (259, 24, '2026-05-13 17:33:20.458532', b'0', '因为你喜欢《咒术回战》', 0.6307, 2, 1);
INSERT INTO `recommendation` VALUES (260, 23, '2026-05-13 17:33:20.445597', b'0', '因为你喜欢《鬼灭之刃》', 0.6167, 2, 1);

-- ----------------------------
-- Table structure for review
-- ----------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `anime_id` bigint NOT NULL,
  `rating` decimal(3, 1) NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `like_count` int NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `parent_id` bigint NULL DEFAULT NULL,
  `reply_user_id` bigint NULL DEFAULT NULL,
  `reply_username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `recommend_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_anime_id`(`anime_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `FKiyf57dy48lyiftdrf7y87rnxi` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `review_ibfk_1` FOREIGN KEY (`anime_id`) REFERENCES `anime` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of review
-- ----------------------------
INSERT INTO `review` VALUES (1, 1, 1, 5.0, '作画和剧情都太棒了！', 12, '2026-01-09 11:24:20', NULL, NULL, NULL, NULL);
INSERT INTO `review` VALUES (2, 2, 1, 4.5, '打斗场面很精彩', 3, '2026-01-10 11:24:20', NULL, NULL, NULL, NULL);
INSERT INTO `review` VALUES (3, 1, 2, 4.8, '每周的快乐源泉', 5, '2026-01-10 11:24:21', NULL, NULL, NULL, NULL);
INSERT INTO `review` VALUES (4, 2, 3, 4.0, '无殖转生hhhhh', 1, '2026-01-10 11:24:21', NULL, NULL, NULL, NULL);
INSERT INTO `review` VALUES (5, 1, 1, 5.0, '????', 1, '2026-01-09 14:38:05', NULL, NULL, NULL, NULL);
INSERT INTO `review` VALUES (6, 1, 1, 4.5, '很好看', 1, '2026-04-01 14:39:12', NULL, NULL, NULL, NULL);
INSERT INTO `review` VALUES (7, 1, 2, 4.0, '不错', 3, '2026-04-01 14:39:13', NULL, NULL, NULL, NULL);
INSERT INTO `review` VALUES (8, 1, 3, 5.0, '神作', 1, '2026-04-01 14:39:14', NULL, NULL, NULL, NULL);
INSERT INTO `review` VALUES (9, 3, 26, 5.0, '我可能一辈子都忘不了CRYCHIC了QAQ', 12, '2026-04-01 18:25:22', NULL, NULL, NULL, NULL);
INSERT INTO `review` VALUES (10, 3, 17, 5.0, '123', 6, '2026-04-02 08:38:06', NULL, NULL, NULL, NULL);
INSERT INTO `review` VALUES (11, 3, 25, 4.0, '这期神了！，你问神在哪里，神在丰川祥子身上', 1, '2026-05-14 12:33:41', NULL, NULL, NULL, NULL);
INSERT INTO `review` VALUES (12, 3, 14, 5.0, '神了！！！', 0, '2026-05-22 13:13:17', NULL, NULL, NULL, NULL);
INSERT INTO `review` VALUES (13, 3, 5, 4.0, '1111111', 0, '2026-05-22 13:38:06', NULL, NULL, NULL, NULL);
INSERT INTO `review` VALUES (17, 3, 5, 5.0, '22222', 0, '2026-05-22 13:40:46', NULL, NULL, NULL, NULL);
INSERT INTO `review` VALUES (18, 3, 23, 5.0, '<script>alert(\'xss\')</script>', 0, '2026-05-22 14:07:40', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `openid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `avatar_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `gender` int NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `session_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `openid`(`openid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, NULL, '动漫爱好者', 'https://img.example.com/avatar1.jpg', 0, '2026-01-10 11:24:20', '2026-01-10 11:24:20', NULL, '', NULL);
INSERT INTO `user` VALUES (2, NULL, '番剧达人', 'https://img.example.com/avatar2.jpg', 0, '2026-01-10 11:24:20', '2026-01-10 11:24:20', NULL, '', NULL);
INSERT INTO `user` VALUES (3, 'oLGyE1yuYrQtoxol30pveV89RS4A', '动漫爱好者6689', NULL, 0, '2026-01-10 16:00:17', '2026-06-04 00:36:14', NULL, 'wx_oLGyE1yuYr', 'nh1/WjVJXp1ZgigpPAombg==');
INSERT INTO `user` VALUES (4, NULL, '测试用户', NULL, 0, '2026-04-01 14:39:12', '2026-04-01 14:39:12', NULL, 'testuser', NULL);

-- ----------------------------
-- Table structure for user_profile
-- ----------------------------
DROP TABLE IF EXISTS `user_profile`;
CREATE TABLE `user_profile`  (
  `user_id` bigint NOT NULL,
  `avg_score` double NULL DEFAULT NULL,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `favorite_categories` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `favorite_tags` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `total_favorites` int NULL DEFAULT NULL,
  `total_views` int NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `wish_list` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  PRIMARY KEY (`user_id`) USING BTREE,
  CONSTRAINT `FK6kwj5lk78pnhwor4pgosvb51r` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_profile
-- ----------------------------
INSERT INTO `user_profile` VALUES (1, 4.7, '2026-04-01 15:32:54.669248', '{\"超能力\":0.09801262366117973,\"校园\":0.09801262366117973,\"友情\":0.10776855291185013,\"战斗\":0.20578117657302988,\"热血\":0.20578117657302988,\"搞笑\":0.05895843123596014,\"日常\":0.05895843123596014,\"奇幻\":0.1667269841478103}', '{\"超能力\":0.09801262366117973,\"校园\":0.09801262366117973,\"友情\":0.10776855291185013,\"战斗\":0.20578117657302988,\"热血\":0.20578117657302988,\"搞笑\":0.05895843123596014,\"日常\":0.05895843123596014,\"奇幻\":0.1667269841478103}', 0, 2, '2026-05-22 13:42:01.449036', NULL);
INSERT INTO `user_profile` VALUES (3, 4.7, '2026-04-01 17:06:55.891866', '{\"治愈\":0.08420546920291008,\"校园\":0.18804981236233453,\"喜剧\":0.0319928808382743,\"青春\":0.009861684103080049,\"修仙\":0.062074272467715824,\"少女乐队\":0.041854564941354346,\"国漫\":0.062074272467715824,\"社团\":0.09398265905634441,\"音乐\":0.18804981236233453,\"战斗\":0.062074272467715824,\"剧情\":0.009861684103080049,\"玄幻\":0.009861684103080049,\"热血\":0.009861684103080049,\"奇幻\":0.05221258836463578,\"日常\":0.09398265905634441}', '{\"治愈\":0.08420546920291008,\"校园\":0.18804981236233453,\"喜剧\":0.0319928808382743,\"青春\":0.009861684103080049,\"修仙\":0.062074272467715824,\"少女乐队\":0.041854564941354346,\"国漫\":0.062074272467715824,\"社团\":0.09398265905634441,\"音乐\":0.18804981236233453,\"战斗\":0.062074272467715824,\"剧情\":0.009861684103080049,\"玄幻\":0.009861684103080049,\"热血\":0.009861684103080049,\"奇幻\":0.05221258836463578,\"日常\":0.09398265905634441}', 0, 0, '2026-05-22 14:07:39.928272', NULL);

-- ----------------------------
-- Table structure for user_reminder
-- ----------------------------
DROP TABLE IF EXISTS `user_reminder`;
CREATE TABLE `user_reminder`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `anime_id` bigint NOT NULL,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `remind_time` datetime(6) NULL DEFAULT NULL,
  `schedule_id` bigint NOT NULL,
  `status` tinyint NOT NULL DEFAULT 0,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_reminder
-- ----------------------------

-- ----------------------------
-- Table structure for watch_record
-- ----------------------------
DROP TABLE IF EXISTS `watch_record`;
CREATE TABLE `watch_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `anime_id` bigint NOT NULL,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `progress` int NULL DEFAULT NULL,
  `status` int NULL DEFAULT NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `watched_episodes` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK25faq814cu0iixd9dxo527hyw`(`anime_id` ASC) USING BTREE,
  INDEX `FKjpibhb10gccy794hceyxcy48c`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FK25faq814cu0iixd9dxo527hyw` FOREIGN KEY (`anime_id`) REFERENCES `anime` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKjpibhb10gccy794hceyxcy48c` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of watch_record
-- ----------------------------
INSERT INTO `watch_record` VALUES (1, 1, '2026-04-01 14:39:12.000000', NULL, 2, NULL, 1, NULL);
INSERT INTO `watch_record` VALUES (2, 2, '2026-04-01 14:39:12.000000', NULL, 2, NULL, 1, NULL);

-- ----------------------------
-- Table structure for wechat_subscription
-- ----------------------------
DROP TABLE IF EXISTS `wechat_subscription`;
CREATE TABLE `wechat_subscription`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NULL DEFAULT NULL,
  `enable_new_anime_notify` bit(1) NULL DEFAULT NULL,
  `enable_recommend_notify` bit(1) NULL DEFAULT NULL,
  `notify_minutes_before` int NULL DEFAULT NULL,
  `openid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `subscribed_anime_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `update_time` datetime(6) NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of wechat_subscription
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
