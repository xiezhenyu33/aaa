package com.parko.zkcenter.service.zkcenter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parko.system.entity.pojo.CommomPage;
import com.parko.system.entity.pojo.PageBean;
import com.parko.system.utils.CriteriaUtil;
import com.parko.system.utils.EntityUtil;
import com.parko.system.utils.RestrictionsUtil;
import com.parko.zkcenter.dao.zkcenter.TSpotCheckDao;
import com.parko.zkcenter.entity.zkcenter.TSpotCheck;
import io.netty.util.internal.StringUtil;

/**
 * 医院抽检实体（消息）业务处理接口实现类
 * @author Administrator
 *
 */
@Service
public class TSpotCheckServiceImpl implements TSpotCheckService {

	@Autowired
	private TSpotCheckDao tSpotCheckDao;//医院抽检实体数据库交互

	@PersistenceContext
	 private EntityManager entityManger;
	
	/**
	 * 保存医生抽检信息数据
	 * @throws IllegalAccessException 
	 */
	@Override
	public TSpotCheck saveTSpotCheck(TSpotCheck tSpotCheck, int curUserId) throws IllegalAccessException {
		
		tSpotCheck.setDataRemoveType(0);
		if(tSpotCheck.getId()==0) {//新增
			tSpotCheck= (TSpotCheck) EntityUtil.entityFormatField(EntityUtil.createOperation,curUserId,tSpotCheck);
		}else {//修改
			tSpotCheck= (TSpotCheck) EntityUtil.entityFormatField(EntityUtil.updateOperation,curUserId,tSpotCheck);
		}
		tSpotCheck=tSpotCheckDao.save(tSpotCheck);
		return tSpotCheck;
	}
/**
 * 根据主键id获取对象数据
 */
	@Override
	public TSpotCheck findById(int checkId) {
		TSpotCheck tSpotCheck =new TSpotCheck();
		Optional<TSpotCheck> tOptional=tSpotCheckDao.findById(checkId);
		if(tOptional.isPresent()) {
			tSpotCheck=tOptional.get();
		}
		return tSpotCheck;
	}
/**
 * 根据id删除对应的数据
 * @throws IllegalAccessException 
 */
	@Override
	public TSpotCheck deleteById(int checkId, int curUserId) throws IllegalAccessException {
		TSpotCheck tSpotCheck=findById(checkId);
		tSpotCheck= (TSpotCheck) EntityUtil.entityFormatField(EntityUtil.deleteOperation,curUserId,tSpotCheck);
		tSpotCheck=tSpotCheckDao.save(tSpotCheck);
		return tSpotCheck;
	}
/**
 * 根据对象获取对应的数据列表
 */
	@Override
	public List<TSpotCheck> geTSpotChecks(TSpotCheck tSpotCheck) {
		   CriteriaUtil<TSpotCheck> criteriaUtil = new CriteriaUtil<TSpotCheck>();
		      if(tSpotCheck.getUserId()>0){
		    	  criteriaUtil.add(RestrictionsUtil.eq("userId",tSpotCheck.getUserId(),true));
		      }
		      if(!StringUtil.isNullOrEmpty(tSpotCheck.getCheckStatus())){
		    	  criteriaUtil.add(RestrictionsUtil.eq("checkStatus",tSpotCheck.getCheckStatus(),true));
		      }
		      criteriaUtil.add(RestrictionsUtil.eq("dataRemoveType",0,true));
		      List<TSpotCheck> tSpotChecks=tSpotCheckDao.findAll(criteriaUtil);
		      return tSpotChecks;
	}
	/**
	 * 批量保存用户数据
	 * @throws IllegalAccessException 
	 */
@Override
public List<TSpotCheck> saveAll(List<TSpotCheck> tSpotChecks,int curUserId) throws IllegalAccessException {
	for(TSpotCheck tSpotCheck:tSpotChecks) {
		tSpotCheck=saveTSpotCheck(tSpotCheck, curUserId);
	}
	return tSpotChecks;
}
/**
 * 分页查询用户医院抽检消数据列表
 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public PageBean<Map<String, Object>> getUsersSpotCheckOnPage(CommomPage commomPage, int userId) {
		 int pageSize=commomPage.getPageSize();
		 int pageNum=commomPage.getPageNum();
		
    	 String sql="SELECT\r\n" + 
    	 		"	ts.id \"id\",\r\n" + 
    	 		" ts.title \"title\",\r\n" + 
    	 		" substring(ts.create_time from 0 for 10) \"createTime\",\r\n" + 
    	 		" (select ta.affiliate_name from t_affiliate ta where ta.id=ts.affiliate_id) \"affiliate\"\r\n" + 
    	 		" FROM t_spot_check ts \r\n" + 
    	 		" WHERE 1=1 and ts.user_id="+userId+" and ts.data_remove_type = 0 \r\n" + 
    	 		" ORDER BY CAST (ts.create_time AS TIMESTAMP) DESC\r\n" ;
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
