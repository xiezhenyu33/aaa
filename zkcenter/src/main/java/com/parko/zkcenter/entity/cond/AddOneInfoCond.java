package com.parko.zkcenter.entity.cond;


import com.parko.zkcenter.entity.back.TInformationPublish;
import lombok.Data;

/**
 * 信息资料发布添加条件
 * @author Administrator
 *
 */
@Data
public class AddOneInfoCond extends FileUrlsCond
{
     private TInformationPublish tInformationPublish;// 信息资料库实体类
    
}
