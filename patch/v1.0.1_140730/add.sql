drop index finance_housearearole_create_by on finance_housearearole;

drop table if exists finance_housearearole;

/*==============================================================*/
/* Table: finance_housearearole                                 */
/*==============================================================*/
create table finance_housearearole
(
   id                   national varchar(64) not null comment '编号',
   name                 varchar(64) comment '权限名称',
   roleperson           varchar(64) comment '权限授予人',
   areas                varchar(500) comment '授予的查看区域',
   create_by            national varchar(64) comment '创建者',
   create_date          datetime comment '创建时间',
   update_by            national varchar(64) comment '更新者',
   update_date          datetime comment '更新时间',
   remarks              national varchar(255) comment '备注信息',
   del_flag             national char(1) not null default '0' comment '删除标志',
   primary key (id)
);

alter table finance_housearearole comment '房屋按区域查询权限设置';

/*==============================================================*/
/* Index: finance_housearearole_create_by                       */
/*==============================================================*/
create index finance_housearearole_create_by on finance_housearearole
(
   create_by
);
