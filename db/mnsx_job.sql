/*
 Navicat Premium Data Transfer

 Source Server         : Coud-CentOS-Mnsx
 Source Server Type    : MySQL
 Source Server Version : 50742
 Source Host           : 47.101.195.28:3306
 Source Schema         : mnsx_job

 Target Server Type    : MySQL
 Target Server Version : 50742
 File Encoding         : 65001

 Date: 27/07/2023 09:42:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for job_app
-- ----------------------------
DROP TABLE IF EXISTS `job_app`;
CREATE TABLE `job_app`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '应用编号',
  `app_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用名称',
  `app_desc` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用描述',
  `create_way` int(11) NOT NULL COMMENT '创建方式（1-自动，2-手动）',
  `address_list` varchar(512) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT '应用地址列表',
  `creator` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `enabled` int(11) NOT NULL COMMENT '启用状态：1-启用，0-停用',
  `is_deleted` int(11) NOT NULL COMMENT '是否删除：1-是，0-否',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for job_info
-- ----------------------------
DROP TABLE IF EXISTS `job_info`;
CREATE TABLE `job_info`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '任务编号',
  `app_id` int(11) NOT NULL COMMENT '应用编号',
  `job_name` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT '任务名称',
  `job_desc` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务描述',
  `alarm_email` varchar(512) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '警告邮箱',
  `create_way` int(11) NOT NULL COMMENT '创建方式：1-自动，2-手动',
  `run_cron` varchar(128) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT '任务执行CRON',
  `run_strategy` int(11) NOT NULL COMMENT '任务执行策略：1-随机',
  `run_param` varchar(512) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '任务执行参数',
  `run_timeout` int(11) NOT NULL COMMENT '任务执行超时时间',
  `run_fail_retry_count` int(11) NOT NULL COMMENT '任务执行失败重试最大次数',
  `trigger_last_time` datetime(0) NULL DEFAULT NULL COMMENT '上次调度时间',
  `trigger_next_time` datetime(0) NULL DEFAULT NULL COMMENT '下次调度时间',
  `creator` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `enabled` int(11) NOT NULL COMMENT '启用状态：1-启用，0-停用',
  `is_deleted` int(11) NOT NULL COMMENT '是否删除：1-是，2-否',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for job_log
-- ----------------------------
DROP TABLE IF EXISTS `job_log`;
CREATE TABLE `job_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '日志编号',
  `job_id` int(11) NOT NULL COMMENT '任务编号',
  `run_address_list` varchar(512) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT '运行地址',
  `run_fail_retry_count` int(11) NOT NULL COMMENT '任务执行失败重试',
  `trigger_start_time` datetime(0) NOT NULL COMMENT '调度开始时间',
  `trigger_end_time` datetime(0) NOT NULL COMMENT '调度结束时间',
  `trigger_result` int(11) NOT NULL COMMENT '调度结果：1-成功，0-失败',
  `trigger_msg` varchar(2560) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '调度信息',
  `job_result` int(11) NULL DEFAULT NULL COMMENT '执行结果',
  `job_msg` varchar(2560) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
