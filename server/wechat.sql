/*
Navicat MySQL Data Transfer

Source Server         : remote_mysql
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : wechat

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2017-03-07 14:30:49
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for drama
-- ----------------------------
DROP TABLE IF EXISTS `drama`;
CREATE TABLE `drama` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `author_id` bigint(255) DEFAULT NULL,
  `score` double(255,0) NOT NULL DEFAULT '0',
  `like_count` bigint(255) NOT NULL DEFAULT '0',
  `weight` double NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `data` longtext,
  `status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of drama
-- ----------------------------
INSERT INTO `drama` VALUES ('1', '一号测试', '一个小故事2', null, '0', '0', '0', '2016-11-21 14:22:14', '2016-11-21 14:22:14', '{\"title\":\"newFlow_1\",\"nodes\":{\"gf_node_1\":{\"name\":\"node_1\",\"left\":255,\"top\":63,\"type\":\"start\",\"width\":26,\"height\":24,\"alt\":true},\"gf_node_2\":{\"name\":\"node_2\",\"left\":477,\"top\":443,\"type\":\"end\",\"width\":26,\"height\":24,\"alt\":true},\"gf_node_3\":{\"name\":\"场景1\",\"left\":256,\"top\":144,\"type\":\"task\",\"width\":26,\"height\":24,\"alt\":true},\"gf_node_4\":{\"name\":\"选我\",\"left\":260,\"top\":262,\"type\":\"node\",\"width\":26,\"height\":24,\"alt\":true},\"gf_node_5\":{\"name\":\"不，选我\",\"left\":470,\"top\":149,\"type\":\"node\",\"width\":26,\"height\":24,\"alt\":true},\"gf_node_12\":{\"name\":\"成功\",\"left\":256,\"top\":433,\"type\":\"task\",\"width\":26,\"height\":24,\"alt\":true},\"gf_node_13\":{\"name\":\"失败\",\"left\":477,\"top\":271,\"type\":\"task\",\"width\":26,\"height\":24,\"alt\":true}},\"lines\":{\"gf_line_6\":{\"type\":\"sl\",\"from\":\"gf_node_1\",\"to\":\"gf_node_3\",\"name\":\"\"},\"gf_line_7\":{\"type\":\"sl\",\"from\":\"gf_node_3\",\"to\":\"gf_node_4\",\"name\":\"\"},\"gf_line_8\":{\"type\":\"sl\",\"from\":\"gf_node_3\",\"to\":\"gf_node_5\",\"name\":\"\"},\"gf_line_14\":{\"type\":\"sl\",\"from\":\"gf_node_4\",\"to\":\"gf_node_12\",\"name\":\"\"},\"gf_line_15\":{\"type\":\"sl\",\"from\":\"gf_node_12\",\"to\":\"gf_node_2\",\"name\":\"\"},\"gf_line_16\":{\"type\":\"sl\",\"from\":\"gf_node_5\",\"to\":\"gf_node_13\",\"name\":\"\"},\"gf_line_17\":{\"type\":\"sl\",\"from\":\"gf_node_13\",\"to\":\"gf_node_2\",\"name\":\"\"}},\"areas\":{},\"initNum\":19}', '0');

-- ----------------------------
-- Table structure for drama_play
-- ----------------------------
DROP TABLE IF EXISTS `drama_play`;
CREATE TABLE `drama_play` (
  `drama_id` int(11) NOT NULL,
  `node_id` bigint(20) NOT NULL,
  `people_id` int(11) NOT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`drama_id`,`node_id`,`people_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of drama_play
-- ----------------------------

-- ----------------------------
-- Table structure for node
-- ----------------------------
DROP TABLE IF EXISTS `node`;
CREATE TABLE `node` (
  `id` varchar(50) NOT NULL,
  `drama_id` int(11) DEFAULT NULL,
  `description` text NOT NULL,
  `choices` longtext,
  `type` int(11) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of node
-- ----------------------------
INSERT INTO `node` VALUES ('1-10a4fa8c-4b60-4815-ac67-b620c2c0562b', '1', '成功', null, '3', '2016-11-24 16:19:54', '2016-11-24 16:19:54');
INSERT INTO `node` VALUES ('1-2d951cd0-d258-4e5d-a194-32bcea44e1cd', '1', '失败', null, '3', '2016-11-24 16:19:54', '2016-11-24 16:19:54');
INSERT INTO `node` VALUES ('1-b7a1cd21-3d97-46ab-a002-5dfd8e046c0c', '1', '场景1', '[{\"description\":\"不，选我\",\"nextNodeId\":\"1-2d951cd0-d258-4e5d-a194-32bcea44e1cd\"},{\"description\":\"选我\",\"nextNodeId\":\"1-10a4fa8c-4b60-4815-ac67-b620c2c0562b\"}]', '1', '2016-11-24 16:19:54', '2016-11-24 16:19:54');

-- ----------------------------
-- Table structure for people
-- ----------------------------
DROP TABLE IF EXISTS `people`;
CREATE TABLE `people` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `openid` varchar(255) NOT NULL,
  `unionid` varchar(255) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `coins` bigint(20) NOT NULL DEFAULT '0',
  `points` bigint(255) NOT NULL DEFAULT '0',
  `level` int(255) NOT NULL DEFAULT '0',
  `last_increase_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_login_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `hk_openid` (`openid`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of people
-- ----------------------------
INSERT INTO `people` VALUES ('1', 'fromUser', null, '22', '0', '9', '14', null, '2016-11-24 13:44:54', '2016-11-24 17:39:56', '2016-11-24 13:44:54');
INSERT INTO `people` VALUES ('2', 'odCuQtyJJVNz-vrgRobWQj3yk5sc', null, null, '0', '0', '0', null, '2017-02-14 14:26:58', '2017-02-14 14:26:57', '2017-02-14 14:26:58');
INSERT INTO `people` VALUES ('3', 'odCuQtydm6RGBdh0MMDNHevVCESg', null, null, '140', '140', '0', '2017-03-01 19:24:22', '2017-02-15 08:40:45', '2017-02-15 08:40:44', '2017-03-01 19:24:22');

-- ----------------------------
-- Table structure for people_login
-- ----------------------------
DROP TABLE IF EXISTS `people_login`;
CREATE TABLE `people_login` (
  `people_id` int(11) NOT NULL,
  `account` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `update_time` datetime NOT NULL,
  UNIQUE KEY `k_unq_people_id` (`people_id`) USING HASH,
  KEY `k_people_id` (`people_id`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of people_login
-- ----------------------------
INSERT INTO `people_login` VALUES ('2', 'kuangye', '123465', '2016-11-17 16:44:44');
