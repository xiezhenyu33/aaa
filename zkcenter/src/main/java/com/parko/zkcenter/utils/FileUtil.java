package com.parko.zkcenter.utils;

import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

/**
 * <p>Title: 封装了常用的文件操作</p>
 * <p>Description: 封装了常用的文件操作</p>
 * <p>Copyright: Copyright (c) com.swt.com</p>
 * <p>Company: www.sunyard.com</p>
 * @author zhangjy
 * @version 1.0 201703089
 */

public class FileUtil {

	/*
	 * 函数功能：解压缩文件
	 输入参数：
	   zipFileName:压缩文件全路径
	 输出参数：
	 */
	public static void unzipFile(String zipFileName) throws Exception {
		try {
			File f = new File(zipFileName);
			ZipFile zipFile = new ZipFile(zipFileName);
			if((!f.exists()) && (f.length() <= 0)) {
				throw new Exception("要解压的文件不存在!");
			}
			String strPath, gbkPath, strtemp;
			File tempFile = new File(f.getParent());
			strPath = tempFile.getAbsolutePath();
			java.util.Enumeration e = zipFile.entries();
			while(e.hasMoreElements()) {
				ZipEntry zipEnt = (ZipEntry) e.nextElement();
				File file = new File(zipEnt.getName());
				if(zipEnt.isDirectory()) {
					strtemp = strPath + "/" + zipEnt.getName();
					gbkPath = new String(strtemp.getBytes("GBK"));
					File dir = new File(gbkPath);
					dir.mkdirs();
					continue;
				} else {
					//读写文件
					InputStream is = zipFile.getInputStream(zipEnt);
					BufferedInputStream bis = new BufferedInputStream(is);

					strtemp = strPath + "/" + zipEnt.getName();

					//建目录
					String strsubdir = zipEnt.getName();
					for(int i = 0; i < strsubdir.length(); i++) {
						if(strsubdir.substring(i, i + 1).equalsIgnoreCase("/")) {
							String temp = strPath + "/"
								+ strsubdir.substring(0, i);
							File subdir = new File(temp);
							if(!subdir.exists())
								subdir.mkdir();
						}
					}

					FileOutputStream fos = new FileOutputStream(strtemp);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					int c;
					while((c = bis.read()) != -1) {
						bos.write((byte) c);
					}
					bos.close();
					fos.close();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 获取文件的扩展名
	 * @param filePath 文件路径
	 * @return 文件的扩展名，如果没有，那么返回空字符串
	 */
	public static String getFileExtentName(String filePath) {
		if(filePath.lastIndexOf(".") > 0) {
			return filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
		} else {
			return "";
		}
	}
	/**
	 * 从路径中获取文件名
	 * @param filePath 文件路径
	 * @return 文件名
	 */
	public static String getFileName(String filePath) {
		if(filePath.lastIndexOf("/") > 0) {
			return filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
		} else {
			return "";
		}
	}

	/**
	 * 拷贝一个文件到新的路径和文件名
	 * @param fromPath 源文件路径
	 * @param toPath 目标文件路径
	 */
	public static void copyFile(String fromPath, String toPath, boolean overwriteExist) throws Exception {
		//判断源文件是否存在
		File f = new File(fromPath);
		if(!f.exists() || f.isDirectory()) {
			throw new Exception("要拷贝的源文件不存在");
		}
		f = new File(toPath);
		if((f.exists() || f.isDirectory()) && !overwriteExist) {
			throw new Exception("拷贝的目标文件已经存在");
		}
		//判断目标文件是否已经存在
		int byteSum = 0;
		int byteRead = 0;
		//读/到流中
		InputStream inStream = new FileInputStream(fromPath);
		FileOutputStream fs = new FileOutputStream(toPath);
		byte[] buffer = new byte[1024];
		int length;
		while((byteRead = inStream.read(buffer)) != -1) {
			byteSum += byteRead;
			fs.write(buffer, 0, byteRead);
		}
		inStream.close();
		fs.close();
	}
	/**
	 * 拷贝一个文件到新的路径和文件名
	 * @param fromPath 源文件路径
	 * @param toPath 目标文件路径
	 */
	public static void copyFile2(String fromPath, String toPath, boolean overwriteExist) throws Exception{
		//判断源文件是否存在
		File f = new File(fromPath);
		if(!f.exists() || f.isDirectory()) {
			System.out.println("要拷贝的源文件不存在");
			return;
		}
		f = new File(toPath);
		if((f.exists() || f.isDirectory()) && !overwriteExist) {
			System.out.println("拷贝的目标文件已经存在");
			return;
		}
		//判断目标文件是否已经存在
		int byteSum = 0;
		int byteRead = 0;
		//读/到流中
		InputStream inStream = new FileInputStream(fromPath);
		FileOutputStream fs = new FileOutputStream(toPath);
		byte[] buffer = new byte[1024];
		int length;
		while((byteRead = inStream.read(buffer)) != -1) {
			byteSum += byteRead;
			fs.write(buffer, 0, byteRead);
		}
		inStream.close();
		fs.close();
	}
	/**
	 * 删除目录及其所有子目录和文件
	 * @param dirPath  要删除的目录路径
	 * @throws Exception
	 */
	public static void deleteDir(String dirPath) throws Exception {
		File fileDirToDel = new File(dirPath);

		if (!fileDirToDel.exists() || fileDirToDel.isFile()) {//如果要删除的目录不存在或是文件
			throw new Exception("要删除的目录不存在");
		}
		File[] fileList = fileDirToDel.listFiles();

		for(int i = 0; i < fileList.length; i++) {
			if(fileList[i].isFile()) {
				fileList[i].delete();
			} else if(fileList[i].isDirectory()) {
				deleteDir(fileList[i].getAbsolutePath());
				fileList[i].delete();
			}
		}
		fileDirToDel.delete();
	}

	/**
	 * 晴空目录下所有子目录和文件
	 * @param dirPath  要晴空的目录路径
	 * @throws Exception
	 */
	public static void clearDir(String dirPath) throws Exception {
		File fileDirToClear = new File(dirPath);

		if (!fileDirToClear.exists() || fileDirToClear.isFile()) {//如果要删除的目录不存在或是文件
			throw new Exception("要清空的目录不存在");
		}
		File[] fileList = fileDirToClear.listFiles();

		for(int i = 0; i < fileList.length; i++) {
			if(fileList[i].isFile()) {
				fileList[i].delete();
			} else if(fileList[i].isDirectory()) {
				deleteDir(fileList[i].getAbsolutePath());
				fileList[i].delete();
			}
		}

	}
	/**
	 * 构造目录，包括子目录
	 * @param dirPath 要构造的目录
	 * @throws Exception
	 */
	public static void makeDirs(String dirPath)throws Exception {
		int fromIndex = 0;
		int index = dirPath.indexOf("/", fromIndex) + 1;
		while(index>0) {

			String subPath = dirPath.substring(0, index);
			File f = new File(subPath);
			if((f.exists() && !f.isDirectory()) || !f.exists()) {
				f.mkdir();
			}
			fromIndex = index;
			index = dirPath.indexOf("/", fromIndex) +1;
		}


	}
	 /*
	  * 通过递归得到某一路径下所有文件（不包含文件夹）
	  */
	public static void getAllFiles(List fileList,String filePath){
		File root = new File(filePath);
	    File[] files = root.listFiles();
	    for(File file:files){     
	     if(file != null && file.isDirectory()){
		      getAllFiles(fileList,file.getAbsolutePath());
	     }else if(file != null && file.isFile()){
	    	 fileList.add(file.getAbsolutePath());
	     }else{
	    	 ;
	     }     
	    }
	 }
	public static void main(String[] argv) {
		try {
			FileUtil.makeDirs("c:/aa/bb/cc/");
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}