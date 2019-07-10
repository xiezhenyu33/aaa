package com.parko.zkcenter.service.back;

import java.util.List;

import com.parko.zkcenter.entity.TFileUrlRel;
import com.parko.zkcenter.entity.cond.FileCond;

/**
 * 附件路径关联数据业务处理接口
 * @author Administrator
 *
 */
public interface TFileUrlRelService {

	/**
	 * 批量删除资料-附件关联数据
	 * @param tFileUrlRel
	 */
	void deleteTSysRoleMenus(int relId,String urlType);

	/**
	 * 获取对应的数据列表
	 * @param tFileUrlRel
	 * @return
	 */
	List<TFileUrlRel> geTFileUrlRels(TFileUrlRel tFileUrlRel);
	/**
	 * 批量保存对应的数据
	 * @param tFileUrlRels
	 * @return
	 */
	List<TFileUrlRel> saveAll(List<TFileUrlRel> tFileUrlRels);
/**
 * 处理对应的信息-附件路径关联数据
 * @param fileUrls
 * @param urlType 
 * @return
 */
	List<TFileUrlRel> dealTFileUrlRels(List<FileCond> fileUrls,int relId, String urlType);
  /**
   * 获取对应的附件url列表
   * @param id
 * @param urlType 
   */
	 List<FileCond> getFileUrlsByRelId(int id, String urlType);
}
