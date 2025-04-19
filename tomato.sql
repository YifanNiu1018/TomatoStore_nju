/*
 Navicat MySQL Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80037 (8.0.37)
 Source Host           : localhost:3306
 Source Schema         : tomato

 Target Server Type    : MySQL
 Target Server Version : 80037 (8.0.37)
 File Encoding         : 65001

 Date: 18/04/2025 21:17:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `telephone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_f6xpj7h12wr185bqhfi1hqlbr`(`user_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of account
-- ----------------------------
INSERT INTO `account` VALUES (1, 'https://via.placeholder.com/1301x34/de16fc/e43b93.gif', 'k1z60@yeah.net', '吉安市', '楼若汐', '$2a$10$zD5I8mfk1/x5mftqM4.O1.BP.b4TP7HLfNbxlvwATBpgJJOG77CYi', 'admin', '18644389579', 'odLLhkgNxOpG');
INSERT INTO `account` VALUES (2, 'https://via.placeholder.com/64x435/fb2b9b/a51afa.gif', 'vDvGfScwjQsO@zIkCrBndCKtJ.com', '南汉市', '庆伟', '$2a$10$uvXUZ8Gk85qWcr2UJkSAKeBiWdvwSADfI1Q38/uz/0daX/BJ7Wgsa', 'admin', '16029836228', 'vDvGfScwjQsO');
INSERT INTO `account` VALUES (3, 'https://via.placeholder.com/1706x1792/7dfde7/ecace6.jpg', 'k7znlr.th625@163.com', '武州市', '隆雨欣', '$2a$10$2vqGA5EkX.rNxzNbftz3NezLdpmJc5MLr.XYCMI.qcls33VaP7My6', 'admin', '17455515958', 'hFfaBhCdIxcZ');
INSERT INTO `account` VALUES (4, 'https://via.placeholder.com/816x1499/9ca83f/2870bf.jpg', 'HLmLspbLhQhi@ObhxpZLBjmWG.com', '厦门市', '前瑜', '$2a$10$76JbtIc4.xxxv9/7V3x9jOjwu1IR5ahk//khotTlY0lBv0GKr8o4O', 'admin', '19764269218', 'HLmLspbLhQhi');
INSERT INTO `account` VALUES (5, 'https://via.placeholder.com/112x1440/ff8cba/ddcd3b.jpeg', 'lmfmhh.j25@yeah.net', '海口市', '覃刚', '$2a$10$yWA.4sEZFi9DM0A/NpLjku6tcvEIkLfj5syrdRlIfZdSdPFquW8he', 'admin', '14171408495', 'RvEPjwHNdSvI');
INSERT INTO `account` VALUES (6, 'https://via.placeholder.com/1333x817/049fa3/baad6e.webp', 'tdstGnIDeAeK@bUEYiLzyLYbA.com', '宁徽市', '苑敬阳', '$2a$10$byvFhasZbSfrmByRIfV49Oj/fKBE7u/wOHq5hhcS/kDEYK1YACD2O', 'admin', '15011831397', 'tdstGnIDeAeK');
INSERT INTO `account` VALUES (7, '', '231250002@smail.nju.edu.cn', 'nj', '234', '$2a$10$vIq75rRt4fUa2ANAa6Qw4utrzbd25eKjVNjpd7mtAKePPrqCqEUta', 'customer', '13333333333', '234');
INSERT INTO `account` VALUES (8, '', '', '', '1', '$2a$10$XvSzMOi4TyLm1fH8bf2VbuR72hMgj9KpY7O2zGyUdSVTFdCR23Iyq', 'customer', '', '1');

-- ----------------------------
-- Table structure for carts
-- ----------------------------
DROP TABLE IF EXISTS `carts`;
CREATE TABLE `carts`  (
  `cart_item_id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NULL DEFAULT NULL,
  `quantity` int NULL DEFAULT NULL,
  `user_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`cart_item_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of carts
-- ----------------------------

-- ----------------------------
-- Table structure for carts_orders_relation
-- ----------------------------
DROP TABLE IF EXISTS `carts_orders_relation`;
CREATE TABLE `carts_orders_relation`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `cart_item_id` int NOT NULL COMMENT '关联购物车商品ID',
  `order_id` int NOT NULL COMMENT '关联订单ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `cart_item_id`(`cart_item_id` ASC) USING BTREE,
  INDEX `order_id`(`order_id` ASC) USING BTREE,
  CONSTRAINT `carts_orders_relation_ibfk_1` FOREIGN KEY (`cart_item_id`) REFERENCES `carts` (`cart_item_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `carts_orders_relation_ibfk_2` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '购物车商品与订单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of carts_orders_relation
-- ----------------------------

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `order_id` int NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `payment_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '支付方式',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL DEFAULT 'PENDING' COMMENT '订单支付状态（PENDING, SUCCESS, FAILED, TIMEOUT）',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间',
  PRIMARY KEY (`order_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price` decimal(19, 2) NOT NULL,
  `rate` double NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of products
-- ----------------------------

-- ----------------------------
-- Table structure for specifications
-- ----------------------------
DROP TABLE IF EXISTS `specifications`;
CREATE TABLE `specifications`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `item` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `product_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK8mklfk5ymdkx8nfr7rddr60af`(`product_id` ASC) USING BTREE,
  CONSTRAINT `FK8mklfk5ymdkx8nfr7rddr60af` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of specifications
-- ----------------------------

-- ----------------------------
-- Table structure for stockpiles
-- ----------------------------
DROP TABLE IF EXISTS `stockpiles`;
CREATE TABLE `stockpiles`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `amount` int NOT NULL,
  `frozen` int NOT NULL,
  `product_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKgfj7xw0trgrvvisavbabl3nd8`(`product_id` ASC) USING BTREE,
  CONSTRAINT `FKgfj7xw0trgrvvisavbabl3nd8` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of stockpiles
-- ----------------------------

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `telephone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_k8d0f2n7n88w1a16yhua64onx`(`user_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'https://via.placeholder.com/39x1381/6cf50b/377edf.webp', 'v9ru72_k18@sohu.com', '宁海市', '麴雨桐', '$2a$10$CD9UnJESDC8MiVCT0.wGIeRbWtX8qm5y0mwjvhPXc//fN9czPRCKC', 'admin', '13826412381', 'FDiEARsvtqhU');
INSERT INTO `users` VALUES (2, 'https://via.placeholder.com/782x1440/d375df/0d05b2.jpeg', 'eCvmkOrzHyKg@SudAScdqWUNj.com', '济海市', '甘明', '$2a$10$eFH7PzxQU71JhvkPotyxdeZKucFRvT5Pkgl56osAse6i0utrFirGG', 'admin', '13852880654', 'eCvmkOrzHyKg');
INSERT INTO `users` VALUES (3, 'https://via.placeholder.com/948x1548/29cfcf/ade347.png', 'LBIxayuyZUWz@vpSHarWYrpao.com', '诸原市', '独国芳', '$2a$10$e8GRcOTuX9AOjGSSaVcZ.ejrmxd9BNrJme2uAyFVHYnc0y3dtRHi6', 'admin', '19110526227', 'LBIxayuyZUWz');
INSERT INTO `users` VALUES (4, 'https://via.placeholder.com/1736x584/645a84/fbb0d6.jpeg', 'gztjcv_lly@139.com', '北头市', '张鑫', '$2a$10$o5hxdm9QtCwW4qtVWn4MsO.245BURa4V10SA2cHg0gvmKk4bRhxQi', 'admin', '16413460465', 'IPhqgdtxeVjY');
INSERT INTO `users` VALUES (5, 'https://via.placeholder.com/755x1934/7c724b/c29d4d.jpeg', 'FTMJbFlOVUVR@pkrUjUBXnWDg.com', '成口市', '圣娜', '$2a$10$Jg7tVeDunSwzFHN5yc/pcOQA2ooDGmcvyOvFUcSw/c5xePjBEmsx6', 'admin', '15248215154', 'FTMJbFlOVUVR');
INSERT INTO `users` VALUES (6, 'https://webzyll.oss-cn-shanghai.aliyuncs.com/1.png', '1@qq.com', '1', '1', '$2a$10$Bc1vQ3rzYx9r/eaUrEiWQ.90HZCZhs1v5HPbtyFLnOQgTa9Tqv106', 'customer', '13333333333', '1');

SET FOREIGN_KEY_CHECKS = 1;
