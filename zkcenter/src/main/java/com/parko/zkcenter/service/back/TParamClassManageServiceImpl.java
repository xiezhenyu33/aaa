package com.parko.zkcenter.service.back;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.parko.system.entity.pojo.PageBean;
import com.parko.system.utils.CriteriaUtil;
import com.parko.system.utils.EntityUtil;
import com.parko.system.utils.RestrictionsUtil;
import com.parko.zkcenter.dao.back.TParamClassManageDao;
import com.parko.zkcenter.entity.back.TParamClassManage;
import com.parko.zkcenter.entity.cond.GetParamClasssOnPageCond;
import io.netty.util.internal.StringUtil;

/**
 * 参数分类数据业务处理接口实现类
 * @author Administrator
 *
 */
@Service
public class TParamClassManageServiceImpl implements TParamClassManageService {

	 @Autowired
	 private TParamClassManageDao tParamClassManageDao;//参数分类管理数据库交互

	 /**
	  * 保存对应的数据
	 * @throws IllegalAccessException 
	  */
	@Override
	public TParamClassManage saveTParamClassManage(TParamClassManage tParamClassManage,int curUserId) throws IllegalAccessException {
		tParamClassManage.setDataRemoveType(0);
		if(tParamClassManage.getId()==0) {//新增
			tParamClassManage= (TParamClassManage) EntityUtil.entityFormatField(EntityUtil.createOperation,curUserId,tParamClassManage);
		}else {//修改
			tParamClassManage= (TParamClassManage) EntityUtil.entityFormatField(EntityUtil.updateOperation,curUserId,tParamClassManage);
		}
		tParamClassManage=tParamClassManageDao.save(tParamClassManage);
		return tParamClassManage;
	}
  /**
   * 根据主键id获取对应的数据
   */
	@Override
	public TParamClassManage findById(int classId) {
		TParamClassManage tParamClassManage=new TParamClassManage();
		Optional<TParamClassManage> optional=tParamClassManageDao.findById(classId);
		if(optional.isPresent()) {
			tParamClassManage=optional.get();
		}
		return tParamClassManage;
	}
/**
 * 根据主键id删除对应的数据
 * @throws IllegalAccessException 
 */
	@Override
	public TParamClassManage deleteTParamClassManage(int classId,int curUserId) throws IllegalAccessException {
		TParamClassManage tParamClassManage=findById(classId);
		tParamClassManage= (TParamClassManage) EntityUtil.entityFormatField(EntityUtil.deleteOperation,curUserId,tParamClassManage);
		tParamClassManage=tParamClassManageDao.save(tParamClassManage);
		return tParamClassManage;
	}
  /**
   * 根据对象获取所有的列表数据
   */
	@Override
	public List<TParamClassManage> geTParamClassManages(TParamClassManage tParamClassManage) {
		   CriteriaUtil<TParamClassManage> criteriaUtil = new CriteriaUtil<TParamClassManage>();
		      if(!StringUtil.isNullOrEmpty(tParamClassManage.getClassType())){
		    	  criteriaUtil.add(RestrictionsUtil.like("classType",tParamClassManage.getClassType(),true));
		      }
		      if(!StringUtil.isNullOrEmpty(tParamClassManage.getClassName())){
		    	  criteriaUtil.add(RestrictionsUtil.like("className",tParamClassManage.getClassName(),true));
		      }
		      criteriaUtil.add(RestrictionsUtil.eq("dataRemoveType",0,true));
		      Sort sort=new Sort(Sort.Direction.DESC,"createTime");
		      List<TParamClassManage> tParamClassManages=tParamClassManageDao.findAll(criteriaUtil,sort);
		      return tParamClassManages;
	}
	/**
	 * 分页查询参数分类(特医食品分类/规范指南分类)信息列表
	 */
@SuppressWarnings("deprecation")
@Override
public PageBean<TParamClassManage> getParamClasssOnPage(GetParamClasssOnPageCond getParamClasssOnPageCond,List<Integer> curUserIds) {
		  int pageSize=getParamClasssOnPageCond.getPageSize();
		  int pageNum=getParamClasssOnPageCond.getPageNum();
		  String className=getParamClasssOnPageCond.getClassName();
		  String classType=getParamClasssOnPageCond.getClassType();
		PageBean<TParamClassManage> pageBean=new PageBean<TParamClassManage>();
	  Pageable pageable = new PageRequest(pageNum,pageSize, Sort.Direction.DESC,"createTime");
	    CriteriaUtil<TParamClassManage> criteriaUtil = new CriteriaUtil<TParamClassManage>();
	    if(!StringUtil.isNullOrEmpty(className)){
	  	  criteriaUtil.add(RestrictionsUtil.like("className",className,true));
	    }
	    if(!StringUtil.isNullOrEmpty(classType)){
		  	  criteriaUtil.add(RestrictionsUtil.eq("classType",classType,true));
		    }
	    //当前机构的用户
	      if(curUserIds!=null&&curUserIds.size()>0) {
	    	   criteriaUtil.add(RestrictionsUtil.in("createUser",curUserIds,true));
	      }
	    criteriaUtil.add(RestrictionsUtil.eq("dataRemoveType",0,true));
		  Page<TParamClassManage>  tParamClassManagePages=tParamClassManageDao.findAll(criteriaUtil, pageable);
		  List<TParamClassManage> tParamClassManage=tParamClassManagePages.getContent();
		  pageBean.setCurrentPage(tParamClassManagePages.getNumber());
		  pageBean.setPageData(tParamClassManage);
		  pageBean.setPageSize(tParamClassManagePages.getSize());
		  pageBean.setTotalCount(tParamClassManagePages.getTotalElements());
		  pageBean.setTotalPage(tParamClassManagePages.getTotalPages());
	   return pageBean;
	}
	 
	 
}
