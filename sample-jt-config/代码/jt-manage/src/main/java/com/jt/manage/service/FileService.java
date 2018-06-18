package com.jt.manage.service;

import org.springframework.web.multipart.MultipartFile;

import com.jt.common.vo.PicUploadResult;

public interface FileService {
	//定义文件上传接口
	public PicUploadResult fileUpload(MultipartFile uploadFile);
}
