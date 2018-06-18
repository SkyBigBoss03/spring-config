package com.jt.manage.controller.web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.vo.ItemCatResult;
import com.jt.manage.service.ItemCatService;

@Controller
public class WebItemCatController {
	
	@Autowired(required=false)
	private ItemCatService itemCatService;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	//查询商品分配目录
	@RequestMapping("/web/itemcat/all")
	public void findItemCatAll(String callback,HttpServletResponse response){
		
		//拼接返回后的数据格式：category.getDataService(JSON);
		//获取三级分类目录
		ItemCatResult itemCatResult = itemCatService.findItemCatAll();
		try {
			String JSON = objectMapper.writeValueAsString(itemCatResult); //将数据转化为JSON串
			String callbackJSON = callback +"("+JSON+")";                 //拼接请求回调参数
			response.setContentType("text/html;charset=utf-8");			  //设定字符集
			response.getWriter().write(callbackJSON);                     //回显参数
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
