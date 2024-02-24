use yuapi;
-- 接口信息
create table if not exists yuapi.`interface_info`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `name` varchar(256) not null comment '名称',
    `description` varchar(256) null comment '描述',
    `url` varchar(512) not null comment '接口地址',
    `requestParams` text not null comment '请求参数',
    `requestHeader` text null comment '请求头',
    `responseHeader` text null comment '响应头',
    `status` int default 0 not null comment '接口状态（0-关闭，1-开启）',
    `method` varchar(256) not null comment '请求类型',
    `userId` bigint not null comment '创建人',
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDeleted` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
) comment '接口信息';

insert into yuapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('胡浩然', '曹金鑫', 'www.fernando-wiza.io', '阎展鹏', '顾健柏', 0, '卢晋鹏', 38608435);
insert into yuapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('周子涵', '江哲瀚', 'www.diana-labadie.co', '郝子轩', '余智辉', 0, '卢黎昕', 20141);
insert into yuapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('宋修洁', '史浩然', 'www.marquerite-schoen.co', '魏明杰', '苏智辉', 0, '韩晓啸', 6);
insert into yuapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('贾皓轩', '林语堂', 'www.ramon-windler.co', '刘越彬', '邹健柏', 0, '林嘉懿', 33);
insert into yuapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('韩乐驹', '贺凯瑞', 'www.amado-gusikowski.io', '武智宸', '林浩', 0, '马哲瀚', 8150887);
insert into yuapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('赖俊驰', '韦梓晨', 'www.donald-bradtke.com', '程俊驰', '金涛', 0, '萧浩宇', 2680188680);
insert into yuapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('廖思淼', '于泽洋', 'www.rex-dicki.name', '张建辉', '彭擎宇', 0, '萧瑾瑜', 43533791);
insert into yuapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('宋致远', '陶天宇', 'www.gala-reinger.co', '叶正豪', '钟鹏飞', 0, '傅越彬', 36);
insert into yuapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('薛健柏', '程琪', 'www.maryanne-cruickshank.net', '赵昊天', '叶思淼', 0, '汪修洁', 5);
insert into yuapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('邹浩轩', '邱笑愚', 'www.jamar-shanahan.name', '方烨磊', '史瑞霖', 0, '吴天磊', 322327);
insert into yuapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('谢天翊', '袁天翊', 'www.pearl-terry.com', '雷致远', '杜航', 0, '史风华', 2);
insert into yuapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('谭钰轩', '阎果', 'www.antone-ebert.com', '杨天磊', '程文昊', 0, '罗鑫鹏', 2302193883);
insert into yuapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('廖智宸', '叶子默', 'www.dale-gusikowski.co', '邓鹏煊', '韦展鹏', 0, '田黎昕', 649328);
insert into yuapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('朱晟睿', '蔡烨华', 'www.mark-senger.co', '侯烨华', '曹靖琪', 0, '卢越彬', 7);
insert into yuapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('毛炎彬', '洪煜城', 'www.ezekiel-huels.co', '段鹏煊', '莫展鹏', 0, '蒋鹏涛', 6344229);
insert into yuapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('薛煜城', '汪健雄', 'www.jaqueline-brown.co', '马鹏煊', '崔苑博', 0, '莫浩宇', 72562);
insert into yuapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('吕晓博', '郝瑞霖', 'www.kathrine-hahn.org', '冯懿轩', '胡浩', 0, '覃正豪', 2521);
insert into yuapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('张笑愚', '朱雪松', 'www.joye-nader.org', '蒋振家', '汪伟祺', 0, '许绍辉', 863369);
insert into yuapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('薛荣轩', '赵峻熙', 'www.jay-schuppe.net', '廖鹤轩', '高文昊', 0, '彭嘉懿', 30957);
insert into yuapi.`interface_info` (`name`, `description`, `url`, `requestHeader`, `responseHeader`, `status`, `method`, `userId`) values ('于文', '崔思', 'www.garry-wisozk.com', '董伟泽', '段明辉', 0, '许雨泽', 3581138);

-- 用户调用接口关系表
create table if not exists yuapi.`user_interface_info`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `userId` bigint not null comment '调用用户 id',
    `interfaceInfoId` bigint not null comment '接口 id',
    `totalNum` int default 0 not null comment '总调用次数',
    `leftNum` int default 0 not null comment '剩余调用次数',
    `status` int default 0 not null comment '0-正常，1-禁用',
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDelete` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
) comment '用户调用接口关系';