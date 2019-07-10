package com.parko.zkcenter.dao.zkcenter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import com.parko.zkcenter.entity.zkcenter.TSpotCheck;

/**
 * 医院抽检实体数据库交互
 * @author Administrator
 *
 */
public interface TSpotCheckDao extends CrudRepository<TSpotCheck, Integer>, JpaRepository<TSpotCheck, Integer>, JpaSpecificationExecutor<TSpotCheck>{

}
