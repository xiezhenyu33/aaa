package com.parko.zkcenter.dao.back;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import com.parko.zkcenter.entity.back.TParamClassManage;

/**
 * 参数分类管理数据库交互
 * @author Administrator
 *
 */
public interface TParamClassManageDao extends CrudRepository<TParamClassManage, Integer>, JpaRepository<TParamClassManage, Integer>, JpaSpecificationExecutor<TParamClassManage>{

}
