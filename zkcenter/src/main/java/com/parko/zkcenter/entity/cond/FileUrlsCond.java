package com.parko.zkcenter.entity.cond;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 路径数组
 * @author Administrator
 *
 */
@Data
public class FileUrlsCond  implements Serializable{

	private  List<FileCond> fileUrls;//附件路径数组
	
}
