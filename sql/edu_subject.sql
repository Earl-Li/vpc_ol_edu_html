DROP TABLE IF EXISTS `edu_subject`;
CREATE TABLE `edu_subject` (
  `id` char(19) DEFAULT NULL COMMENT '课程类别ID',
  `title` varchar(42) NOT NULL COMMENT '类别名称',
  `parent_id` char(19) NOT NULL DEFAULT '0' COMMENT '父ID',
  `sort` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '排序字段',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '更新时间',
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='课程科目';

-- ----------------------------
-- Records of edu_subject
-- ----------------------------

INSERT INTO `edu_subject` VALUES ('1293200126289928194', '前端开发', '0', '0', '2020-08-11 22:58:31', '2020-08-11 22:58:31');
INSERT INTO `edu_subject` VALUES ('1293200126319288322', 'vue', '1293200126289928194', '0', '2020-08-11 22:58:31', '2020-08-11 22:58:31');
INSERT INTO `edu_subject` VALUES ('1293200126365425666', 'JavaScript', '1293200126289928194', '0', '2020-08-11 22:58:31', '2020-08-11 22:58:31');
INSERT INTO `edu_subject` VALUES ('1293200126688387074', 'JQuery', '1293200126289928194', '0', '2020-08-11 22:58:31', '2020-08-11 22:58:31');
INSERT INTO `edu_subject` VALUES ('1293200126705164289', '后端开发', '0', '0', '2020-08-11 22:58:31', '2020-08-11 22:58:31');
INSERT INTO `edu_subject` VALUES ('1293200126730330114', 'java', '1293200126705164289', '0', '2020-08-11 22:58:31', '2020-08-11 22:58:31');
INSERT INTO `edu_subject` VALUES ('1293200126772273153', 'c++', '1293200126705164289', '0', '2020-08-11 22:58:31', '2020-08-11 22:58:31');
INSERT INTO `edu_subject` VALUES ('1293200126793244674', '数据库开发', '0', '0', '2020-08-11 22:58:31', '2020-08-11 22:58:31');
INSERT INTO `edu_subject` VALUES ('1293200126810021889', 'mysql', '1293200126793244674', '0', '2020-08-11 22:58:31', '2020-08-11 22:58:31');