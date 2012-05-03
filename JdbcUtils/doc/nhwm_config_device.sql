/*
Navicat MySQL Data Transfer

Source Server         : 127.0.0.1
Source Server Version : 50134
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50134
File Encoding         : 65001

Date: 2012-05-03 14:04:41
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `nhwm_config_device`
-- ----------------------------
DROP TABLE IF EXISTS `nhwm_config_device`;
CREATE TABLE `nhwm_config_device` (
  `ID` int(11) NOT NULL,
  `DEVICE_IP` varchar(100) NOT NULL,
  `DEVICE_ENAME` varchar(200) NOT NULL,
  `DEVICE_TYPE` varchar(200) DEFAULT NULL,
  `DEVICE_FACTORY` varchar(200) DEFAULT NULL,
  `HAS_DATA` int(11) DEFAULT '0',
  `DEVICE_CNAME` varchar(200) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of nhwm_config_device
-- ----------------------------
INSERT INTO `nhwm_config_device` VALUES ('1', '133.40.60.24', 'ename', 'g-dkb-type', '厂家', '0', '中文名');
INSERT INTO `nhwm_config_device` VALUES ('2', '10.10.10.56', '1111', '防火墙', '四川艺朗科技', '0', '绿盟_1_CN');
INSERT INTO `nhwm_config_device` VALUES ('3', '10.10.10.57', '1111', '防火墙', '四川艺朗科技', '0', '绿盟_2_CN');
INSERT INTO `nhwm_config_device` VALUES ('4', '10.10.10.71', '1111', '负载均衡', '思杰', '0', 'Citrix_CN');
INSERT INTO `nhwm_config_device` VALUES ('5', '10.10.10.111', '1111', '防火墙', '创意公司', '0', '思科_CN');
INSERT INTO `nhwm_config_device` VALUES ('6', '10.10.10.121', '1111', '路由交换机', '华为公司', '0', 'S9303-4_CN');
INSERT INTO `nhwm_config_device` VALUES ('7', '10.10.10.122', '1111', '路由交换机', '华为公司', '0', 'S9303-5_CN');
INSERT INTO `nhwm_config_device` VALUES ('8', '10.10.10.131', '1111', '防火墙', '四川艺朗科技', '0', '山石SG5150-4_CN');
INSERT INTO `nhwm_config_device` VALUES ('9', '10.10.10.52', '1111', '防火墙', '四川艺朗科技', '0', '山石SA-5040-2_CN');
INSERT INTO `nhwm_config_device` VALUES ('10', '10.10.10.54', '1111', '防火墙', '四川艺朗科技', '0', '山石SG-5150-2_CN');
INSERT INTO `nhwm_config_device` VALUES ('11', '10.10.10.130', '1111', '防火墙', '四川艺朗科技', '0', 'AX3200_CN');
INSERT INTO `nhwm_config_device` VALUES ('12', '10.10.10.120', '1111', '路由交换机', '华为公司', '0', 'S9303-7_CN');
INSERT INTO `nhwm_config_device` VALUES ('13', '10.10.10.37', '1111', '路由交换机', '华为公司', '0', 'S5600_CN');
INSERT INTO `nhwm_config_device` VALUES ('14', '10.10.10.91', '1111', '交换机', '锐捷公司', '0', '交换机_CN');
INSERT INTO `nhwm_config_device` VALUES ('15', '10.10.10.92', '1111', '路由器', '锐捷公司', '0', '路由器_CN');
INSERT INTO `nhwm_config_device` VALUES ('16', '10.10.10.93', '1111', '摩登', '锐捷公司', '0', '摩登_CN');
INSERT INTO `nhwm_config_device` VALUES ('17', '10.10.10.38', '1111', 'S9303-3', '华为公司', '0', 'S9303-3_CN');
INSERT INTO `nhwm_config_device` VALUES ('18', '10.10.10.133', '1111', '山石SG5150-2', '四川艺朗科技', '0', '山石_CN');
INSERT INTO `nhwm_config_device` VALUES ('19', '10.10.10.20', '1111', 'CDSF_BT', 'CDSF_BT', '0', 'CDSF_BT_CN');
INSERT INTO `nhwm_config_device` VALUES ('20', '10.10.10.10', '1111', 'switch3550-1', 'switch3550-1', '0', 'switch3550-1_CN');
INSERT INTO `nhwm_config_device` VALUES ('21', '10.10.10.31', '1111', '路由交换机', '华为公司', '0', '路由交换机_NE80-1_CN');
INSERT INTO `nhwm_config_device` VALUES ('22', '10.10.10.32', '1111', '路由交换机', '华为公司', '0', '路由交换机_NE80-2_CN');
INSERT INTO `nhwm_config_device` VALUES ('23', '10.10.10.33', '1111', '路由交换机', '华为公司', '0', '路由交换机_S7806-1_CN');
INSERT INTO `nhwm_config_device` VALUES ('24', 'D-IP', 'D-ENAME', 'D-TYPE', 'D-FACTORY', '0', 'D-NAME');
INSERT INTO `nhwm_config_device` VALUES ('5880', '111.131.10.122', '1111', '路由交换机', '华为公司', '0', 'S9303-5_CE');
INSERT INTO `nhwm_config_device` VALUES ('5881', '133.40.60.24', 'ename', 'g-dkb-type', '厂家', '0', '中文名++++++++++++++');
INSERT INTO `nhwm_config_device` VALUES ('5882', '133.40.60.24', 'ename', 'g-dkb-type', '厂家', '0', '中文名++++++++++++++');
INSERT INTO `nhwm_config_device` VALUES ('5883', '133.40.60.24', 'ename', 'g-dkb-type', '厂家', '0', '主键自动递增insertObjectToMySql');
