package com.parko.zkcenter.dao.back;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import com.parko.zkcenter.entity.back.TNewsManage;

/**
 * 新闻管理实体数据交互
 * @author Administrator
 *
 */
public interface TNewsManageDao  extends CrudRepository<TNewsManage, Integer>, JpaRepository<TNewsManage, Integer>, JpaSpecificationExecutor<TNewsManage>{

}
