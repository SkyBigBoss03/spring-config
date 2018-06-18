package com.jt.manage.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jt.common.vo.PicUploadResult;
@Service
public class FileServiceImpl implements FileService {
	
	@Value("${image.local}")
	private String localDir;
	
	@Value("${image.path}")
	private String urlPath;
	
	@Override
	public PicUploadResult fileUpload(MultipartFile uploadFile) {
		
		PicUploadResult result = new PicUploadResult();
		//获取文件全名  1.jpg
		String fileName = uploadFile.getOriginalFilename();
		
		//获取图片类型   .jpg  
		String type = fileName.substring(fileName.lastIndexOf("."));
		
		//判断是否为正确的图片类型
		if(!type.matches("^.*(jpg|png|gif)$")){
			//表明图片的格式不正确
			result.setError(1);
			return result;
		}
		
		try {
			//判断是否为恶意文件
			BufferedImage image = ImageIO.read(uploadFile.getInputStream());
			int height = image.getHeight();
			int width = image.getWidth();
			
			//判断是否为一个正确的图片
			if(height ==0 || width ==0){
				//表示不是一张图片
				result.setError(1);
				return result;
			}
			
			/**
			 * 1.如果程序执行到这里,表示上传的图片是一个正确的格式 则进行如果操作
			 * 2.准备磁盘路径和访问路径
			 * 3.url:image.jt.com/yyyy/MM/dd/HH/mm/3位随即数+文件名
			 * 4.
			 */
			result.setHeight(height+"");
			result.setWidth(width+"");
			
			//String localDir = "E:/jt-upload/";
			//String urlPath = "http://image.jt.com/";
			
			//定义时间格式并且转化为yyyy/MM/dd
			String datePath = new SimpleDateFormat("yyyy/MM/dd/HH").format(new Date());
			
			//生成三位随机数				0-899 + 100 =100-999
			int randomNum = new Random().nextInt(900) + 100;
			
			//形成url路径 image.jt.com/yyyy/MM/dd/HH/123456.jpg
			String imageUrl = urlPath +datePath + "/"+randomNum + fileName;
			result.setUrl(imageUrl);
			
			//形成本地磁盘路径
			String localPath = localDir + datePath;
			
			//生成文件夹目录
			File file = new File(localPath);
			
			//判断文件是否存在,如果不存在需要创建文件夹
			if(!file.exists()){
				
				file.mkdirs();
			}
			
			//如果文件夹存在则执行些写盘操作
			uploadFile.transferTo(new File(localPath+"/" + randomNum+fileName));
		} catch (Exception e) {
			e.printStackTrace();
			//表示文件上传失败
			result.setError(1);
			return result;
		}	
		return result;
	}

}
