package com.parko.zkcenter.service.back;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.parko.system.entity.pojo.PageBean;
import com.parko.system.service.sys.TSysUserService;
import com.parko.system.utils.CriteriaUtil;
import com.parko.system.utils.EntityUtil;
import com.parko.system.utils.RestrictionsUtil;
import com.parko.zkcenter.dao.back.TNewsManageDao;
import com.parko.zkcenter.entity.back.TNewsManage;
import com.parko.zkcenter.entity.cond.GetNewsOnPageCond;
import io.netty.util.internal.StringUtil;

/**
 *  新闻管理数据业务处理接口实现类
 * @author Administrator
 *
 */
@Service
public class TNewsManageServiceImpl implements TNewsManageService {

	@Autowired
	private TNewsManageDao tNewsManageDao;//新闻管理实体数据交互
	
  /**
   * 保存单个新闻管理实体数据
 * @throws IllegalAccessException 
   */
	@Override
	public TNewsManage saveTNewsManage(TNewsManage tNewsManage,int curUserId) throws IllegalAccessException {
		tNewsManage.setDataRemoveType(0);
		if(tNewsManage.getId()==0) {//新增
			tNewsManage= (TNewsManage) EntityUtil.entityFormatField(EntityUtil.createOperation,curUserId,tNewsManage);
		}else {//修改
			tNewsManage= (TNewsManage) EntityUtil.entityFormatField(EntityUtil.updateOperation,curUserId,tNewsManage);
		}
		tNewsManage=tNewsManageDao.save(tNewsManage);
		return tNewsManage;
	}
/**
 * 根据主键id获取单个数据对象
 */
	@Override
	public TNewsManage findById(int newId) {

		TNewsManage tNewsManage =new TNewsManage();
		Optional<TNewsManage> tOptional=tNewsManageDao.findById(newId);
		if(tOptional.isPresent()) {
			tNewsManage=tOptional.get();
		}
		return tNewsManage;
	}
/**
 * 根据主键id删除单个数据对象
 * @throws IllegalAccessException 
 */
	@Override
	public TNewsManage deleteById(int newId,int curUserId) throws IllegalAccessException {
		TNewsManage tNewsManage=findById(newId);
		tNewsManage= (TNewsManage) EntityUtil.entityFormatField(EntityUtil.deleteOperation,curUserId,tNewsManage);
		tNewsManage=tNewsManageDao.save(tNewsManage);
		return tNewsManage;
	}
	/**
	 * 分页查询新闻数据列表
	 */
@SuppressWarnings("deprecation")
@Override
public PageBean<TNewsManage> getNewsOnPage(GetNewsOnPageCond getNewsOnPageCond,List<Integer> curUserIds) {
		  int pageSize=getNewsOnPageCond.getPageSize();
		  int pageNum=getNewsOnPageCond.getPageNum();
		  String newsType=getNewsOnPageCond.getNewsType();
		  String title=getNewsOnPageCond.getTitle();
		  String startTime=getNewsOnPageCond.getStartTime();
		  String endTime=getNewsOnPageCond.getEndTime();
		PageBean<TNewsManage> pageBean=new PageBean<TNewsManage>();
	    Pageable pageable = new PageRequest(pageNum,pageSize, Sort.Direction.DESC,"createTime");
	      CriteriaUtil<TNewsManage> criteriaUtil = new CriteriaUtil<TNewsManage>();
	      if(!StringUtil.isNullOrEmpty(title)){
	    	  criteriaUtil.add(RestrictionsUtil.like("title",title,true));
	      }
	      if(!StringUtil.isNullOrEmpty(newsType)){
	    	  criteriaUtil.add(RestrictionsUtil.eq("newsType",newsType,true));
	      }
	      if(!StringUtil.isNullOrEmpty(startTime)){
	    	  criteriaUtil.add(RestrictionsUtil.lte("createTime",startTime+" 00:00:00",true));
	      }
	      if(!StringUtil.isNullOrEmpty(endTime)){
	    	  criteriaUtil.add(RestrictionsUtil.gte("createTime",endTime+" 23:59:59",true));
	      }
	      //当前机构的用户
	      if(curUserIds!=null&&curUserIds.size()>0) {
	    	   criteriaUtil.add(RestrictionsUtil.in("createUser",curUserIds,true));
	      }
	      criteriaUtil.add(RestrictionsUtil.eq("dataRemoveType",0,true));
		  Page<TNewsManage>  tNewsManagePages=tNewsManageDao.findAll(criteriaUtil, pageable);
		  List<TNewsManage> tNewsManage=tNewsManagePages.getContent();
		  pageBean.setCurrentPage(tNewsManagePages.getNumber());
		  pageBean.setPageData(tNewsManage);
		  pageBean.setPageSize(tNewsManagePages.getSize());
		  pageBean.setTotalCount(tNewsManagePages.getTotalElements());
		  pageBean.setTotalPage(tNewsManagePages.getTotalPages());
	     return pageBean;
}
	
}
