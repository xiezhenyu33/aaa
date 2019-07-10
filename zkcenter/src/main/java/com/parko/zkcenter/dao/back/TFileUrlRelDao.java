package com.parko.zkcenter.dao.back;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import com.parko.zkcenter.entity.TFileUrlRel;

/**
 * 附件路径对象关联数据库交互
 * @author Administrator
 *
 */
public interface TFileUrlRelDao extends CrudRepository<TFileUrlRel, Integer>, JpaRepository<TFileUrlRel, Integer>, JpaSpecificationExecutor<TFileUrlRel>{

}
