/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2014-5-18 21:29:44                           */
/*==============================================================*/


/*==============================================================*/
/* Table: finance_cutconfig                                     */
/*==============================================================*/
create table finance_cutconfig
(
   id                   national varchar(64) not null comment '编号',
   name                 varchar(64) comment '提成名称',
   cut_code             varchar(64) comment '提成标示符',
   cut_type             varchar(64) comment '提成类别',
   person               varchar(64) comment '对应身份',
   cut_num              varchar(64) comment '提成系数',
   create_by            national varchar(64) comment '创建者',
   create_date          datetime comment '创建时间',
   update_by            national varchar(64) comment '更新者',
   update_date          datetime comment '更新时间',
   remarks              national varchar(255) comment '备注信息',
   del_flag             national char(1) not null default '0' comment '删除标志',
   primary key (id)
);

alter table finance_cutconfig comment '包租提成设置';

/*==============================================================*/
/* Index: finance_cutconfig_create_by                           */
/*==============================================================*/
create index finance_cutconfig_create_by on finance_cutconfig
(
   create_by ASC
);


/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2014-5-18 22:50:21                           */
/*==============================================================*/


drop table if exists jeesite.tmp_finance_rentmonth;

rename table jeesite.finance_rentmonth to tmp_finance_rentmonth;

/*==============================================================*/
/* Table: finance_rentmonth                                     */
/*==============================================================*/
create table finance_rentmonth
(
   id                   varchar(64) not null comment '主键',
   name                 varchar(255) comment '名称',
   rent_id              varchar(64) comment '包租id',
   person               varchar(64) comment '业务员',
   paytype              varchar(64) comment '付款方式',
   sdate                datetime comment '开始时间',
   edate                datetime comment '结束时间',
   deposit              varchar(64) comment '押金',
   rentmonth            varchar(64) comment '月租金',
   lastpaysdate         datetime comment '付款开始日期',
   lastpayedate         datetime comment '付款结束日期',
   nextpaydate          datetime comment '下次付租日期',
   amountreceived       varchar(64) comment '已收金额',
   agencyfee            varchar(15) comment '中介费',
   infotype             varchar(15) comment '信息类别',
   firstmonth_num       varchar(15) comment '是否一期的头一个月',
   busi_manager         varchar(64),
   busi_departleader    varchar(64),
   busi_teamleader      varchar(64),
   cut_type             varchar(64),
   create_by            national varchar(64) comment '创建者',
   create_date          datetime comment '创建时间',
   update_by            national varchar(64) comment '更新者',
   update_date          datetime comment '更新时间',
   remarks              national varchar(255) comment '备注信息',
   del_flag             national char(1) not null default '0' comment '删除标志',
   primary key (id)
);

alter table finance_rentmonth comment '包租单月记录';

insert into finance_rentmonth (id, name, rent_id, person, paytype, sdate, edate, deposit, rentmonth, lastpaysdate, lastpayedate, nextpaydate, amountreceived, agencyfee, infotype, firstmonth_num, create_by, create_date, update_by, update_date, remarks, del_flag)
select id, name, rent_id, person, paytype, sdate, edate, deposit, rentmonth, lastpaysdate, lastpayedate, nextpaydate, amountreceived, agencyfee, infotype, firstmonth_num, create_by, create_date, update_by, update_date, remarks, del_flag
from jeesite.tmp_finance_rentmonth;


/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2014-5-18 22:59:54                           */
/*==============================================================*/


drop table if exists jeesite.tmp_finance_rentmonth;

rename table jeesite.finance_rentmonth to tmp_finance_rentmonth;

/*==============================================================*/
/* Table: finance_rentmonth                                     */
/*==============================================================*/
create table finance_rentmonth
(
   id                   varchar(64) not null comment '主键',
   name                 varchar(255) comment '名称',
   rent_id              varchar(64) comment '包租id',
   person               varchar(64) comment '业务员',
   paytype              varchar(64) comment '付款方式',
   sdate                datetime comment '开始时间',
   edate                datetime comment '结束时间',
   deposit              varchar(64) comment '押金',
   rentmonth            varchar(64) comment '月租金',
   lastpaysdate         datetime comment '付款开始日期',
   lastpayedate         datetime comment '付款结束日期',
   nextpaydate          datetime comment '下次付租日期',
   amountreceived       varchar(64) comment '已收金额',
   agencyfee            varchar(15) comment '中介费',
   infotype             varchar(15) comment '信息类别',
   firstmonth_num       varchar(15) comment '是否一期的头一个月',
   busi_manager         varchar(64),
   busi_departleader    varchar(64),
   busi_teamleader      varchar(64),
   cut_vacantperiodtype varchar(64) comment '空置期提成方案',
   cut_businesssaletype varchar(64) comment '业绩提成方案',
   create_by            national varchar(64) comment '创建者',
   create_date          datetime comment '创建时间',
   update_by            national varchar(64) comment '更新者',
   update_date          datetime comment '更新时间',
   remarks              national varchar(255) comment '备注信息',
   del_flag             national char(1) not null default '0' comment '删除标志',
   primary key (id)
);

alter table finance_rentmonth comment '包租单月记录';

insert into finance_rentmonth (id, name, rent_id, person, paytype, sdate, edate, deposit, rentmonth, lastpaysdate, lastpayedate, nextpaydate, amountreceived, agencyfee, infotype, firstmonth_num, busi_manager, busi_departleader, busi_teamleader, create_by, create_date, update_by, update_date, remarks, del_flag)
select id, name, rent_id, person, paytype, sdate, edate, deposit, rentmonth, lastpaysdate, lastpayedate, nextpaydate, amountreceived, agencyfee, infotype, firstmonth_num, busi_manager, busi_departleader, busi_teamleader, create_by, create_date, update_by, update_date, remarks, del_flag
from jeesite.tmp_finance_rentmonth;


/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2014-5-18 23:01:32                           */
/*==============================================================*/


drop table if exists jeesite.tmp_finance_rentmonth;

rename table jeesite.finance_rentmonth to tmp_finance_rentmonth;

/*==============================================================*/
/* Table: finance_rentmonth                                     */
/*==============================================================*/
create table finance_rentmonth
(
   id                   varchar(64) not null comment '主键',
   name                 varchar(255) comment '名称',
   rent_id              varchar(64) comment '包租id',
   person               varchar(64) comment '业务员',
   paytype              varchar(64) comment '付款方式',
   sdate                datetime comment '开始时间',
   edate                datetime comment '结束时间',
   deposit              varchar(64) comment '押金',
   rentmonth            varchar(64) comment '月租金',
   lastpaysdate         datetime comment '付款开始日期',
   lastpayedate         datetime comment '付款结束日期',
   nextpaydate          datetime comment '下次付租日期',
   amountreceived       varchar(64) comment '已收金额',
   agencyfee            varchar(15) comment '中介费',
   infotype             varchar(15) comment '信息类别',
   firstmonth_num       varchar(15) comment '是否一期的头一个月',
   busi_manager         varchar(64) comment '业务经理',
   busi_departleader    varchar(64) comment '业务部长',
   busi_teamleader      varchar(64) comment '业务组长',
   cut_vacantperiodtype varchar(64) comment '空置期提成方案',
   cut_businesssaletype varchar(64) comment '业绩提成方案',
   create_by            national varchar(64) comment '创建者',
   create_date          datetime comment '创建时间',
   update_by            national varchar(64) comment '更新者',
   update_date          datetime comment '更新时间',
   remarks              national varchar(255) comment '备注信息',
   del_flag             national char(1) not null default '0' comment '删除标志',
   primary key (id)
);

alter table finance_rentmonth comment '包租单月记录';

insert into finance_rentmonth (id, name, rent_id, person, paytype, sdate, edate, deposit, rentmonth, lastpaysdate, lastpayedate, nextpaydate, amountreceived, agencyfee, infotype, firstmonth_num, busi_manager, busi_departleader, busi_teamleader, cut_vacantperiodtype, cut_businesssaletype, create_by, create_date, update_by, update_date, remarks, del_flag)
select id, name, rent_id, person, paytype, sdate, edate, deposit, rentmonth, lastpaysdate, lastpayedate, nextpaydate, amountreceived, agencyfee, infotype, firstmonth_num, busi_manager, busi_departleader, busi_teamleader, cut_vacantperiodtype, cut_businesssaletype, create_by, create_date, update_by, update_date, remarks, del_flag
from jeesite.tmp_finance_rentmonth;


/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2014-5-21 16:46:24                           */
/*==============================================================*/


drop table if exists jeesite.tmp_finance_rentmonth;

rename table jeesite.finance_rentmonth to tmp_finance_rentmonth;

/*==============================================================*/
/* Table: finance_rentmonth                                     */
/*==============================================================*/
create table finance_rentmonth
(
   id                   varchar(64) not null comment '主键',
   name                 varchar(255) comment '名称',
   rent_id              varchar(64) comment '包租id',
   person               varchar(64) comment '业务员',
   paytype              varchar(64) comment '付款方式',
   sdate                datetime comment '开始时间',
   edate                datetime comment '结束时间',
   deposit              varchar(64) comment '押金',
   rentmonth            varchar(64) comment '月租金',
   lastpaysdate         datetime comment '付款开始日期',
   lastpayedate         datetime comment '付款结束日期',
   nextpaydate          datetime comment '下次付租日期',
   amountreceived       varchar(64) comment '已收金额',
   agencyfee            varchar(15) comment '中介费',
   infotype             varchar(15) comment '信息类别',
   firstmonth_num       varchar(15) comment '是否一期的头一个月',
   busi_manager         varchar(64) comment '业务经理',
   busi_departleader    varchar(64) comment '业务部长',
   busi_teamleader      varchar(64) comment '业务组长',
   cut_vacantperiodtype varchar(64) comment '空置期提成方案',
   cut_businesssaletype varchar(64) comment '业绩提成方案',
   cancelrentdate       datetime comment '退租时间',
   create_by            national varchar(64) comment '创建者',
   create_date          datetime comment '创建时间',
   update_by            national varchar(64) comment '更新者',
   update_date          datetime comment '更新时间',
   remarks              national varchar(255) comment '备注信息',
   del_flag             national char(1) not null default '0' comment '删除标志',
   primary key (id)
);

alter table finance_rentmonth comment '包租单月记录';

insert into finance_rentmonth (id, name, rent_id, person, paytype, sdate, edate, deposit, rentmonth, lastpaysdate, lastpayedate, nextpaydate, amountreceived, agencyfee, infotype, firstmonth_num, busi_manager, busi_departleader, busi_teamleader, cut_vacantperiodtype, cut_businesssaletype, create_by, create_date, update_by, update_date, remarks, del_flag)
select id, name, rent_id, person, paytype, sdate, edate, deposit, rentmonth, lastpaysdate, lastpayedate, nextpaydate, amountreceived, agencyfee, infotype, firstmonth_num, busi_manager, busi_departleader, busi_teamleader, cut_vacantperiodtype, cut_businesssaletype, create_by, create_date, update_by, update_date, remarks, del_flag
from jeesite.tmp_finance_rentmonth;


/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2014-5-21 21:19:20                           */
/*==============================================================*/


drop table if exists jeesite.tmp_finance_rentmonth;

rename table jeesite.finance_rentmonth to tmp_finance_rentmonth;

/*==============================================================*/
/* Table: finance_rentmonth                                     */
/*==============================================================*/
create table finance_rentmonth
(
   id                   varchar(64) not null comment '主键',
   name                 varchar(255) comment '名称',
   rent_id              varchar(64) comment '包租id',
   person               varchar(64) comment '业务员',
   paytype              varchar(64) comment '付款方式',
   sdate                datetime comment '开始时间',
   edate                datetime comment '结束时间',
   deposit              varchar(64) comment '押金',
   rentmonth            varchar(64) comment '月租金',
   lastpaysdate         datetime comment '付款开始日期',
   lastpayedate         datetime comment '付款结束日期',
   nextpaydate          datetime comment '下次付租日期',
   nextshouldamount     varchar(64) comment '下次应收应付金额',
   amountreceived       varchar(64) comment '已收金额',
   agencyfee            varchar(15) comment '中介费',
   infotype             varchar(15) comment '信息类别',
   firstmonth_num       varchar(15) comment '是否一期的头一个月',
   busi_manager         varchar(64) comment '业务经理',
   busi_departleader    varchar(64) comment '业务部长',
   busi_teamleader      varchar(64) comment '业务组长',
   cut_vacantperiodtype varchar(64) comment '空置期提成方案',
   cut_businesssaletype varchar(64) comment '业绩提成方案',
   cancelrentdate       datetime comment '退租时间',
   create_by            national varchar(64) comment '创建者',
   create_date          datetime comment '创建时间',
   update_by            national varchar(64) comment '更新者',
   update_date          datetime comment '更新时间',
   remarks              national varchar(255) comment '备注信息',
   del_flag             national char(1) not null default '0' comment '删除标志',
   primary key (id)
);

alter table finance_rentmonth comment '包租单月记录';

insert into finance_rentmonth (id, name, rent_id, person, paytype, sdate, edate, deposit, rentmonth, lastpaysdate, lastpayedate, nextpaydate, amountreceived, agencyfee, infotype, firstmonth_num, busi_manager, busi_departleader, busi_teamleader, cut_vacantperiodtype, cut_businesssaletype, cancelrentdate, create_by, create_date, update_by, update_date, remarks, del_flag)
select id, name, rent_id, person, paytype, sdate, edate, deposit, rentmonth, lastpaysdate, lastpayedate, nextpaydate, amountreceived, agencyfee, infotype, firstmonth_num, busi_manager, busi_departleader, busi_teamleader, cut_vacantperiodtype, cut_businesssaletype, cancelrentdate, create_by, create_date, update_by, update_date, remarks, del_flag
from jeesite.tmp_finance_rentmonth;


/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2014-5-21 21:40:45                           */
/*==============================================================*/


drop table if exists jeesite.tmp_finance_house;

rename table jeesite.finance_house to tmp_finance_house;

/*==============================================================*/
/* Table: finance_house                                         */
/*==============================================================*/
create table jeesite.finance_house
(
   id                   national varchar(64) not null comment '编号',
   busi_id              national varchar(64) not null comment '业务编号',
   name                 national varchar(255) comment '地址',
   houses               national varchar(64) comment '楼盘',
   landlord_name        national varchar(64) comment '房东姓名',
   landlord_telephone   national varchar(64) comment '房东联系方式',
   debit_card           national varchar(64) comment '转帐卡号',
   is_xingyebank        char(1) comment '是否为兴业银行',
   receive_username     varchar(64) comment '收款户名',
   receive_bank         varchar(64) comment '收款银行及营业网点',
   is_samecity          char(1) comment '是否同城',
   remit_address        varchar(164) comment '汇入地址',
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

insert into jeesite.finance_house (id, busi_id, name, houses, landlord_name, landlord_telephone, debit_card, tenant_name, tenant_telephone, team_leader, create_by, create_date, update_by, update_date, remarks, del_flag, office_id, image, is_canrent, is_cansale, area, sale_price, measure, direction, age, decorate, is_needdeposit)
select id, busi_id, name, houses, landlord_name, landlord_telephone, debit_card, tenant_name, tenant_telephone, team_leader, create_by, create_date, update_by, update_date, remarks, del_flag, office_id, image, is_canrent, is_cansale, area, sale_price, measure, direction, age, decorate, is_needdeposit
from jeesite.tmp_finance_house;

/*==============================================================*/
/* Index: finance_house_create_by                               */
/*==============================================================*/
create index finance_house_create_by on jeesite.finance_house
(
   create_by
);

/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2014-5-21 21:48:27                           */
/*==============================================================*/


drop table if exists jeesite.tmp_finance_house;

rename table jeesite.finance_house to tmp_finance_house;

/*==============================================================*/
/* Table: finance_house                                         */
/*==============================================================*/
create table jeesite.finance_house
(
   id                   national varchar(64) not null comment '编号',
   busi_id              national varchar(64) not null comment '业务编号',
   name                 national varchar(255) comment '地址',
   houses               national varchar(64) comment '楼盘',
   landlord_name        national varchar(64) comment '房东姓名',
   landlord_telephone   national varchar(64) comment '房东联系方式',
   debit_card           national varchar(64) comment '转帐卡号',
   is_xingyebank        char(1) comment '是否为兴业银行',
   receive_username     varchar(64) comment '收款户名',
   receive_bank         varchar(64) comment '收款银行及营业网点',
   is_samecity          char(1) comment '是否同城',
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

insert into jeesite.finance_house (id, busi_id, name, houses, landlord_name, landlord_telephone, debit_card, is_xingyebank, receive_username, receive_bank, is_samecity, remit_address, tenant_name, tenant_telephone, team_leader, create_by, create_date, update_by, update_date, remarks, del_flag, office_id, image, is_canrent, is_cansale, area, sale_price, measure, direction, age, decorate, is_needdeposit)
select id, busi_id, name, houses, landlord_name, landlord_telephone, debit_card, is_xingyebank, receive_username, receive_bank, is_samecity, remit_address, tenant_name, tenant_telephone, team_leader, create_by, create_date, update_by, update_date, remarks, del_flag, office_id, image, is_canrent, is_cansale, area, sale_price, measure, direction, age, decorate, is_needdeposit
from jeesite.tmp_finance_house;

/*==============================================================*/
/* Index: finance_house_create_by                               */
/*==============================================================*/
create index finance_house_create_by on jeesite.finance_house
(
   create_by
);

/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2014-5-21 21:56:47                           */
/*==============================================================*/


drop table if exists jeesite.tmp_finance_house;

rename table jeesite.finance_house to tmp_finance_house;

/*==============================================================*/
/* Table: finance_house                                         */
/*==============================================================*/
create table jeesite.finance_house
(
   id                   national varchar(64) not null comment '编号',
   busi_id              national varchar(64) not null comment '业务编号',
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
