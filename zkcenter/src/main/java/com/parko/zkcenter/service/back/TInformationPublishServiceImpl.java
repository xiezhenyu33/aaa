package com.parko.zkcenter.service.back;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.parko.system.entity.pojo.PageBean;
import com.parko.system.entity.sys.TSysRole;
import com.parko.system.entity.sys.TSysUser;
import com.parko.system.utils.CriteriaUtil;
import com.parko.system.utils.EntityUtil;
import com.parko.system.utils.RestrictionsUtil;
import com.parko.zkcenter.dao.back.TInformationPublishDao;
import com.parko.zkcenter.entity.back.TInformationPublish;
import com.parko.zkcenter.entity.cond.GetInfosOnPageCond;

import io.netty.util.internal.StringUtil;

/**
 * 信息资料发布数据业务处理接口实现类
 * @author Administrator
 *
 */
@Service
public class TInformationPublishServiceImpl implements TInformationPublishService {

	@Autowired
	private TInformationPublishDao tInformationPublishDao;//信息资料发布数据库交互

	@PersistenceContext
	 private EntityManager entityManger;
	
	/**
	 * 保存信息资料发布数据
	 * @throws IllegalAccessException 
	 */
	@Override
	public TInformationPublish saveTInformationPublish(TInformationPublish tInformationPublish, int curUserId) throws IllegalAccessException {
		tInformationPublish.setDataRemoveType(0);
		if(tInformationPublish.getId()==0) {//新增
			tInformationPublish= (TInformationPublish) EntityUtil.entityFormatField(EntityUtil.createOperation,curUserId,tInformationPublish);
		}else {//修改
			tInformationPublish= (TInformationPublish) EntityUtil.entityFormatField(EntityUtil.updateOperation,curUserId,tInformationPublish);
		}
		tInformationPublish=tInformationPublishDao.save(tInformationPublish);
		return tInformationPublish;
	}
    /**
     *  删除对应的资料信息发布数据
     * @throws IllegalAccessException 
     */
	@Override
	public TInformationPublish deleteOneInfo(int infoId, int curUserId) throws IllegalAccessException {
		TInformationPublish tInformationPublish=findById(infoId);
		tInformationPublish= (TInformationPublish) EntityUtil.entityFormatField(EntityUtil.deleteOperation,curUserId,tInformationPublish);
		tInformationPublish=tInformationPublishDao.save(tInformationPublish);
		return tInformationPublish;
	}
	/**
	 * 根据id获取对象数据
	 */
	@Override
	public TInformationPublish findById(int infoId) {
		TInformationPublish tInformationPublish=new TInformationPublish();
		Optional<TInformationPublish> optional=tInformationPublishDao.findById(infoId);
		if(optional.isPresent()) {
			tInformationPublish=optional.get();
		}
		return tInformationPublish;
	}
	/**
	 * 分页查询资料信息发布数据列表
	 */
	@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	@Override
	public PageBean<Map<String, Object>> getInfosOnPage(GetInfosOnPageCond getInfosOnPageCond,String rightSql) {
		      int pageSize=getInfosOnPageCond.getPageSize();
			  int pageNum=getInfosOnPageCond.getPageNum();
			  String infoMetType=getInfosOnPageCond.getInfoMetType();
			  String infoMetName=getInfosOnPageCond.getInfoMetName();
			  String infoType=getInfosOnPageCond.getInfoType();
			  String sql=" select ti.id \"id\",\r\n" + 
			  		" ti.info_met_name \"infoMetName\",\r\n" + 
			  		" (select dic.dic_subname from t_sys_second_dic dic where dic.dic_type='D006' and dic.dic_subval=ti.info_type) \"infoType\"\r\n" + 
			  		" from t_information_publish ti where 1=1 and ti.data_remove_type=0";
			  
			  if(!StringUtil.isNullOrEmpty(infoMetName)){
				  sql=sql+" and ti.info_met_name like '%"+infoMetName+"%'";
		      }
		      if(!StringUtil.isNullOrEmpty(infoMetType)){
		    	  sql=sql+" and ti.info_met_type ='"+infoMetType+"'";
		      }
		      if(!StringUtil.isNullOrEmpty(infoType)){
		    	  sql=sql+" and ti.info_type= '"+infoType+"'";
		      }
		      //当前机构的用户
		      if(!"".equals(rightSql)) {
		    	  sql=sql+" and ti.create_user in "+rightSql;
		      }
				 sql=sql+" order by cast(ti.create_time as timestamp)  desc";
		    	  Query nativeQuery = entityManger.createNativeQuery(sql);
		    	  Query countQuery = entityManger.createNativeQuery(sql);
		    	  // 记录数
		          int size = countQuery.getResultList().size();
		    	  // 查询起始位置
		          nativeQuery.setFirstResult(pageNum*pageSize);
		          // 查询条数
		          nativeQuery.setMaxResults(pageSize);
		          nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);//封装成map集合返回
		          List<Map<String, Object>> resultList = nativeQuery.getResultList();
		          // 封装
		          PageBean<Map<String, Object>> pageBean = new PageBean(resultList, (long)size, pageSize, pageNum);
		          int totalPage=size%pageSize==0?size/pageSize:size/pageSize+1;
		          pageBean.setTotalPage(totalPage);
		          return pageBean;
	}
	
	
}
