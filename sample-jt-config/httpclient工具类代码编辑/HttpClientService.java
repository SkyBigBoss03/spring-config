package com.jt.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HttpClientService {
	
	@Autowired(required=false)
	private CloseableHttpClient httpClient;   //定义httpClient
	
	@Autowired(required=false)
	private RequestConfig requestConfig;	//设定请求参数
	
	//定义多参数调用
	public String doGet(String url,Map<String,String> params,String encode) throws Exception{
		
		//判断参数是否为空
		if(params !=null){
			URIBuilder builder = new URIBuilder(url);	//将url交给URIBuilder追加数据
			for (Map.Entry<String, String> entry: params.entrySet()) {
				
				builder.setParameter(entry.getKey(), entry.getValue());
			}
			//?lyj_name=%E5%88%98%E6%98%B1%E6%B1%9F&lyj_id=100&lyj_age=500
			url = builder.build().toString();		//拼接参数
		}
		//定义默认的字符集
		if(null==encode){
			encode = "UTF-8";
		}
		
		System.out.println(url);
		//创建get请求
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(requestConfig);
		
		//获取响应对象
		CloseableHttpResponse httpResponse = null;
		try {
			//获取请求数据
			httpResponse = httpClient.execute(httpGet);
			
			if(httpResponse.getStatusLine().getStatusCode() == 200){	
				//表示请求成功
				String result = EntityUtils.toString(httpResponse.getEntity(),encode);
				return result;
			}	
			//将数据源关闭
			httpResponse.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//定义没有参数的调用 默认的编码格式为utf-8
	public String doGet(String url) throws Exception{
		
		return doGet(url,null,null);
	}
	
	public String doGet(String url,String encode) throws Exception{
		return doGet(url, null, encode);
	}
	
	
	//定义POST请求
	public String doPost(String url,Map<String,String> params,String encode) throws Exception{
		
		//构建HTTPPost对象
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(requestConfig);//定义配置文件
		
		//判断是否含有参数
		if(null != params){
			//定义POST提交参数
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();
			
			for (Map.Entry<String,String> entry: params.entrySet()) {
				paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			
			if(null == encode){
				encode = "UTF-8";
			}
			//构建表单提交实体
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramList, encode);
			httpPost.setEntity(formEntity);	//设定数据提交的参数
		}
		
		//发出POST请求
		CloseableHttpResponse response = null;	//定义响应对象
		try {
			response = httpClient.execute(httpPost);
			
			if(response.getStatusLine().getStatusCode() == 200){
				//表示响应成功
				String result = EntityUtils.toString(response.getEntity());
				return result;
			}
			//执行完成后将链接释放
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String doPost(String url,String encode) throws Exception{
		
		return doPost(url, null, encode);
	}
	
	public String doPost(String url) throws Exception{
		
		return doPost(url, null, null);
	}
}
