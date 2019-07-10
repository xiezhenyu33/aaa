package com.parko.zkcenter.entity.cond;

import java.io.Serializable;

import lombok.Data;

/**
 * 文件条件
 * @author Administrator
 *
 */
@Data
public class FileCond implements Serializable{

	private String fileUrl;
	
	private String fileName;
}
