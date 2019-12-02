package com.xbboom.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * @todo
 * @author 孟庆�?
 * @date 2015�?1�?2�?
 */
public class FileUploadUtils {
	/**
	 * 通用上传方法
	 * 
	 * @param key
	 *            模块
	 * @param request
	 * @param file
	 *            上传的文�?
	 * @return 返回文件名（包括文件在tomcat服务器上保存的路径）
	 */
	 //static String filesMd="C:/java/apache-tomcat-8.5.30/apache-tomcat-8.5.30/webapps/";
	static String filesMd="C:\\java\\apache-tomcat-8.0.51\\apache-tomcat-8.0.51\\webapps\\";
	//static String filesMd="E:/project/apache-tomcat-8.0.51/webapps/";
	public static String uploadFile(HttpServletRequest request,
			MultipartFile file) {
		String path = "";
		try {
			// 获取上传文件的文件名�?
			String suffix= file.getOriginalFilename();
			String realFileName =new Randem().getRandomChar(8) +"."+ suffix.substring(suffix.lastIndexOf(".") + 1);
//			if(realFileName.contains(".png")) {
//				realFileName="temp.png";
//			}else if(realFileName.contains(".PNG")) {
//				realFileName="temp.PNG";
//			}else if(realFileName.contains(".jpg")) {
//				realFileName="temp.jpg";
//			}else if(realFileName.contains(".JPG")) {
//				realFileName="temp.JPG";
//			}else if(realFileName.contains(".jpeg")) {
//				realFileName="temp.jpeg";
//			}else if(realFileName.contains(".JPEG")) {
//				realFileName="temp.JPEG";
//			}else if(realFileName.contains(".gif")) {
//				realFileName="temp.gif";
//			}else if(realFileName.contains(".GIF")) {
//				realFileName="temp.GIF";
//			}
			// 文件上传的存储路�?
			String ctxPath = filesMd+"imgs";
			// 创建文件
			File dirPath = new File(ctxPath);
			/**
			 * 问题描述：系统找不到指定路径 问题解析：mkdir()方法不能创建父目录不存在是的子路�?
			 */
			if (!dirPath.exists()) {
				dirPath.mkdirs();
			}
			// 上传后的文件名称前拼接年月日时分秒微秒来防止重复
			//String dateString = new Randem().getRandomChar(8);

			String real = ctxPath + File.separator + realFileName;
			// String real =ctxPath + File.separator + dateString;
			File uploadFile = new File(real);
			System.out.println(uploadFile);
			// 保存在数据库中的路径
			// path = properties.getValue(key)+File.separator +dateString
			// +realFileName;
			path = "/imgs"+File.separator + realFileName;
			// 复制，上传文�?
			FileCopyUtils.copy(file.getBytes(), uploadFile);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("上传文件出现异常", e);
		}
		return path.replaceAll("\\\\", "/");
	}
	public static String uploadAudio(HttpServletRequest request,
			MultipartFile file) {
		String path = "";
		try {
			// 获取上传文件的文件名�?
			String realFileName = file.getOriginalFilename();
			if(realFileName.contains(".mp3")) {
				realFileName="temp.mp3";
			}
			// 文件上传的存储路�?
			String ctxPath = "E:/project/apache-tomcat-8.0.51/webapps/";
			// 创建文件
			File dirPath = new File(ctxPath);
			/**
			 * 问题描述：系统找不到指定路径 问题解析：mkdir()方法不能创建父目录不存在是的子路�?
			 */
			if (!dirPath.exists()) {
				dirPath.mkdirs();
			}
			// 上传后的文件名称前拼接年月日时分秒微秒来防止重复
			String dateString = (new SimpleDateFormat("yyyyMMddhhmmssSSS_"))
					.format(new Date());

			String real = ctxPath + File.separator + dateString + realFileName;
			// String real =ctxPath + File.separator + dateString;
			File uploadFile = new File(real);
			System.out.println(uploadFile);
			// 保存在数据库中的路径
			// path = properties.getValue(key)+File.separator +dateString
			// +realFileName;
			path = "/vankeAudio"+File.separator + dateString+ realFileName;
			// 复制，上传文�?
			FileCopyUtils.copy(file.getBytes(), uploadFile);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("上传文件出现异常", e);
		}
		return path.replaceAll("\\\\", "/");
	}
	/**
	 * 图片上传
	 * 
	 * @param key
	 *            模块
	 * @param request
	 * @param file
	 *            上传的文�?
	 * @return 返回文件名（包括文件在tomcat服务器上保存的路径）
	 */
	public static String uploadImgFile(HttpServletRequest request,
			MultipartFile file) {
		// 获取上传文件的mime类型
		String fileType = file.getContentType();
		if (fileType.contains("image")) {
			return FileUploadUtils.uploadFile(request, file);
		}
		return "fail";
	}

	/**
	 * excel上传
	 * 
	 * @param key
	 *            模块
	 * @param request
	 * @param file
	 *            上传的文�?
	 * @return 能上传则返回文件名（包括文件在tomcat服务器上保存的路径）,上传
	 */
	public static String uploadExcelFile(String key,
			HttpServletRequest request, MultipartFile file) {
		// 获取上传文件的去路径文件名称
		String fileName = file.getOriginalFilename();
		// xls的mime类型是application/vnd.ms-excel,xlsx的mime类型是application/octet-stream,exe的mime类型是application/octet-stream
		if (fileName.contains(".xls") || fileName.contains(".xlsx")) {
			return FileUploadUtils.uploadFile(request, file);
		}
		return "fail";
	}
}