package com.parko.zkcenter.service.report;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.parko.system.entity.pojo.PageBean;
import com.parko.system.service.sys.TSysUserService;
import com.parko.system.utils.CriteriaUtil;
import com.parko.system.utils.EntityUtil;
import com.parko.system.utils.RestrictionsUtil;
import com.parko.zkcenter.dao.report.TQualityControlReportDao;
import com.parko.zkcenter.entity.cond.GetReportsOnPageCond;
import com.parko.zkcenter.entity.report.TQualityControlReport;
import com.parko.zkcenter.utils.JavaCommomUtils;
import io.netty.util.internal.StringUtil;

/**
 * 质控报表数据业务处理接口实现类
 * @author Administrator
 *
 */
@Service
public class TQualityControlReportServiceImpl implements TQualityControlReportService{

	@Autowired
	private TQualityControlReportDao tQualityControlReportDao;//质控报表数据交互

	@Autowired
	private TSysUserService tSysUserService;//系统用户业务处理接口
	
	@PersistenceContext
	 private EntityManager entityManger;
	
	/**
	 * 保存广东省综合医院摸底调查表
	 * @throws IllegalAccessException 
	 */
	@Override
	public synchronized TQualityControlReport saveQualityControlReport(TQualityControlReport tQualityControlReport, int curUserId) throws IllegalAccessException {
		tQualityControlReport.setDataRemoveType(0);
		if(tQualityControlReport.getId()==0) {//新增数据
			//生成六位数报表编号
			String reportNum=buildReportNum();
			tQualityControlReport.setReportNum(reportNum);
			tQualityControlReport=(TQualityControlReport) EntityUtil.entityFormatField(EntityUtil.createOperation,curUserId,tQualityControlReport);
		}else {//修改数据
			tQualityControlReport=(TQualityControlReport) EntityUtil.entityFormatField(EntityUtil.updateOperation,curUserId,tQualityControlReport);
		}
		tQualityControlReport=tQualityControlReportDao.save(tQualityControlReport);
		return tQualityControlReport;
	}
  /**
   * 获取用户个人的广东省综合医院摸底调查表数据列表
   */
	@Override
	public List<TQualityControlReport> getQlyCtlReports(TQualityControlReport tQualityControlReport) {
		 CriteriaUtil<TQualityControlReport> criteriaUtil = new CriteriaUtil<TQualityControlReport>();
		 if(tQualityControlReport.getCreateUser()>0) {
			  criteriaUtil.add(RestrictionsUtil.eq("createUser",tQualityControlReport.getCreateUser(),true));
		 }
		  criteriaUtil.add(RestrictionsUtil.eq("dataRemoveType",0,true));
		  Sort sort=new Sort(Sort.Direction.DESC,"createTime");
		  List<TQualityControlReport> tQualityControlReports=tQualityControlReportDao.findAll(criteriaUtil,sort);
		return tQualityControlReports;	  
	}
	/**
	 * 获取表详情数据
	 */
@Override
public TQualityControlReport findById(int id) {
	TQualityControlReport tQualityControlReport=new TQualityControlReport();
	Optional<TQualityControlReport> tQualityControlReportOp=tQualityControlReportDao.findById(id);
	if(tQualityControlReportOp.isPresent()) {
		tQualityControlReport=tQualityControlReportOp.get();
	}
	return tQualityControlReport;
}
/**
 * 分页查询质控报表数据列表
 */
	@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	@Override
	public PageBean<Map<String, Object>> getReportsOnPage(GetReportsOnPageCond getReportsOnPageCond,int userId) {
		  int pageSize=getReportsOnPageCond.getPageSize();
		  int pageNum=getReportsOnPageCond.getPageNum();
		  String affiliateName=getReportsOnPageCond.getAffiliateName();
		  String reportType=getReportsOnPageCond.getReportType();//报表类型 0 评分标准 1建设管理 2 工作开展调查 3 摸底调查
		  String startTime=getReportsOnPageCond.getStartTime();
		  String endTime=getReportsOnPageCond.getEndTime();
		  String reportStatus=getReportsOnPageCond.getReportStatus();
		  String sql="select tq.id \"id\", \r\n" +
		        " tq.report_num \"reportNum\","+
		  		" tq.report_name \"reportName\",\r\n" + 
		  		" tq.affiliate_name \"affiliateName\",\r\n" + 
		  		" tq.report_status \"reportStatusCoe\",\r\n" + 
		  		" (select dic.dic_subname from t_sys_second_dic dic where dic.dic_type='D006' and dic.dic_subval=tq.report_status) \"reportStatus\",\r\n" + 
		  		" (case when tq.report_status='1' then '-' else tq.create_time end) \"createTime\"\r\n" + 
		  		" from T_QUALITY_CONTROL_REPORT tq where tq.data_remove_type=0 ";
		  if(!StringUtil.isNullOrEmpty(affiliateName)){
			  sql=sql+" and tq.affiliate_name = '"+affiliateName+"'";
	      }
	      if(!StringUtil.isNullOrEmpty(reportType)){
	    	  sql=sql+" and tq.report_type ='"+reportType+"'";
	      }
	      if(!StringUtil.isNullOrEmpty(startTime)){
	    	  sql=sql+" and tq.create_time >='"+startTime+" 00:00:00'";
	      }
	      if(!StringUtil.isNullOrEmpty(endTime)){
	    	  sql=sql+" and tq.create_time <='"+endTime+" 23:59:59'";
	      }
	      if(!StringUtil.isNullOrEmpty(reportStatus)){
	    	  sql=sql+" and tq.report_status ='"+reportStatus+"'";
	      }
	      sql=sql+" and tq.create_user in "+tSysUserService.getAffiliateByRightOnUserId(userId);
			 sql=sql+" order by cast(tq.create_time as timestamp)  desc";
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
	/**
	 * 处理六位数报表生成
	 * @return
	 */
	public String buildReportNum() {
		String reportNum="";
		long num=tQualityControlReportDao.count();
		  if(num==0) {
			  reportNum="000001";
		  }else {
			 num=num+1;
			 String preStr=JavaCommomUtils.randomNumData(6-String.valueOf(num).length());
			 reportNum=preStr+num;
		  }
		return reportNum;
	}
}
