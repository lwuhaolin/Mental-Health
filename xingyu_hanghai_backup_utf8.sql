
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `xingyu_hanghai` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `xingyu_hanghai`;
DROP TABLE IF EXISTS `admins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admins` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密存储）',
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `role` enum('超级管理员','运营管理员','系统管理员') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `status` enum('正常','禁用') COLLATE utf8mb4_unicode_ci DEFAULT '正常' COMMENT '账号状态',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `idx_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `admins` WRITE;
/*!40000 ALTER TABLE `admins` DISABLE KEYS */;
INSERT INTO `admins` (`id`, `username`, `password`, `real_name`, `role`, `email`, `phone`, `status`, `last_login_time`, `create_time`, `update_time`) VALUES (1,'admin','admin123','系统管理员','超级管理员','admin@xingyu.com','13800138000','正常','2025-10-30 17:48:35','2025-10-15 15:23:52','2025-10-30 17:48:35');
INSERT INTO `admins` (`id`, `username`, `password`, `real_name`, `role`, `email`, `phone`, `status`, `last_login_time`, `create_time`, `update_time`) VALUES (2,'admin_operation','admin123','运营管理员','运营管理员','operation@xingyu.com','13800138202','正常',NULL,'2025-10-15 16:56:10','2025-10-15 16:56:10');
/*!40000 ALTER TABLE `admins` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `ai_interactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_interactions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '互动ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `interaction_type` enum('AI疗愈','AI评估','日常对话') COLLATE utf8mb4_unicode_ci DEFAULT '日常对话' COMMENT '互动类型',
  `user_message` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户消息',
  `ai_response` text COLLATE utf8mb4_unicode_ci COMMENT 'AI回复',
  `emotion_analysis` json DEFAULT NULL COMMENT '情绪分析结果（JSON格式）',
  `suggestion` text COLLATE utf8mb4_unicode_ci COMMENT 'AI建议',
  `session_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会话ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_interaction_type` (`interaction_type`),
  KEY `idx_session_id` (`session_id`),
  CONSTRAINT `ai_interactions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI互动记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `ai_interactions` WRITE;
/*!40000 ALTER TABLE `ai_interactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `ai_interactions` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `assessment_answers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assessment_answers` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '答案ID',
  `user_assessment_id` bigint NOT NULL COMMENT '测评记录ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `answer` text COLLATE utf8mb4_unicode_ci COMMENT '用户答案',
  `score` int DEFAULT NULL COMMENT '本题得分',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_assessment_id` (`user_assessment_id`),
  KEY `idx_question_id` (`question_id`),
  CONSTRAINT `assessment_answers_ibfk_1` FOREIGN KEY (`user_assessment_id`) REFERENCES `user_assessments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=298 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测评答案表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `assessment_answers` WRITE;
/*!40000 ALTER TABLE `assessment_answers` DISABLE KEYS */;
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (258,36,11,'2',2,'2025-10-27 00:24:27');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (259,36,12,'3',3,'2025-10-27 00:24:27');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (260,36,13,'3',3,'2025-10-27 00:24:27');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (261,36,14,'3',3,'2025-10-27 00:24:27');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (262,36,15,'3',3,'2025-10-27 00:24:27');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (263,36,16,'3',3,'2025-10-27 00:24:27');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (264,36,17,'3',3,'2025-10-27 00:24:27');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (265,36,18,'3',3,'2025-10-27 00:24:27');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (266,36,19,'3',3,'2025-10-27 00:24:27');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (267,36,20,'3',3,'2025-10-27 00:24:27');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (268,37,1,'3',3,'2025-10-27 00:26:56');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (269,37,2,'3',3,'2025-10-27 00:26:56');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (270,37,3,'3',3,'2025-10-27 00:26:56');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (271,37,4,'3',3,'2025-10-27 00:26:56');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (272,37,5,'3',3,'2025-10-27 00:26:56');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (273,37,6,'3',3,'2025-10-27 00:26:56');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (274,37,7,'3',3,'2025-10-27 00:26:56');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (275,37,8,'3',3,'2025-10-27 00:26:56');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (276,37,9,'3',3,'2025-10-27 00:26:56');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (277,37,10,'3',3,'2025-10-27 00:26:56');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (278,38,1,'3',3,'2025-10-27 00:34:21');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (279,38,2,'3',3,'2025-10-27 00:34:21');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (280,38,3,'3',3,'2025-10-27 00:34:21');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (281,38,4,'3',3,'2025-10-27 00:34:21');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (282,38,5,'3',3,'2025-10-27 00:34:21');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (283,38,6,'3',3,'2025-10-27 00:34:21');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (284,38,7,'3',3,'2025-10-27 00:34:21');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (285,38,8,'3',3,'2025-10-27 00:34:21');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (286,38,9,'3',3,'2025-10-27 00:34:21');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (287,38,10,'3',3,'2025-10-27 00:34:21');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (288,39,1,'2',2,'2025-10-27 00:39:32');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (289,39,2,'3',3,'2025-10-27 00:39:32');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (290,39,3,'3',3,'2025-10-27 00:39:32');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (291,39,4,'3',3,'2025-10-27 00:39:32');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (292,39,5,'3',3,'2025-10-27 00:39:32');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (293,39,6,'3',3,'2025-10-27 00:39:32');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (294,39,7,'3',3,'2025-10-27 00:39:32');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (295,39,8,'3',3,'2025-10-27 00:39:32');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (296,39,9,'3',3,'2025-10-27 00:39:32');
INSERT INTO `assessment_answers` (`id`, `user_assessment_id`, `question_id`, `answer`, `score`, `create_time`) VALUES (297,39,10,'3',3,'2025-10-27 00:39:32');
/*!40000 ALTER TABLE `assessment_answers` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `assessment_questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assessment_questions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `assessment_id` bigint NOT NULL COMMENT '????ID',
  `question_number` int NOT NULL COMMENT '????',
  `question_text` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '????',
  `question_type` enum('single_choice','multiple_choice','scale') COLLATE utf8mb4_unicode_ci DEFAULT 'single_choice' COMMENT '????',
  `options` json DEFAULT NULL COMMENT '???JSON???',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id`),
  KEY `idx_assessment_id` (`assessment_id`),
  KEY `idx_question_number` (`question_number`),
  CONSTRAINT `assessment_questions_ibfk_1` FOREIGN KEY (`assessment_id`) REFERENCES `assessments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测评题目表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `assessment_questions` WRITE;
/*!40000 ALTER TABLE `assessment_questions` DISABLE KEYS */;
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (1,1,1,'我感到情绪沮丧，郁闷','scale','[\"没有或很少时间\", \"小部分时间\", \"相当多时间\", \"绝大部分或全部时间\"]','2025-10-15 19:28:14');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (2,1,2,'我感到早晨心情最好','scale','[\"没有或很少时间\", \"小部分时间\", \"相当多时间\", \"绝大部分或全部时间\"]','2025-10-15 19:28:32');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (3,1,3,'我要哭或想哭','scale','[\"没有或很少时间\", \"小部分时间\", \"相当多时间\", \"绝大部分或全部时间\"]','2025-10-15 19:28:32');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (4,1,4,'我夜间睡眠不好','scale','[\"没有或很少时间\", \"小部分时间\", \"相当多时间\", \"绝大部分或全部时间\"]','2025-10-15 19:28:32');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (5,1,5,'我吃饭像平时一样多','scale','[\"没有或很少时间\", \"小部分时间\", \"相当多时间\", \"绝大部分或全部时间\"]','2025-10-15 19:28:32');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (6,1,6,'我的性功能正常','scale','[\"没有或很少时间\", \"小部分时间\", \"相当多时间\", \"绝大部分或全部时间\"]','2025-10-15 19:28:32');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (7,1,7,'我感到体重减轻','scale','[\"没有或很少时间\", \"小部分时间\", \"相当多时间\", \"绝大部分或全部时间\"]','2025-10-15 19:28:32');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (8,1,8,'我为便秘烦恼','scale','[\"没有或很少时间\", \"小部分时间\", \"相当多时间\", \"绝大部分或全部时间\"]','2025-10-15 19:28:32');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (9,1,9,'我的心跳比平时快','scale','[\"没有或很少时间\", \"小部分时间\", \"相当多时间\", \"绝大部分或全部时间\"]','2025-10-15 19:28:32');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (10,1,10,'我无故感到疲劳','scale','[\"没有或很少时间\", \"小部分时间\", \"相当多时间\", \"绝大部分或全部时间\"]','2025-10-15 19:28:32');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (11,2,1,'我感到比往常更加神经过敏和焦虑','scale','[\"没有或很少时间\", \"小部分时间\", \"相当多时间\", \"绝大部分或全部时间\"]','2025-10-15 19:28:45');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (12,2,2,'我无缘无故感到担心','scale','[\"没有或很少时间\", \"小部分时间\", \"相当多时间\", \"绝大部分或全部时间\"]','2025-10-15 19:28:45');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (13,2,3,'我容易心烦意乱或感到恐慌','scale','[\"没有或很少时间\", \"小部分时间\", \"相当多时间\", \"绝大部分或全部时间\"]','2025-10-15 19:28:45');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (14,2,4,'我感到我的身体好像被分成几块，支离破碎','scale','[\"没有或很少时间\", \"小部分时间\", \"相当多时间\", \"绝大部分或全部时间\"]','2025-10-15 19:28:45');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (15,2,5,'我感到事事都很顺利，不会有倒霉的事情发生','scale','[\"没有或很少时间\", \"小部分时间\", \"相当多时间\", \"绝大部分或全部时间\"]','2025-10-15 19:28:45');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (16,2,6,'我的四肢抖动和震颤','scale','[\"没有或很少时间\", \"小部分时间\", \"相当多时间\", \"绝大部分或全部时间\"]','2025-10-15 19:28:45');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (17,2,7,'我因头痛、颈痛和背痛而苦恼','scale','[\"没有或很少时间\", \"小部分时间\", \"相当多时间\", \"绝大部分或全部时间\"]','2025-10-15 19:28:45');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (18,2,8,'我感到无力且容易疲劳','scale','[\"没有或很少时间\", \"小部分时间\", \"相当多时间\", \"绝大部分或全部时间\"]','2025-10-15 19:28:45');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (19,2,9,'我感到很平静，能安静坐下来','scale','[\"没有或很少时间\", \"小部分时间\", \"相当多时间\", \"绝大部分或全部时间\"]','2025-10-15 19:28:45');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (20,2,10,'我感到我的心跳较快','scale','[\"没有或很少时间\", \"小部分时间\", \"相当多时间\", \"绝大部分或全部时间\"]','2025-10-15 19:28:45');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (21,3,1,'最近一个月，我感到难以应对日常生活中的压力','scale','[\"从不\", \"偶尔\", \"有时\", \"经常\", \"总是\"]','2025-10-15 19:29:02');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (22,3,2,'我发现自己很容易紧张或烦躁','scale','[\"从不\", \"偶尔\", \"有时\", \"经常\", \"总是\"]','2025-10-15 19:29:02');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (23,3,3,'我感到自己无法控制生活中的重要事情','scale','[\"从不\", \"偶尔\", \"有时\", \"经常\", \"总是\"]','2025-10-15 19:29:02');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (24,3,4,'我感到紧张和压力山大','scale','[\"从不\", \"偶尔\", \"有时\", \"经常\", \"总是\"]','2025-10-15 19:29:02');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (25,3,5,'我对处理个人问题感到自信','scale','[\"从不\", \"偶尔\", \"有时\", \"经常\", \"总是\"]','2025-10-15 19:29:02');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (26,3,6,'我感到事情正在按照我的意愿发展','scale','[\"从不\", \"偶尔\", \"有时\", \"经常\", \"总是\"]','2025-10-15 19:29:02');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (27,3,7,'我发现自己在为一些小事而烦恼','scale','[\"从不\", \"偶尔\", \"有时\", \"经常\", \"总是\"]','2025-10-15 19:29:02');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (28,3,8,'我能有效地处理生活中发生的重要变化','scale','[\"从不\", \"偶尔\", \"有时\", \"经常\", \"总是\"]','2025-10-15 19:29:02');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (29,3,9,'我对处理工作或学习中的职责感到自信','scale','[\"从不\", \"偶尔\", \"有时\", \"经常\", \"总是\"]','2025-10-15 19:29:02');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (30,3,10,'我感到自己能够控制生活中的烦恼','scale','[\"从不\", \"偶尔\", \"有时\", \"经常\", \"总是\"]','2025-10-15 19:29:02');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (31,4,1,'在聚会中，我通常主动与他人交谈','single_choice','[\"非常不符合\", \"不太符合\", \"一般\", \"比较符合\", \"非常符合\"]','2025-10-15 19:29:13');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (32,4,2,'我很少感到焦虑或紧张','single_choice','[\"非常不符合\", \"不太符合\", \"一般\", \"比较符合\", \"非常符合\"]','2025-10-15 19:29:13');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (33,4,3,'我喜欢尝试新的事物和体验','single_choice','[\"非常不符合\", \"不太符合\", \"一般\", \"比较符合\", \"非常符合\"]','2025-10-15 19:29:13');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (34,4,4,'我通常对他人持信任态度','single_choice','[\"非常不符合\", \"不太符合\", \"一般\", \"比较符合\", \"非常符合\"]','2025-10-15 19:29:13');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (35,4,5,'我做事有条理，喜欢提前规划','single_choice','[\"非常不符合\", \"不太符合\", \"一般\", \"比较符合\", \"非常符合\"]','2025-10-15 19:29:13');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (36,4,6,'我享受与他人共度的时光','single_choice','[\"非常不符合\", \"不太符合\", \"一般\", \"比较符合\", \"非常符合\"]','2025-10-15 19:29:13');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (37,4,7,'我容易感到情绪低落','single_choice','[\"非常不符合\", \"不太符合\", \"一般\", \"比较符合\", \"非常符合\"]','2025-10-15 19:29:13');
INSERT INTO `assessment_questions` (`id`, `assessment_id`, `question_number`, `question_text`, `question_type`, `options`, `create_time`) VALUES (38,4,8,'我对艺术和美学有浓厚兴趣','single_choice','[\"非常不符合\", \"不太符合\", \"一般\", \"比较符合\", \"非常符合\"]','2025-10-15 19:29:13');
/*!40000 ALTER TABLE `assessment_questions` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `assessments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assessments` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '测评ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '测评标题',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '测评描述',
  `category` enum('抑郁','焦虑','压力','人格','职业倾向','人际关系') COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '测评类别',
  `questions_count` int DEFAULT '0' COMMENT '题目数量',
  `estimated_time` int DEFAULT NULL COMMENT '预计耗时（分钟）',
  `status` enum('启用','禁用') COLLATE utf8mb4_unicode_ci DEFAULT '启用' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `test_count` int DEFAULT '0' COMMENT '??????',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='心理测评问卷表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `assessments` WRITE;
/*!40000 ALTER TABLE `assessments` DISABLE KEYS */;
INSERT INTO `assessments` (`id`, `title`, `description`, `category`, `questions_count`, `estimated_time`, `status`, `create_time`, `update_time`, `test_count`) VALUES (1,'抑郁自评量表','用于评估个体近一周的抑郁症状严重程度','抑郁',10,10,'启用','2025-10-15 15:23:52','2025-10-15 15:23:52',8);
INSERT INTO `assessments` (`id`, `title`, `description`, `category`, `questions_count`, `estimated_time`, `status`, `create_time`, `update_time`, `test_count`) VALUES (2,'焦虑自评量表','用于评估个体近一周的焦虑症状严重程度','焦虑',10,10,'启用','2025-10-15 15:23:52','2025-10-15 15:23:52',1);
INSERT INTO `assessments` (`id`, `title`, `description`, `category`, `questions_count`, `estimated_time`, `status`, `create_time`, `update_time`, `test_count`) VALUES (3,'压力测试量表','评估当前生活压力水平和应对能力','压力',10,8,'启用','2025-10-15 15:23:52','2025-10-15 15:23:52',0);
INSERT INTO `assessments` (`id`, `title`, `description`, `category`, `questions_count`, `estimated_time`, `status`, `create_time`, `update_time`, `test_count`) VALUES (4,'人格特质测试','基于大五人格理论的人格特质评估','人格',8,15,'启用','2025-10-15 15:23:52','2025-10-16 21:38:28',0);
/*!40000 ALTER TABLE `assessments` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `bottle_replies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bottle_replies` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '回复ID',
  `bottle_id` bigint NOT NULL COMMENT '漂流瓶ID',
  `reply_user_id` bigint DEFAULT NULL COMMENT '回复用户ID（可为空，表示系统或匿名回复）',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '回复内容',
  `is_anonymous` tinyint(1) DEFAULT '1' COMMENT '是否匿名',
  `like_count` int DEFAULT '0' COMMENT '点赞数量',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'normal',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_bottle_id` (`bottle_id`),
  KEY `idx_reply_user_id` (`reply_user_id`),
  CONSTRAINT `bottle_replies_ibfk_1` FOREIGN KEY (`bottle_id`) REFERENCES `message_bottles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `bottle_replies_ibfk_2` FOREIGN KEY (`reply_user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='漂流瓶回复表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `bottle_replies` WRITE;
/*!40000 ALTER TABLE `bottle_replies` DISABLE KEYS */;
INSERT INTO `bottle_replies` (`id`, `bottle_id`, `reply_user_id`, `content`, `is_anonymous`, `like_count`, `status`, `create_time`) VALUES (6,12,1,'打打',0,0,'normal','2025-10-23 22:54:24');
INSERT INTO `bottle_replies` (`id`, `bottle_id`, `reply_user_id`, `content`, `is_anonymous`, `like_count`, `status`, `create_time`) VALUES (7,10,1,'打打',0,0,'normal','2025-10-23 22:59:09');
INSERT INTO `bottle_replies` (`id`, `bottle_id`, `reply_user_id`, `content`, `is_anonymous`, `like_count`, `status`, `create_time`) VALUES (8,10,1,'阿德',0,0,'normal','2025-10-23 22:59:12');
/*!40000 ALTER TABLE `bottle_replies` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `consultation_appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consultation_appointments` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `psychologist_id` bigint NOT NULL COMMENT '专家ID',
  `topic` varchar(500) NOT NULL COMMENT '咨询主题',
  `preferred_time` datetime NOT NULL COMMENT '期望时间',
  `contact_info` varchar(100) NOT NULL COMMENT '联系方式',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态：pending-待处理, confirmed-已确认, completed-已完成, cancelled-已取消',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_psychologist_id` (`psychologist_id`),
  KEY `idx_status` (`status`),
  KEY `idx_preferred_time` (`preferred_time`),
  CONSTRAINT `consultation_appointments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `consultation_appointments_ibfk_2` FOREIGN KEY (`psychologist_id`) REFERENCES `psychologists` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='咨询预约表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `consultation_appointments` WRITE;
/*!40000 ALTER TABLE `consultation_appointments` DISABLE KEYS */;
INSERT INTO `consultation_appointments` (`id`, `user_id`, `psychologist_id`, `topic`, `preferred_time`, `contact_info`, `status`, `create_time`, `update_time`) VALUES (88,1,1,'大大啊','2025-10-26 09:00:00','13800138000','confirmed','2025-10-26 23:34:26','2025-10-26 23:34:26');
INSERT INTO `consultation_appointments` (`id`, `user_id`, `psychologist_id`, `topic`, `preferred_time`, `contact_info`, `status`, `create_time`, `update_time`) VALUES (89,1,1,'你是谁','2025-10-30 16:00:00','131313131331','confirmed','2025-10-30 17:17:31','2025-10-30 17:17:31');
INSERT INTO `consultation_appointments` (`id`, `user_id`, `psychologist_id`, `topic`, `preferred_time`, `contact_info`, `status`, `create_time`, `update_time`) VALUES (90,1,1,'1111','2025-10-30 09:00:00','13800138000','confirmed','2025-10-30 18:18:32','2025-10-30 18:18:32');
/*!40000 ALTER TABLE `consultation_appointments` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `consultation_stats`;
/*!50001 DROP VIEW IF EXISTS `consultation_stats`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `consultation_stats` AS SELECT 
 1 AS `psychologist_id`,
 1 AS `real_name`,
 1 AS `title`,
 1 AS `total_consultations`,
 1 AS `avg_rating`,
 1 AS `total_duration`*/;
SET character_set_client = @saved_cs_client;
DROP TABLE IF EXISTS `consultations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consultations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '咨询ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `psychologist_id` bigint NOT NULL COMMENT '心理专家ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '咨询主题',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '咨询描述',
  `consultation_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('待接单','进行中','已完成','已取消') COLLATE utf8mb4_unicode_ci DEFAULT '待接单' COMMENT '状态',
  `scheduled_time` datetime DEFAULT NULL COMMENT '预约时间',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `duration` int DEFAULT NULL COMMENT '咨询时长（分钟）',
  `cost` decimal(10,2) DEFAULT NULL COMMENT '咨询费用',
  `user_rating` int DEFAULT NULL COMMENT '用户评分（1-5）',
  `user_feedback` text COLLATE utf8mb4_unicode_ci COMMENT '用户反馈',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_psychologist_id` (`psychologist_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `consultations_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `consultations_ibfk_2` FOREIGN KEY (`psychologist_id`) REFERENCES `psychologists` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='咨询记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `consultations` WRITE;
/*!40000 ALTER TABLE `consultations` DISABLE KEYS */;
INSERT INTO `consultations` (`id`, `user_id`, `psychologist_id`, `title`, `description`, `consultation_type`, `status`, `scheduled_time`, `start_time`, `end_time`, `duration`, `cost`, `user_rating`, `user_feedback`, `create_time`, `update_time`) VALUES (41,1,1,'大大啊','大大啊','在线咨询','已完成','2025-10-26 01:00:00','2025-10-26 23:37:14','2025-10-26 23:37:34',1,300.00,5,'dadaad','2025-10-26 23:34:26','2025-10-26 23:37:34');
INSERT INTO `consultations` (`id`, `user_id`, `psychologist_id`, `title`, `description`, `consultation_type`, `status`, `scheduled_time`, `start_time`, `end_time`, `duration`, `cost`, `user_rating`, `user_feedback`, `create_time`, `update_time`) VALUES (42,1,1,'你是谁','我想问问你是谁','在线咨询','已完成','2025-10-30 16:00:00','2025-10-30 18:20:25','2025-10-30 18:20:29',1,300.00,NULL,NULL,'2025-10-30 17:17:31','2025-10-30 18:20:29');
INSERT INTO `consultations` (`id`, `user_id`, `psychologist_id`, `title`, `description`, `consultation_type`, `status`, `scheduled_time`, `start_time`, `end_time`, `duration`, `cost`, `user_rating`, `user_feedback`, `create_time`, `update_time`) VALUES (43,1,1,'1111','1111','在线咨询','已完成','2025-10-30 01:00:00','2025-10-30 18:20:26','2025-10-30 18:20:28',1,300.00,NULL,NULL,'2025-10-30 18:18:32','2025-10-30 18:20:28');
/*!40000 ALTER TABLE `consultations` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `contact_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contact_messages` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '留言ID',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `phone` varchar(20) NOT NULL COMMENT '电话',
  `subject` varchar(20) NOT NULL COMMENT '主题',
  `content` text NOT NULL COMMENT '留言内容',
  `status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending-待处理, replied-已回复, resolved-已解决',
  `reply_content` text COMMENT '回复内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='联系我们留言表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `contact_messages` WRITE;
/*!40000 ALTER TABLE `contact_messages` DISABLE KEYS */;
INSERT INTO `contact_messages` (`id`, `name`, `email`, `phone`, `subject`, `content`, `status`, `reply_content`, `create_time`, `update_time`) VALUES (1,'张三','zhangsan@example.com','13800138007','suggestion','平台界面很美观，建议增加更多心理测评量表。','replied','感谢您的建议！我们正在开发新的测评量表，预计下个月上线。','2025-10-15 20:48:51','2025-10-15 20:48:51');
INSERT INTO `contact_messages` (`id`, `name`, `email`, `phone`, `subject`, `content`, `status`, `reply_content`, `create_time`, `update_time`) VALUES (2,'李四','lisi@example.com','13800138008','problem','无法正常提交心理测评报告，显示系统错误。','resolved','问题已修复，请重新尝试。如仍有问题请联系客服。','2025-10-15 20:48:51','2025-10-15 20:48:51');
INSERT INTO `contact_messages` (`id`, `name`, `email`, `phone`, `subject`, `content`, `status`, `reply_content`, `create_time`, `update_time`) VALUES (3,'王五','wangwu@example.com','13800138009','cooperation','希望与贵平台合作开展心理健康讲座。','replied','谢谢你','2025-10-15 20:48:51','2025-10-15 20:48:51');
INSERT INTO `contact_messages` (`id`, `name`, `email`, `phone`, `subject`, `content`, `status`, `reply_content`, `create_time`, `update_time`) VALUES (4,'赵六','zhaoliu@example.com','13800138010','other','咨询师服务态度很好，治疗效果明显。','replied','感谢您的认可！我们会继续努力提供优质服务。','2025-10-15 20:48:51','2025-10-15 20:48:51');
INSERT INTO `contact_messages` (`id`, `name`, `email`, `phone`, `subject`, `content`, `status`, `reply_content`, `create_time`, `update_time`) VALUES (5,'钱七','qianqi@example.com','13800138011','suggestion','希望增加AI心理助手的对话功能。','replied','AI对话功能已在开发计划中，敬请期待！','2025-10-15 20:48:51','2025-10-15 20:48:51');
INSERT INTO `contact_messages` (`id`, `name`, `email`, `phone`, `subject`, `content`, `status`, `reply_content`, `create_time`, `update_time`) VALUES (6,'????','test@example.com','13800138000','suggestion','????????','pending',NULL,'2025-10-15 20:54:39','2025-10-15 20:54:39');
INSERT INTO `contact_messages` (`id`, `name`, `email`, `phone`, `subject`, `content`, `status`, `reply_content`, `create_time`, `update_time`) VALUES (7,'adad','123@qq.com','13141415151','suggestion','eqeqweqeqwweqw','pending',NULL,'2025-10-15 20:55:42','2025-10-15 20:55:42');
INSERT INTO `contact_messages` (`id`, `name`, `email`, `phone`, `subject`, `content`, `status`, `reply_content`, `create_time`, `update_time`) VALUES (8,'ada','12@qq.com','13141415141','suggestion','13141414141','pending',NULL,'2025-10-15 21:02:44','2025-10-15 21:02:44');
INSERT INTO `contact_messages` (`id`, `name`, `email`, `phone`, `subject`, `content`, `status`, `reply_content`, `create_time`, `update_time`) VALUES (9,'dada131','3131@11.com','13414141414','suggestion','414114414141','pending',NULL,'2025-10-19 00:06:44','2025-10-19 00:06:44');
/*!40000 ALTER TABLE `contact_messages` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `message_bottles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message_bottles` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '漂流瓶ID',
  `user_id` bigint NOT NULL COMMENT '发送用户ID',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '漂流瓶内容',
  `bottle_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'mood',
  `mood` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'calm',
  `is_anonymous` tinyint(1) DEFAULT '1' COMMENT '是否匿名',
  `reply_count` int DEFAULT '0' COMMENT '回复数量',
  `like_count` int DEFAULT '0' COMMENT '点赞数量',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'normal',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_bottle_type` (`bottle_type`),
  KEY `idx_mood` (`mood`),
  CONSTRAINT `message_bottles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='漂流瓶表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `message_bottles` WRITE;
/*!40000 ALTER TABLE `message_bottles` DISABLE KEYS */;
INSERT INTO `message_bottles` (`id`, `user_id`, `content`, `bottle_type`, `mood`, `is_anonymous`, `reply_count`, `like_count`, `status`, `create_time`) VALUES (9,1,'大声道','mood','happy',0,0,0,'normal','2025-10-23 22:44:30');
INSERT INTO `message_bottles` (`id`, `user_id`, `content`, `bottle_type`, `mood`, `is_anonymous`, `reply_count`, `like_count`, `status`, `create_time`) VALUES (10,1,'阿达','mood','happy',0,2,0,'normal','2025-10-23 22:44:50');
INSERT INTO `message_bottles` (`id`, `user_id`, `content`, `bottle_type`, `mood`, `is_anonymous`, `reply_count`, `like_count`, `status`, `create_time`) VALUES (11,1,'阿萨大大','mood','happy',1,0,4,'normal','2025-10-23 22:45:36');
INSERT INTO `message_bottles` (`id`, `user_id`, `content`, `bottle_type`, `mood`, `is_anonymous`, `reply_count`, `like_count`, `status`, `create_time`) VALUES (12,1,'啊大大','mood','happy',0,1,1,'normal','2025-10-23 22:47:18');
INSERT INTO `message_bottles` (`id`, `user_id`, `content`, `bottle_type`, `mood`, `is_anonymous`, `reply_count`, `like_count`, `status`, `create_time`) VALUES (15,1,'答','mood','happy',1,0,0,'normal','2025-10-26 21:39:32');
INSERT INTO `message_bottles` (`id`, `user_id`, `content`, `bottle_type`, `mood`, `is_anonymous`, `reply_count`, `like_count`, `status`, `create_time`) VALUES (16,1,'大大阿达','mood','happy',0,0,0,'normal','2025-10-30 17:47:03');
/*!40000 ALTER TABLE `message_bottles` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `psychologists`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `psychologists` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '专家ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密存储）',
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `title` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职称（如：国家二级心理咨询师）',
  `specialty` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '擅长领域',
  `experience_years` int DEFAULT NULL COMMENT '从业年限',
  `introduction` text COLLATE utf8mb4_unicode_ci COMMENT '个人介绍',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `hourly_rate` decimal(10,2) DEFAULT NULL COMMENT '咨询费用（元/小时）',
  `rating` decimal(3,2) DEFAULT '5.00' COMMENT '评分',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '待审核',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `account` decimal(10,2) DEFAULT '0.00' COMMENT '总收入',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `idx_username` (`username`),
  KEY `idx_specialty` (`specialty`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='心理专家表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `psychologists` WRITE;
/*!40000 ALTER TABLE `psychologists` DISABLE KEYS */;
INSERT INTO `psychologists` (`id`, `username`, `password`, `real_name`, `title`, `specialty`, `experience_years`, `introduction`, `avatar`, `email`, `phone`, `hourly_rate`, `rating`, `status`, `last_login_time`, `create_time`, `update_time`, `account`) VALUES (1,'jj','123456','张心理','国家二级心理咨询师','抑郁焦虑、人际关系、职场压力',9,'专注于认知行为疗法，帮助数百名来访者走出心理困境','http://localhost:8080/uploads/avatars/psychologist_1_1761057788943.jpg','','',300.00,5.00,'正常',NULL,'2025-10-15 15:23:52','2025-10-22 19:01:10',900.00);
INSERT INTO `psychologists` (`id`, `username`, `password`, `real_name`, `title`, `specialty`, `experience_years`, `introduction`, `avatar`, `email`, `phone`, `hourly_rate`, `rating`, `status`, `last_login_time`, `create_time`, `update_time`, `account`) VALUES (2,'liumei','123456','刘美','心理学博士','青少年心理、家庭教育、情绪管理',12,'擅长青少年心理问题咨询和家庭教育指导','http://localhost:8080/uploads/avatars/psychologist_2_1761058670283.png',NULL,NULL,400.00,5.00,'正常',NULL,'2025-10-15 15:23:52','2025-10-21 22:57:50',0.00);
INSERT INTO `psychologists` (`id`, `username`, `password`, `real_name`, `title`, `specialty`, `experience_years`, `introduction`, `avatar`, `email`, `phone`, `hourly_rate`, `rating`, `status`, `last_login_time`, `create_time`, `update_time`, `account`) VALUES (3,'liyongxin','123456','李用心','资深心理咨询师','人际关系、职场压力',8,'心理学硕士，专注于职场心理辅导和人际关系改善。','http://localhost:8080/uploads/avatars/psychologist_3_1761058705057.png','li@xingyu.com','13800138002',280.00,4.80,'正常',NULL,'2025-10-15 20:48:51','2025-10-21 22:58:25',280.00);
INSERT INTO `psychologists` (`id`, `username`, `password`, `real_name`, `title`, `specialty`, `experience_years`, `introduction`, `avatar`, `email`, `phone`, `hourly_rate`, `rating`, `status`, `last_login_time`, `create_time`, `update_time`, `account`) VALUES (4,'wangwenxin','123456','王文心','青少年心理专家','青少年心理、学习压力',12,'儿童青少年心理专家，拥有丰富的校园心理咨询经验。','/avatars/wang.jpg','wang@xingyu.com','13800138003',320.00,4.90,'正常',NULL,'2025-10-15 20:48:51','2025-10-15 20:48:51',0.00);
INSERT INTO `psychologists` (`id`, `username`, `password`, `real_name`, `title`, `specialty`, `experience_years`, `introduction`, `avatar`, `email`, `phone`, `hourly_rate`, `rating`, `status`, `last_login_time`, `create_time`, `update_time`, `account`) VALUES (5,'zhaominxin','123456','赵敏心','婚姻家庭咨询师','婚姻家庭、情感问题',6,'婚姻家庭咨询师，擅长解决夫妻关系和家庭矛盾。','/avatars/zhao.jpg','zhao@xingyu.com','13800138004',260.00,4.70,'正常',NULL,'2025-10-15 20:48:51','2025-10-15 20:48:51',0.00);
INSERT INTO `psychologists` (`id`, `username`, `password`, `real_name`, `title`, `specialty`, `experience_years`, `introduction`, `avatar`, `email`, `phone`, `hourly_rate`, `rating`, `status`, `last_login_time`, `create_time`, `update_time`, `account`) VALUES (6,'chenanxin','123456','陈安心','心理治疗师','创伤治疗、心理康复',15,'资深心理治疗师，擅长创伤后应激障碍的治疗和康复。','/avatars/chen.jpg','chen@xingyu.com','13800138005',350.00,5.00,'正常',NULL,'2025-10-15 20:48:51','2025-10-15 20:48:51',0.00);
INSERT INTO `psychologists` (`id`, `username`, `password`, `real_name`, `title`, `specialty`, `experience_years`, `introduction`, `avatar`, `email`, `phone`, `hourly_rate`, `rating`, `status`, `last_login_time`, `create_time`, `update_time`, `account`) VALUES (7,'zhouhuanxin','123456','周欢心','积极心理学专家','积极心理、幸福提升',7,'积极心理学研究者，帮助人们发现生活中的美好和意义。','/avatars/zhou.jpg','zhou@xingyu.com','13800138006',270.00,4.80,'正常',NULL,'2025-10-15 20:48:51','2025-10-15 20:48:51',0.00);
INSERT INTO `psychologists` (`id`, `username`, `password`, `real_name`, `title`, `specialty`, `experience_years`, `introduction`, `avatar`, `email`, `phone`, `hourly_rate`, `rating`, `status`, `last_login_time`, `create_time`, `update_time`, `account`) VALUES (17,'xxx','123456','dada','dad','ada',5,'qweqeqeqeqeq','http://localhost:8080/uploads/avatars/psychologist_17_1761058765039.png','131@qq.com','13141414141',300.00,4.50,'正常',NULL,'2025-10-21 22:59:15','2025-10-21 22:59:25',0.00);
/*!40000 ALTER TABLE `psychologists` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `system_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` text COLLATE utf8mb4_unicode_ci COMMENT '配置值',
  `description` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '配置描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `config_key` (`config_key`),
  KEY `idx_config_key` (`config_key`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `system_config` WRITE;
/*!40000 ALTER TABLE `system_config` DISABLE KEYS */;
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `create_time`, `update_time`) VALUES (1,'site_name','星语航海心理测评与分析平台','网站名称','2025-10-15 15:23:52','2025-10-15 15:23:52');
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `create_time`, `update_time`) VALUES (2,'theme_color','#1E90FF','主题色调（蓝色）','2025-10-15 15:23:52','2025-10-15 15:23:52');
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `create_time`, `update_time`) VALUES (3,'ai_api_key','your_ai_api_key_here','AI服务API密钥','2025-10-15 15:23:52','2025-10-15 15:23:52');
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `create_time`, `update_time`) VALUES (4,'max_login_attempts','5','最大登录尝试次数','2025-10-15 15:23:52','2025-10-15 15:23:52');
/*!40000 ALTER TABLE `system_config` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `user_activity_stats`;
/*!50001 DROP VIEW IF EXISTS `user_activity_stats`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `user_activity_stats` AS SELECT 
 1 AS `user_id`,
 1 AS `username`,
 1 AS `real_name`,
 1 AS `assessment_count`,
 1 AS `consultation_count`,
 1 AS `bottle_count`,
 1 AS `last_active_time`*/;
SET character_set_client = @saved_cs_client;
DROP TABLE IF EXISTS `user_assessments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_assessments` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `assessment_id` bigint NOT NULL COMMENT '测评ID',
  `score` decimal(5,2) DEFAULT NULL COMMENT '测评得分',
  `result_level` enum('正常','轻度','中度','重度') COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '结果等级',
  `result_description` text COLLATE utf8mb4_unicode_ci COMMENT '结果描述',
  `suggestions` text COLLATE utf8mb4_unicode_ci COMMENT '建议',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `status` enum('进行中','已完成','已终止') COLLATE utf8mb4_unicode_ci DEFAULT '进行中' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_assessment_id` (`assessment_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `user_assessments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_assessments_ibfk_2` FOREIGN KEY (`assessment_id`) REFERENCES `assessments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户测评记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `user_assessments` WRITE;
/*!40000 ALTER TABLE `user_assessments` DISABLE KEYS */;
INSERT INTO `user_assessments` (`id`, `user_id`, `assessment_id`, `score`, `result_level`, `result_description`, `suggestions`, `start_time`, `end_time`, `status`, `create_time`) VALUES (36,1,2,29.00,'重度',NULL,'您可能存在重度焦虑。强烈建议：尽快寻求专业心理治疗，可能需要配合药物治疗。请及时就医！','2025-10-27 00:24:27','2025-10-27 00:24:27','已完成','2025-10-27 00:24:27');
INSERT INTO `user_assessments` (`id`, `user_id`, `assessment_id`, `score`, `result_level`, `result_description`, `suggestions`, `start_time`, `end_time`, `status`, `create_time`) VALUES (37,1,1,30.00,'重度',NULL,'您可能存在重度抑郁症状。强烈建议：立即寻求专业心理医生帮助，进行系统治疗。必要时可考虑药物治疗配合心理治疗。请重视自己的心理健康！','2025-10-27 00:26:56','2025-10-27 00:26:56','已完成','2025-10-27 00:26:56');
INSERT INTO `user_assessments` (`id`, `user_id`, `assessment_id`, `score`, `result_level`, `result_description`, `suggestions`, `start_time`, `end_time`, `status`, `create_time`) VALUES (38,1,1,30.00,'重度',NULL,'您可能存在重度抑郁症状。强烈建议：立即寻求专业心理医生帮助，进行系统治疗。必要时可考虑药物治疗配合心理治疗。请重视自己的心理健康！','2025-10-27 00:34:21','2025-10-27 00:34:21','已完成','2025-10-27 00:34:21');
INSERT INTO `user_assessments` (`id`, `user_id`, `assessment_id`, `score`, `result_level`, `result_description`, `suggestions`, `start_time`, `end_time`, `status`, `create_time`) VALUES (39,1,1,29.00,'重度',NULL,'您可能存在重度抑郁症状。强烈建议：立即寻求专业心理医生帮助，进行系统治疗。必要时可考虑药物治疗配合心理治疗。请重视自己的心理健康！','2025-10-27 00:39:32','2025-10-27 00:39:32','已完成','2025-10-27 00:39:32');
/*!40000 ALTER TABLE `user_assessments` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密存储）',
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '真实姓名',
  `nickname` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `gender` enum('男','女','其他') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `age` int DEFAULT NULL COMMENT '年龄',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `status` enum('正常','禁用') COLLATE utf8mb4_unicode_ci DEFAULT '正常' COMMENT '账号状态',
  `role` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'user',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `idx_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`id`, `username`, `password`, `real_name`, `nickname`, `email`, `phone`, `gender`, `age`, `avatar`, `status`, `role`, `last_login_time`, `create_time`, `update_time`) VALUES (1,'use','123456','use','大大','test@example.com','13800138000','男',28,'','正常','user','2025-10-30 18:14:44','2025-10-16 00:21:16','2025-10-30 18:14:44');
INSERT INTO `users` (`id`, `username`, `password`, `real_name`, `nickname`, `email`, `phone`, `gender`, `age`, `avatar`, `status`, `role`, `last_login_time`, `create_time`, `update_time`) VALUES (2,'testuser1','password123','测试用户1','测试1','test1@xingyu.com','138001380011','男',25,'/api/placeholder/40x40?text=测1','正常','user','2025-10-16 00:21:23','2025-10-15 16:54:16','2025-10-18 23:10:33');
INSERT INTO `users` (`id`, `username`, `password`, `real_name`, `nickname`, `email`, `phone`, `gender`, `age`, `avatar`, `status`, `role`, `last_login_time`, `create_time`, `update_time`) VALUES (3,'testuser2','password123','测试用户2','测试2','test2@xingyu.com','13800138002','女',30,'/api/placeholder/40x40?text=测2','正常','user',NULL,'2025-10-15 16:54:20','2025-10-15 17:06:58');
INSERT INTO `users` (`id`, `username`, `password`, `real_name`, `nickname`, `email`, `phone`, `gender`, `age`, `avatar`, `status`, `role`, `last_login_time`, `create_time`, `update_time`) VALUES (4,'testuser3','123456','测试用户3','测试3','test3@xingyu.com','13800138003','男',28,'/api/placeholder/40x40?text=测3','正常','user','2025-10-15 22:16:09','2025-10-15 20:06:40','2025-10-17 00:38:02');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

USE `xingyu_hanghai`;
/*!50001 DROP VIEW IF EXISTS `consultation_stats`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `consultation_stats` AS select `p`.`id` AS `psychologist_id`,`p`.`real_name` AS `real_name`,`p`.`title` AS `title`,count(`c`.`id`) AS `total_consultations`,avg(`c`.`user_rating`) AS `avg_rating`,sum(`c`.`duration`) AS `total_duration` from (`psychologists` `p` left join `consultations` `c` on(((`p`.`id` = `c`.`psychologist_id`) and (`c`.`status` = '已完成')))) group by `p`.`id`,`p`.`real_name`,`p`.`title` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!50001 DROP VIEW IF EXISTS `user_activity_stats`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `user_activity_stats` AS select `u`.`id` AS `user_id`,`u`.`username` AS `username`,`u`.`real_name` AS `real_name`,count(distinct `ua`.`id`) AS `assessment_count`,count(distinct `c`.`id`) AS `consultation_count`,count(distinct `mb`.`id`) AS `bottle_count`,max(`u`.`last_login_time`) AS `last_active_time` from (((`users` `u` left join `user_assessments` `ua` on((`u`.`id` = `ua`.`user_id`))) left join `consultations` `c` on((`u`.`id` = `c`.`user_id`))) left join `message_bottles` `mb` on((`u`.`id` = `mb`.`user_id`))) group by `u`.`id`,`u`.`username`,`u`.`real_name` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

