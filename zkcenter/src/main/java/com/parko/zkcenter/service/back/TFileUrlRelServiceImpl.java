package com.parko.zkcenter.service.back;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parko.system.utils.CriteriaUtil;
import com.parko.system.utils.RestrictionsUtil;
import com.parko.zkcenter.dao.back.TFileUrlRelDao;
import com.parko.zkcenter.entity.TFileUrlRel;
import com.parko.zkcenter.entity.cond.FileCond;

/**
 *  附件路径关联数据业务处理接口实现类
 * @author Administrator
 *
 */
@Service
public class TFileUrlRelServiceImpl implements TFileUrlRelService {

	@Autowired
	private TFileUrlRelDao tFileUrlRelDao;//附件路径对象关联数据库交互

	/**
	 * 批量删除资料-附件关联数据
	 */
	@Override
	public void deleteTSysRoleMenus(int relId,String urlType) {
		 TFileUrlRel tFileUrlRel=new TFileUrlRel();
		 tFileUrlRel.setRelId(relId);
		 tFileUrlRel.setUrlType(urlType);
		List<TFileUrlRel> tFileUrlRels=geTFileUrlRels(tFileUrlRel);
		if(tFileUrlRels!=null&&tFileUrlRels.size()>0) {
			tFileUrlRelDao.deleteAll(tFileUrlRels);
		}
	}
    /**
     * 获取对应的数据列表
     */
	@Override
	public List<TFileUrlRel> geTFileUrlRels(TFileUrlRel tFileUrlRel) {
		 CriteriaUtil<TFileUrlRel> criteriaUtil = new CriteriaUtil<TFileUrlRel>();
	        if(tFileUrlRel.getRelId()>0){
	            criteriaUtil.add(RestrictionsUtil.eq("relId",tFileUrlRel.getRelId(),true));
	        }
	        if(tFileUrlRel.getUrlType()!=null&&!"".equals(tFileUrlRel.getUrlType())){
	            criteriaUtil.add(RestrictionsUtil.eq("urlType",tFileUrlRel.getUrlType(),true));
	        }
	     List<TFileUrlRel> tFileUrlRels=tFileUrlRelDao.findAll(criteriaUtil);
		return tFileUrlRels;
	}
	/**
	 *  批量保存对应的数据
	 */
	@Override
	public List<TFileUrlRel> saveAll(List<TFileUrlRel> tFileUrlRels) {
		tFileUrlRels=tFileUrlRelDao.saveAll(tFileUrlRels);
		return tFileUrlRels;
	}
	/**
	 * 处理对应的信息-附件路径关联数据
	 */
	@Override
	public List<TFileUrlRel> dealTFileUrlRels(List<FileCond> fileUrls,int relId,String urlType) {
		
		  List<TFileUrlRel> tFileUrlRels=new ArrayList<>();
		 if(fileUrls.size()>0) {//存在需要保存的信息-附件路径关联
			 //先将信息-附件路径关联删除
			  deleteTSysRoleMenus(relId,urlType);
			 //保存角色菜单关联数据
			 for(FileCond url:fileUrls) {
				 TFileUrlRel tTFileUrlRelParam=new TFileUrlRel();
				 tTFileUrlRelParam.setFileUrl(url.getFileUrl());
				 tTFileUrlRelParam.setFileName(url.getFileName());
				 tTFileUrlRelParam.setRelId(relId);
				 tTFileUrlRelParam.setUrlType(urlType);
				 tFileUrlRels.add(tTFileUrlRelParam);
			 }
			 if(tFileUrlRels.size()>0) {
				 tFileUrlRels=saveAll(tFileUrlRels);
			 }
		 }
		 return tFileUrlRels;
	}
	/**
	 * 获取对应的附件url列表
	 */
	@Override
	public List<FileCond> getFileUrlsByRelId(int relId,String urlType) {
		
		  TFileUrlRel tFileUrlRel=new TFileUrlRel();
			tFileUrlRel.setRelId(relId);
			tFileUrlRel.setUrlType(urlType);
		  List<TFileUrlRel> tFileUrlRels=geTFileUrlRels(tFileUrlRel);
		  List<FileCond> fileUrls=new ArrayList<>();
		  if(tFileUrlRels!=null&&tFileUrlRels.size()>0) {
			  for(TFileUrlRel tf:tFileUrlRels) {
				  FileCond fileCond=new FileCond();
				  fileCond.setFileUrl(tf.getFileUrl());
				  fileCond.setFileName(tf.getFileName());
				  fileUrls.add(fileCond);
			  }
		  }
		  return fileUrls;
	}
	
	
}
