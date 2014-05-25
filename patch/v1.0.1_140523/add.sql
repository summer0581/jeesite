/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2014-5-24 16:09:42                           */
/*==============================================================*/


drop table if exists jeesite.tmp_finance_house;

rename table jeesite.finance_house to tmp_finance_house;

/*==============================================================*/
/* Table: finance_house                                         */
/*==============================================================*/
create table jeesite.finance_house
(
   id                   national varchar(64) not null comment '编号',
   busi_id              national varchar(64) comment '业务编号',
   name                 national varchar(255) comment '地址',
   houses               national varchar(64) comment '楼盘',
   landlord_name        national varchar(64) comment '房东姓名',
   landlord_telephone   national varchar(64) comment '房东联系方式',
   debit_card           national varchar(64) comment '转帐卡号',
   is_xingyebank        varchar(10) comment '是否为兴业银行',
   receive_username     varchar(64) comment '收款户名',
   receive_bank         varchar(64) comment '收款银行及营业网点',
   is_samecity          varchar(10) comment '是否同城',
   remit_address        varchar(164) comment '汇入地址',
   house_source         varchar(64) comment '房屋来源',
   tenant_name          national varchar(64) comment '租户姓名',
   tenant_telephone     national varchar(64) comment '租户联系方式',
   team_leader          national varchar(64) comment '组长',
   create_by            national varchar(64) comment '创建者',
   create_date          datetime comment '创建时间',
   update_by            national varchar(64) comment '更新者',
   update_date          datetime comment '更新时间',
   remarks              national varchar(255) comment '备注信息',
   del_flag             national char(1) not null default '0' comment '删除标志',
   office_id            national varchar(64) not null comment '归属部门',
   image                national varchar(2000) comment '图片',
   is_canrent           national char(1) comment '是否可租',
   is_cansale           national char(1) comment '是否可卖',
   area                 national char(2) comment '区域',
   sale_price           national varchar(64) comment '价格',
   measure              national varchar(64) comment '面积',
   direction            national varchar(64) comment '朝向',
   age                  national varchar(64) comment '年代',
   decorate             national varchar(64) comment '装修',
   is_needdeposit       national char(1) comment '是否需要定金',
   primary key (id)
);

alter table jeesite.finance_house comment '房屋明细';

insert into jeesite.finance_house (id, busi_id, name, houses, landlord_name, landlord_telephone, debit_card, is_xingyebank, receive_username, receive_bank, is_samecity, remit_address, house_source, tenant_name, tenant_telephone, team_leader, create_by, create_date, update_by, update_date, remarks, del_flag, office_id, image, is_canrent, is_cansale, area, sale_price, measure, direction, age, decorate, is_needdeposit)
select id, busi_id, name, houses, landlord_name, landlord_telephone, debit_card, is_xingyebank, receive_username, receive_bank, is_samecity, remit_address, house_source, tenant_name, tenant_telephone, team_leader, create_by, create_date, update_by, update_date, remarks, del_flag, office_id, image, is_canrent, is_cansale, area, sale_price, measure, direction, age, decorate, is_needdeposit
from jeesite.tmp_finance_house;

/*==============================================================*/
/* Index: finance_house_create_by                               */
/*==============================================================*/
create index finance_house_create_by on jeesite.finance_house
(
   create_by
);


