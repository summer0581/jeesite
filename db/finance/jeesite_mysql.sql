SET SESSION FOREIGN_KEY_CHECKS=0;

/* Drop Tables */
DROP TABLE finance_house;
DROP TABLE finance_rent;




/* Create Tables */

CREATE TABLE finance_house (
  id VARCHAR(64) NOT NULL DEFAULT '' COMMENT '编号',
  busi_id VARCHAR(64) NOT NULL DEFAULT '' COMMENT '业务编号',
  name VARCHAR(255)  COMMENT '地址',
  houses VARCHAR(64)  COMMENT '楼盘',
  landlord_name VARCHAR(64)  COMMENT '房东姓名',
  landlord_telephone VARCHAR(64)  COMMENT '房东联系方式',
  debit_card VARCHAR(64)  COMMENT '转帐卡号',
  tenant_name VARCHAR(64)  COMMENT '租户姓名',
  tenant_telephone VARCHAR(64)  COMMENT '租户联系方式',
  team_leader VARCHAR(64)  COMMENT '组长',
  
  create_by VARCHAR(64)  COMMENT '创建者',
  create_date DATETIME DEFAULT NULL COMMENT '创建时间',
  update_by VARCHAR(64)  COMMENT '更新者',
  update_date DATETIME DEFAULT NULL COMMENT '更新时间',
  remarks VARCHAR(255)  COMMENT '备注信息',
  del_flag CHAR(1) DEFAULT '0' NOT NULL COMMENT '删除标志',
  PRIMARY KEY (id)
) COMMENT='房屋明细';

CREATE TABLE finance_rent (
  id VARCHAR(64) NOT NULL DEFAULT '' COMMENT '编号',
  house_id VARCHAR(64) NOT NULL DEFAULT '' COMMENT '房屋id',
  name VARCHAR(255)  COMMENT '房屋地址',
  rentin_person VARCHAR(64)  COMMENT '承租业务员',
  rentin_paytype VARCHAR(64)  COMMENT '承租付款方式',
  rentin_sdate DATETIME  COMMENT '承租开始时间',
  rentin_edate DATETIME  COMMENT '承租结束时间',
  rentin_deposit VARCHAR(64)  COMMENT '承租押金',
  rentin_rentmonth VARCHAR(64)  COMMENT '承租月租金',
  rentin_lastpaysdate DATETIME  COMMENT '承租上次付款开始日期',
  rentin_lastpayedate DATETIME  COMMENT '承租上次付款结束日期',
  rentin_nextpaydate DATETIME  COMMENT '承租下次付租日期',
  
  rentout_sdate DATETIME  COMMENT '出租开始时间',
  rentout_edate DATETIME  COMMENT '出租结束时间',
  rentout_deposit VARCHAR(64)  COMMENT '出租押金',
  rentout_rentmonth VARCHAR(64)  COMMENT '出租月租金',
  rentout_lastpaysdate DATETIME  COMMENT '出租上次付款开始日期',
  rentout_lastpayedate DATETIME  COMMENT '出租上次付款结束日期',
  rentout_amountreceived VARCHAR(64)  COMMENT '出租已收金额',
  rentout_nextpaydate DATETIME  COMMENT '出租下次付租日期',
  rentout_paytype VARCHAR(64)  COMMENT '出租付款方式',
  rentout_person VARCHAR(64)  COMMENT '出租业务员',
  rentout_profitmonth VARCHAR(64)  COMMENT '出租每月利润',
  
  create_by VARCHAR(64)  COMMENT '创建者',
  create_date DATETIME DEFAULT NULL COMMENT '创建时间',
  update_by VARCHAR(64)  COMMENT '更新者',
  update_date DATETIME DEFAULT NULL COMMENT '更新时间',
  remarks VARCHAR(255)  COMMENT '备注信息',
  del_flag CHAR(1) DEFAULT '0' NOT NULL COMMENT '删除标志',
  PRIMARY KEY (id)

)COMMENT='包租明细';



/* Create Indexes */

CREATE INDEX finance_house_create_by ON finance_house (create_by ASC);
CREATE INDEX finance_rent_create_by ON finance_rent (create_by ASC);




