/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2014-6-19 22:49:54                           */
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
   nextshouldremark     varchar(2000) comment '应收应付备注',
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

insert into finance_rentmonth (id, name, rent_id, person, paytype, sdate, edate, deposit, rentmonth, lastpaysdate, lastpayedate, nextpaydate, nextshouldamount, amountreceived, agencyfee, infotype, firstmonth_num, busi_manager, busi_departleader, busi_teamleader, cut_vacantperiodtype, cut_businesssaletype, cancelrentdate, create_by, create_date, update_by, update_date, remarks, del_flag)
select id, name, rent_id, person, paytype, sdate, edate, deposit, rentmonth, lastpaysdate, lastpayedate, nextpaydate, nextshouldamount, amountreceived, agencyfee, infotype, firstmonth_num, busi_manager, busi_departleader, busi_teamleader, cut_vacantperiodtype, cut_businesssaletype, cancelrentdate, create_by, create_date, update_by, update_date, remarks, del_flag
from jeesite.tmp_finance_rentmonth;
