CREATE TABLE `merchant_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `merchant_id` varchar(24) COLLATE utf8_bin DEFAULT NULL COMMENT '商家ID',
  `merchant_name` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '商家名称',
  `country` varchar(2) COLLATE utf8_bin DEFAULT NULL COMMENT '国家',
  `category` varchar(6) COLLATE utf8_bin DEFAULT NULL COMMENT '经营类目（1-餐饮；2-出行）',
  `sub_category` varchar(6) COLLATE utf8_bin DEFAULT NULL COMMENT '细分品类(餐饮类：1-泰餐；2-西餐；……)',
  `city` varchar(4) COLLATE utf8_bin DEFAULT NULL COMMENT '城市（整套的编码-或者取谷歌）',
  `district` varchar(8) COLLATE utf8_bin DEFAULT NULL COMMENT '行政区（整套的编码-或者取谷歌）',
  `street` varchar(8) COLLATE utf8_bin DEFAULT NULL COMMENT '街道（整套的编码-或者取谷歌）',
  `biz_area_id` varchar(4) COLLATE utf8_bin DEFAULT NULL COMMENT '所属商圈(1-巴吞旺；2吞武里；)',
  `biz_area_name` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '商圈名称',
  `merchant_tel` varchar(15) COLLATE utf8_bin DEFAULT NULL COMMENT '商家电话',
  `biz_hours` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '营业时间（展示层）',
  `addr` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '详细地址（谷歌地图获取位置信息）',
  `lng` varchar(18) COLLATE utf8_bin DEFAULT NULL COMMENT '经度',
  `lat` varchar(18) COLLATE utf8_bin DEFAULT NULL COMMENT '纬度',
  `merchant_pic` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '店铺图片（默认头图，关闭图片服务时保证C端可用）',
  `merch_sts` varchar(2) COLLATE utf8_bin DEFAULT NULL COMMENT '商家状态，01-已保存，02-待上线，03-已上线，04-已下线，05-已停用',
  `creator` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '创建人',
  `created_date` datetime DEFAULT NULL COMMENT '创建时间',
  `modified_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `remark` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='商家信息表';



CREATE TABLE `via_merchant_base` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `merchant_id` varchar(24) COLLATE utf8_bin DEFAULT NULL COMMENT '商家ID',
  `merchant_code` varchar(8) COLLATE utf8_bin DEFAULT NULL COMMENT '商户编码',
  `country` varchar(2) COLLATE utf8_bin DEFAULT NULL COMMENT '国家',
  `channel_source` varchar(1) COLLATE utf8_bin DEFAULT NULL COMMENT '商务渠道(1-POI;2-ISV;3-自入驻)',
  `brand_id` varchar(4) COLLATE utf8_bin DEFAULT NULL COMMENT '品牌ID',
  `category` varchar(6) COLLATE utf8_bin DEFAULT NULL COMMENT '经营类目（1-餐饮；2-出行）',
  `sub_category` varchar(6) COLLATE utf8_bin DEFAULT NULL COMMENT '细分品类(餐饮类：1-泰餐；2-西餐；……)',
  `levels` varchar(3) COLLATE utf8_bin DEFAULT NULL COMMENT '商家级别',
  `city` varchar(4) COLLATE utf8_bin DEFAULT NULL COMMENT '城市（整套的编码-或者取谷歌）',
  `district` varchar(8) COLLATE utf8_bin DEFAULT NULL COMMENT '行政区（整套的编码-或者取谷歌）',
  `street` varchar(8) COLLATE utf8_bin DEFAULT NULL COMMENT '街道（整套的编码-或者取谷歌）',
  `biz_area_id` varchar(4) COLLATE utf8_bin DEFAULT NULL COMMENT '所属商圈(1-巴吞旺；2吞武里；)',
  `merchant_tel` varchar(15) COLLATE utf8_bin DEFAULT NULL COMMENT '商家电话',
  `comment_score` double(10,1) DEFAULT NULL COMMENT '评分（支持0.5）',
  `currency` varchar(3) COLLATE utf8_bin DEFAULT NULL COMMENT '币种(CNY-人民币；THP-泰铢；SGD-新加坡元；USD-美元)',
  `avg_consume` decimal(18,2) DEFAULT NULL COMMENT '人均消费',
  `biz_hours_start` datetime DEFAULT NULL COMMENT '营业开始时间(查询层)',
  `biz_hours_end` datetime DEFAULT NULL COMMENT '营业结束时间(查询层)',
  `biz_hours_type` varchar(2) COLLATE utf8_bin DEFAULT NULL COMMENT '营业时间类型(1-工作日；2-24小时；3-周末；在这里把周一到周天做个定义)',
  `web_site` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '商家网站地址',
  `lng` varchar(18) COLLATE utf8_bin DEFAULT NULL COMMENT '经度',
  `lat` varchar(18) COLLATE utf8_bin DEFAULT NULL COMMENT '纬度',
  `favor_rate` int(11) DEFAULT NULL COMMENT '好评度（没有小数，直接存int，计分比较好，定时任务计算）',
  `hits_count` int(11) DEFAULT NULL COMMENT '点击量',
  `merchant_pic` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '店铺图片（默认头图，关闭图片服务时保证C端可用）',
  `in_charge_name` varchar(320) COLLATE utf8_bin DEFAULT NULL COMMENT '门店负责人',
  `in_charge_tel` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '负责人联系电话',
  `leagl_person_certno` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '法人身份证号',
  `leagl_person_name` varchar(320) COLLATE utf8_bin DEFAULT NULL COMMENT '法人姓名',
  `biz_license_name` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '1024',
  `deposit_bank_no` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '开户行号',
  `deposit_bank_name` varchar(640) COLLATE utf8_bin DEFAULT NULL COMMENT '开户行名',
  `settle_no` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '结算账户号',
  `settle_name` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '结算账户名',
  `bd_name` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT 'BD姓名',
  `bd_tel` varchar(12) COLLATE utf8_bin DEFAULT NULL COMMENT 'BD电话',
  `merch_sts` varchar(2) COLLATE utf8_bin DEFAULT NULL COMMENT '商家状态，01-已保存，02-待上线，03-已上线，04-已下线，05-已停用',
  `creator` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '创建人',
  `modifier` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '修改人',
  `created_date` datetime DEFAULT NULL COMMENT '创建时间',
  `modified_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `extend` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '扩展字段',
  `remark` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_merchant_id` (`merchant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=384 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='商家信息主表';



CREATE TABLE `via_merchant_sub` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `merchant_id` varchar(24) COLLATE utf8_bin DEFAULT NULL COMMENT '商家ID',
  `lang` varchar(10) COLLATE utf8_bin DEFAULT NULL COMMENT '语言版本',
  `merchant_name` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '商家名称',
  `brand_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '品牌名称',
  `biz_area_name` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '商圈名称',
  `addr` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '详细地址（谷歌地图获取位置信息）',
  `floor` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '楼层地址(楼层、商铺号等)',
  `biz_hours` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '营业时间（展示层）',
  `biz_comments` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '经营备注',
  `tags` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '标签（JSON存储标签，支持随意变化）',
  `created_date` datetime DEFAULT NULL COMMENT '创建时间',
  `modified_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `extend` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '扩展字段',
  `remark` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_merch_lang` (`merchant_id`,`lang`)
) ENGINE=InnoDB AUTO_INCREMENT=462 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='商家信息子域表';



CREATE TABLE `via_merchant_obs_ref` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `merchant_id` varchar(24) COLLATE utf8_bin DEFAULT NULL COMMENT '商家ID',
  `pic_id` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '图片ID',
  `lang` varchar(10) COLLATE utf8_bin DEFAULT NULL COMMENT '语言版本',
  `pic_type` varchar(2) COLLATE utf8_bin DEFAULT NULL COMMENT '图片类型（1-环境；2-菜品；3-价目；4-官方视频；5-网友视频；6-经营证明；7-结算信息；8-签约合同；9-身份证）',
  `pic_seq` int(11) DEFAULT NULL COMMENT '图片序号（序号为1的是头图）',
  `pic_link` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '图片链接',
  `created_date` datetime DEFAULT NULL COMMENT '创建时间',
  `modified_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `extend` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '扩展字段',
  `remark` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_merch_pic_id` (`pic_id`),
  KEY `idx_merchant_obs_id` (`merchant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3029 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='商家OBS表';
