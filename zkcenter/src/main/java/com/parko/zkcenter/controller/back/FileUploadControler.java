package com.parko.zkcenter.controller.back;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.parko.system.entity.pojo.APPResultCommom;
import com.parko.system.entity.pojo.AppResultData;
import com.parko.zkcenter.entity.pojo.CommomParam;
import com.parko.zkcenter.utils.FileDownload;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * 质控中心文件处理部分
 * @author Administrator
 *
 */
@RestController
@RequestMapping(value = "/qualityControl/file")
@Api(tags = "质控中心文件处理部分")
public class FileUploadControler {
	
	AppResultData appResultData;//公共返回数据对象
	
	/**
	 * 附件上传
	 * @param request
	 * @param file
	 * @param fileType 0 评分标准 1 指南 2 特医食品 3 抽检 4 新闻 5 轮播图
	 * @return
	 */
	@RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
	@ApiOperation(value = "附件上传  fileType 0 评分标准 1 指南 2 特医食品 3 抽检 4 新闻 5 轮播图")
	public AppResultData fileUpload(HttpServletRequest request,@RequestParam("file") MultipartFile file,int fileType) {
		String fileSavePath = "";
		JSONObject res = new JSONObject();
		// 判断文件是否为空  
        if (!file.isEmpty()) {  
            try {  
            	fileSavePath = this.saveFileToSrv(request, file,fileType);
            	res.put("fileSavePath", fileSavePath);
        		res.put("fileName", file.getOriginalFilename());
            	appResultData=new AppResultData(APPResultCommom.SUCCESS, "", res);
            } catch (Exception e) {  
                e.printStackTrace();  
                appResultData=new AppResultData(APPResultCommom.FAIL, APPResultCommom.FAIL_MSG+e.getMessage(), null);
                return appResultData;
            }  
        }else {
        	appResultData=new AppResultData(APPResultCommom.FILE_NOEMPTY_ERROR, APPResultCommom.FILE_NOEMPTY_ERROR_MSG, null);
           return appResultData;
        }
		return appResultData;
	}
	/**
	 * 附件下载
	 * @param request
	 * @param response
	 * @param filePath
	 * @throws Exception
	 */
	@ApiOperation(value = "附件下载")
	@RequestMapping(value = "/fileDownload", method = RequestMethod.GET)
	public void download(HttpServletRequest request, HttpServletResponse response, @RequestParam String filePath) throws Exception {
		FileDownload.download(request, response, filePath, "");
	}
	/*** 
     * 保存单个文件 
     * @param file 
	 * @param i 
     * @return 
     */  
    private String saveFileToSrv(HttpServletRequest request,MultipartFile file, int filePathType) {  
        // 判断文件是否为空  
    	//获取文件服务器路径
    	String fileServerPath =CommomParam.FILE_PATH;
    	String filePath ="";
        if (!file.isEmpty()) {  
            try {  
            	switch(filePathType){
            	case 0://评分标准附件
            		 filePath =CommomParam.SCORE_FILE_PATH;
            		break;
            	case 1://指南附件
            		filePath =CommomParam.GUID_FILE_PATH;
            		break;
            	case 2://特医食品附件
            		filePath =CommomParam.SPE_FILE_PATH;
            		break;
            	case 3://抽检附件
            		filePath =CommomParam.CHECK_FILE_PATH;
            		break;
            	case 4://新闻附件
            		filePath =CommomParam.NEWS_FILE_PATH;
            		break;
            	case 5://轮播图附件
            		filePath =CommomParam.ROTA_FILE_PATH;
            		break;
            	default :
            		filePath ="";
            		break;
        	}
            	//获取源文件扩展名
//            	String fileExtendName = FileUtil.getFileExtentName(file.getOriginalFilename());
//            	String newFileName = IDGenerator.getId() + "." + fileExtendName;
            	String fileName= file.getOriginalFilename();
                // 文件保存路径

                String fileSavePath = fileServerPath + filePath +fileName;
                //如果文件夹不存在，创建
    			//创建文件服务器
    			File fileSrvPath = new File(fileServerPath + filePath);
    			if(!fileSrvPath.exists() && !fileSrvPath.isDirectory())
    			{
    				fileSrvPath.mkdirs();
    			}
                // 转存文件  
                file.transferTo(new File(fileSavePath));  
                return filePath + fileName;  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        return "";  
    } 
}
