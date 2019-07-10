package com.parko.zkcenter.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.parko.zkcenter.entity.pojo.CommomParam;

public class FileDownload {

	
	public static void main(String[] args) {
	

	}
    /**
     * 附件下载
     * @param request
     * @param response
     * @param filePath
     * @param string
     * @throws IOException 
     */
	@SuppressWarnings("unchecked")
	public static void download(HttpServletRequest request, HttpServletResponse response, String filePath,
			String string) throws IOException {
			Hashtable htMime = new Hashtable();
			//构造mime映射表
			htMime.put("html", "text/html");
			htMime.put("htm", "text/html");
			htMime.put("mht", "message/rfc822");
			htMime.put("mhtml", "message/rfc822");
			htMime.put("rtf", "application/rtf");
			htMime.put("doc", "application/msword");
			htMime.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
			htMime.put("pdf", "application/pdf");
			htMime.put("xls", "application/vnd.ms-excel");
			htMime.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			htMime.put("txt", "text/plain");
			htMime.put("ppt", "application/vnd.ms-powerpoint");
			htMime.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
			htMime.put("xhtml", "application/xhtml+xml");
			htMime.put("xht", "application/xhtml+xml");
			htMime.put("xml","text/xml");
			htMime.put("tif", "image/tiff");
			htMime.put("tiff", "image/tiff");
			htMime.put("bmp", "image/bmp");
			htMime.put("gif", "image/gif");
			htMime.put("jpg", "image/jpeg");
			htMime.put("jpe", "image/jpeg");
			htMime.put("jpeg", "image/jpeg");
			htMime.put("body", "text/html");
			htMime.put("rtx", "text/richtext");
			htMime.put("tsv", "text/tab-separated-values");
			htMime.put("etx", "text/x-setext");
			htMime.put("ps", "application/x-postscript");
			htMime.put("class", "application/java");
			htMime.put("csh", "application/x-csh");
			htMime.put("sh", "application/x-sh");
			htMime.put("tcl", "application/x-tcl");
			htMime.put("tex", "application/x-tex");
			htMime.put("texinfo", "application/x-texinfo");
			htMime.put("texi", "application/x-texinfo");
			htMime.put("t", "application/x-troff");
			htMime.put("tr", "application/x-troff");
			htMime.put("roff", "application/x-troff");
			htMime.put("man", "application/x-troff-man");
			htMime.put("me", "application/x-troff-me");
			htMime.put("ms", "application/x-wais-source");
			htMime.put("src", "application/x-wais-source");
			htMime.put("zip", "application/zip");
			htMime.put("bcpio", "application/x-bcpio");
			htMime.put("cpio", "application/x-cpio");
			htMime.put("gtar", "application/x-gtar");
			htMime.put("shar", "application/x-shar");
			htMime.put("sv4cpio", "application/x-sv4cpio");
			htMime.put("sv4crc", "application/x-sv4crc");
			htMime.put("tar", "application/x-tar");
			htMime.put("ustar", "application/x-ustar");
			htMime.put("dvi", "application/x-dvi");
			htMime.put("hdf", "application/x-hdf");
			htMime.put("latex", "application/x-latex");
			htMime.put("bin", "application/octet-stream");
			htMime.put("oda", "application/oda");
			htMime.put("ps", "application/postscript");
			htMime.put("eps", "application/postscript");
			htMime.put("ai", "application/postscript");
			htMime.put("nc", "application/x-netcdf");
			htMime.put("cdf", "application/x-netcdf");
			htMime.put("cer", "application/x-x509-ca-cert");
			htMime.put("exe", "application/octet-stream");
			htMime.put("gz", "application/x-gzip");
			htMime.put("Z", "application/x-compress");
			htMime.put("z", "application/x-compress");
			htMime.put("hqx", "application/mac-binhex40");
			htMime.put("mif", "application/x-mif");
			htMime.put("ief", "image/ief");
			htMime.put("ras", "image/x-cmu-raster");
			htMime.put("pnm", "image/x-portable-anymap");
			htMime.put("pbm", "image/x-portable-bitmap");
			htMime.put("pgm", "image/x-portable-graymap");
			htMime.put("ppm", "image/x-portable-pixmap");
			htMime.put("rgb", "image/x-rgb");
			htMime.put("xbm", "image/x-xbitmap");
			htMime.put("xpm", "image/x-xpixmap");
			htMime.put("xwd", "image/x-xwindowdump");
			htMime.put("au", "audio/basic");
			htMime.put("snd", "audio/basic");
			htMime.put("aif", "audio/x-aiff");
			htMime.put("aiff", "audio/x-aiff");
			htMime.put("aifc", "audio/x-aiff");
			htMime.put("wav", "audio/x-wav");
			htMime.put("mpeg", "video/mpeg");
			htMime.put("mpg", "video/mpeg");
			htMime.put("mpe", "video/mpeg");
			htMime.put("qt", "video/quicktime");
			htMime.put("mov", "video/quicktime");
			htMime.put("avi", "video/x-msvideo");
			htMime.put("movie", "video/x-sgi-movie");
			htMime.put("avx", "video/x-rad-screenplay");
			htMime.put("wrl", "x-world/x-vrml");
			htMime.put("mpv2", "video/mpeg2");		
			//获取文件服务器服务器路径
			String fileServerPath =CommomParam.FILE_PATH;
			String fileAbsolutePath = fileServerPath + "/" + filePath;
			//初始化
			File file = new File(fileAbsolutePath);
			//文件扩展名
			String fileName = FileUtil.getFileName(fileAbsolutePath);
			String fileExtendName = FileUtil.getFileExtentName(fileName);
			
			//初始化contentType
			String contentType = (String) htMime.get(fileExtendName.toLowerCase());
			if(contentType == null){
				contentType = "application/x-msdownload";
			}			
			//如下解决中文文件名下载时乱码问题
            String finalFileName = JavaCommomUtils.setParamName(request, fileName);
            String contentDisposition = "attachment;filename=" + finalFileName;

            /*
             * //openFlag=0：只打开。 openFlag=1：打开+下载
			if("0".equals(openFlag)){
				contentDisposition = "inline;filename=" + finalFileName;
			}else{
				contentDisposition = "attachment;filename=" + finalFileName;
			}*/
			//输出文件流
			if(file.exists()){
				response.reset();
				response.setCharacterEncoding("UTF-8");
				response.setContentType(contentType);
				
				// 设置response的Header，如果开启，默认浏览器会进行下载操作，如果注释掉，浏览器会默认预览。
				response.addHeader("Content-Disposition", contentDisposition);
				
				int fileLength = (int)file.length();
				response.setContentLength(fileLength);
				
	            /*如果文件长度大于0*/
	            if (fileLength != 0) {
	                /*创建输入流*/
	                InputStream inStream = new FileInputStream(file);
	                byte[] buf = new byte[4096];
	                /*创建输出流*/
	                OutputStream outStream = response.getOutputStream();
	                int readLength;
	                while (((readLength = inStream.read(buf)) != -1)) {
	                	outStream.write(buf, 0, readLength);
	              }
	                inStream.close();
	                outStream.flush();
	                outStream.close();
	            }
			}
	}

}
