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

import com.parko.system.entity.pojo.PageBean;
import com.parko.system.service.sys.TSysUserService;
import com.parko.system.utils.CriteriaUtil;
import com.parko.system.utils.EntityUtil;
import com.parko.system.utils.RestrictionsUtil;
import com.parko.zkcenter.dao.zkcenter.TAdverseEventsReportingDao;
import com.parko.zkcenter.entity.cond.GetEventsOnPageCond;
import com.parko.zkcenter.entity.zkcenter.TAdverseEventsReporting;
import com.parko.zkcenter.utils.JavaCommomUtils;

import io.netty.util.internal.StringUtil;
import io.swagger.annotations.ApiParam;

/**
 * 不良事件上报数据业务处理接口实现类
 * @author Administrator
 *
 */
@Service
public class TAdverseEventsReportingServiceImpl implements TAdverseEventsReportingService {

	@Autowired
	private TAdverseEventsReportingDao tAdverseEventsReportingDao;//不良事件上报数据交互

	@Autowired
	private TSysUserService tSysUserService;//系统用户业务处理接口
	
	@PersistenceContext
	 private EntityManager entityManger;
	
	/**
	 * 保存不良事件上报数据
	 * @throws IllegalAccessException 
	 */
	@Override
	public TAdverseEventsReporting saveTAdverseEventsReporting(TAdverseEventsReporting tAdverseEventsReporting,
			int curUserId) throws IllegalAccessException {
		tAdverseEventsReporting.setDataRemoveType(0);
		if(tAdverseEventsReporting.getId()==0) {//新增
			//生成六位数事件编号
			String reportNum=buildReportNum();
			tAdverseEventsReporting.setReportNum(reportNum);
			tAdverseEventsReporting= (TAdverseEventsReporting) EntityUtil.entityFormatField(EntityUtil.createOperation,curUserId,tAdverseEventsReporting);
		}else {//修改
			tAdverseEventsReporting= (TAdverseEventsReporting) EntityUtil.entityFormatField(EntityUtil.updateOperation,curUserId,tAdverseEventsReporting);
		}
		tAdverseEventsReporting=tAdverseEventsReportingDao.save(tAdverseEventsReporting);
		return tAdverseEventsReporting;
	}

	/**
	 * 获取不良事件对象根据id
	 */
	@Override
	public TAdverseEventsReporting findById(int eventId) {
		
		TAdverseEventsReporting tAdverseEventsReporting =new TAdverseEventsReporting();
		Optional<TAdverseEventsReporting> tOptional=tAdverseEventsReportingDao.findById(eventId);
		if(tOptional.isPresent()) {
			tAdverseEventsReporting=tOptional.get();
		}
		return tAdverseEventsReporting;
	}

	/**
	 * 获取不良事件数据列表
	 */
	@Override
	public List<TAdverseEventsReporting> geTAdverseEventsReportings(TAdverseEventsReporting tAdverseEventsReporting) {
		CriteriaUtil<TAdverseEventsReporting> criteriaUtil = new CriteriaUtil<TAdverseEventsReporting>();
	      criteriaUtil.add(RestrictionsUtil.eq("dataRemoveType",0,true));
	      List<TAdverseEventsReporting> tAdverseEventsReportings=tAdverseEventsReportingDao.findAll(criteriaUtil);
	      return tAdverseEventsReportings;
	}
	/**
	 * 处理六位数报表生成
	 * @return
	 */
	public String buildReportNum() {
		String reportNum="";
		long num=tAdverseEventsReportingDao.count();
		  if(num==0) {
			  reportNum="000001";
		  }else {
			 num=num+1;
			 String preStr=JavaCommomUtils.randomNumData(6-String.valueOf(num).length());
			 reportNum=preStr+num;
		  }
		return reportNum;
	}
    /**
     * 分页不良事件上报数据列表
     */
	@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	@Override
	public PageBean<Map<String, Object>> getEventsOnPage(GetEventsOnPageCond getEventsOnPageCond, int userId) {
		  int pageSize=getEventsOnPageCond.getPageSize();
		  int pageNum=getEventsOnPageCond.getPageNum();
		  int affiliateId=getEventsOnPageCond.getAffiliateId();//
		   String reportType=getEventsOnPageCond.getReportType();//上报类型 见码表 D008
		   String reportTitle=getEventsOnPageCond.getReportTitle();//上报标题
		   String badEffects=getEventsOnPageCond.getBadEffects();//不良反应内容
		   String startTime=getEventsOnPageCond.getStartTime();//
		  String endTime=getEventsOnPageCond.getEndTime();//
		  String provinceCode=getEventsOnPageCond.getProvinceCode();//所属省代码
	      String cityCode=getEventsOnPageCond.getCityCode();//所属市代码
	      String countyCode=getEventsOnPageCond.getCountyCode();//所属县代码
		  String sql="select ta.id \"id\",\r\n" + 
		  		"ta.report_type \"reportTypeCode\",\r\n" + 
		  		"(select dic.dic_subname from t_sys_second_dic dic where dic.dic_type='D008' and dic.dic_subval=ta.report_type) \"reportType\",\r\n" + 
		  		"ta.report_status \"reportStatusCode\", \r\n" + 
		  		"(select dic.dic_subname from t_sys_second_dic dic where dic.dic_type='D006' and dic.dic_subval=ta.report_status) \"reportStatus\",\r\n" + 
		  		"ta.report_title \"reportTitle\",ta.bad_effects \"badEffects\",\r\n" + 
		  		"ta.affiliate_id \"affiliateId\",\r\n" + 
		  		" taa.affiliate_name \"affiliateName\",\r\n" + 
		  		"(case when ta.report_status='1' then '-' else ta.create_time end) \"createTime\"\r\n" + 
		  		"from T_ADVERSE_EVENTS_REPORTING ta left join t_affiliate taa on (taa.id=ta.affiliate_id) where  ta.data_remove_type=0";
		  if(!StringUtil.isNullOrEmpty(reportTitle)){
			  sql=sql+" and ta.report_title like '%"+reportTitle+"%'";
	      }
		  if(!StringUtil.isNullOrEmpty(badEffects)){
	    	  sql=sql+" and ta.bad_effects like '%"+badEffects+"%'";
	      }
	      if(!StringUtil.isNullOrEmpty(reportType)){
	    	  sql=sql+" and ta.report_type ='"+reportType+"'";
	      }
	      if(affiliateId>0) {
	    	  sql=sql+" and ta.affiliate_id ='"+affiliateId+"'";
	      }
	      if(!StringUtil.isNullOrEmpty(startTime)){
	    	  sql=sql+" and ta.create_time >='"+startTime+" 00:00:00'";
	      }
	      if(!StringUtil.isNullOrEmpty(endTime)){
	    	  sql=sql+" and ta.create_time <='"+endTime+" 23:59:59'";
	      }
	      if(!StringUtil.isNullOrEmpty(provinceCode)) {
			 sql=sql+" and taa.province_code ='"+provinceCode+"'";
		 }
		 if(!StringUtil.isNullOrEmpty(cityCode)) {
			 sql=sql+" and taa.city_code= '"+cityCode+"'";
		 }
		 if(!StringUtil.isNullOrEmpty(countyCode)) {
			 sql=sql+" and taa.county_code = '"+countyCode+"'";
		 }
	      sql=sql+" and ta.create_user in "+tSysUserService.getAffiliateByRightOnUserId(userId);
			 sql=sql+" order by cast(ta.create_time as timestamp)  desc";
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
