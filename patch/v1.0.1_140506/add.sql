drop index finance_vacantperiod_create_by on jeesite.finance_vacantperiod;

drop table if exists jeesite.finance_vacantperiod;

/*==============================================================*/
/* Table: finance_vacantperiod                                  */
/*==============================================================*/
create table jeesite.finance_vacantperiod
(
   id                   national varchar(64) not null comment '编号',
   name                 national varchar(255) comment '明细名称',
   rent_id              national varchar(64) not null comment '明细id',
   sdate                datetime comment '起始时间',
   edate                datetime comment '结束时间',
   type                 char(1) not null default '1' comment '空置类别',
   sn                   national varchar(64) comment '序号',
   create_by            national varchar(64) comment '创建者',
   create_date          datetime comment '创建时间',
   update_by            national varchar(64) comment '更新者',
   update_date          datetime comment '更新时间',
   remarks              national varchar(255) comment '备注信息',
   del_flag             national char(1) not null default '0' comment '删除标志',
   primary key (id)
);

alter table jeesite.finance_vacantperiod comment '空置期设置';

/*==============================================================*/
/* Index: finance_vacantperiod_create_by                        */
/*==============================================================*/
create index finance_vacantperiod_create_by on jeesite.finance_vacantperiod
(
   create_by
);



/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2014-5-6 22:02:35                            */
/*==============================================================*/


alter table jeesite.finance_rent
   drop column rentout_profitmonth;

alter table jeesite.finance_rent
   drop column rentout_person;

alter table jeesite.finance_rent
   drop column rentout_paytype;

alter table jeesite.finance_rent
   drop column rentout_nextpaydate;

alter table jeesite.finance_rent
   drop column rentout_amountreceived;

alter table jeesite.finance_rent
   drop column rentout_lastpayedate;

alter table jeesite.finance_rent
   drop column rentout_lastpaysdate;

alter table jeesite.finance_rent
   drop column rentout_rentmonth;

alter table jeesite.finance_rent
   drop column rentout_deposit;

alter table jeesite.finance_rent
   drop column rentout_edate;

alter table jeesite.finance_rent
   drop column rentout_sdate;

alter table jeesite.finance_rent
   drop column rentin_nextpaydate;

alter table jeesite.finance_rent
   drop column rentin_lastpayedate;

alter table jeesite.finance_rent
   drop column rentin_lastpaysdate;

alter table jeesite.finance_rent
   drop column rentin_rentmonth;

alter table jeesite.finance_rent
   drop column rentin_deposit;

alter table jeesite.finance_rent
   drop column rentin_edate;

alter table jeesite.finance_rent
   drop column rentin_sdate;

alter table jeesite.finance_rent
   drop column rentin_paytype;

alter table jeesite.finance_rent
   drop column rentin_person;

alter table jeesite.finance_rent
   drop column vacant_period;

/*==============================================================*/
/* Table: finance_rent_month                                    */
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
   infotype             varchar(15) comment '信息类别',
   create_by            national varchar(64) comment '创建者',
   create_date          datetime comment '创建时间',
   update_by            national varchar(64) comment '更新者',
   update_date          datetime comment '更新时间',
   remarks              national varchar(255) comment '备注信息',
   del_flag             national char(1) not null default '0' comment '删除标志',
   primary key (id)
);

alter table finance_rentmonth comment '包租单月记录';
