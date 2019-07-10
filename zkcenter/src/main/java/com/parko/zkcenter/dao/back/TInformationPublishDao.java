package com.parko.zkcenter.dao.back;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import com.parko.zkcenter.entity.back.TInformationPublish;

/**
 * 信息资料发布数据库交互
 * @author Administrator
 *
 */
public interface TInformationPublishDao  extends CrudRepository<TInformationPublish, Integer>, JpaRepository<TInformationPublish, Integer>, JpaSpecificationExecutor<TInformationPublish>{

}
