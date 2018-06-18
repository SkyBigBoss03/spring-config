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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HttpClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientService.class);

    @Autowired(required=false)
    private CloseableHttpClient httpClient; //对象

    @Autowired(required=false)
    private RequestConfig requestConfig;  //定义连接的参数
    
    
    /**
     * 编辑HttpClient中的get请求
     * 1.考虑是否含有参数/考虑字符集编码问题
     * url:localhost:8091/addUser?id=1&name=tom
     * 2.参数如何传递
     * 	 面对不同的用户 都需要传递参数,使用Map数据结构
     * 
     */
    public String doGet(String uri,Map<String,String> params,
    		String charset){
    	
    	String result = null;
    	
    	//1.判断是否含有参数
    	try {
  
    		if(params !=null){
        		URIBuilder builder = new URIBuilder(uri);
        		for (Map.Entry<String,String> entry: params.entrySet()) {
        			builder.addParameter(entry.getKey(), entry.getValue());
				}
        		//uri= localhost:8091/add?id=1&name=tom
        		uri = builder.build().toString();
        		System.out.println("获取请求参数:"+uri);
        	}
    		//2.判断字符集是否为null
    		if(charset ==null){
    			charset = "UTF-8";
    		}
    		
    		//3.创建请求对象
    		HttpGet httpGet = new HttpGet(uri);
    		httpGet.setConfig(requestConfig); //设定请求的参数
    		
    		//4.发起请求
    		CloseableHttpResponse response = httpClient.execute(httpGet);
    		
    		if(response.getStatusLine().getStatusCode() == 200){
    			
    			result = 
    	EntityUtils.toString(response.getEntity(),charset);
    		}
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return result;
    }
    
    public String doGet(String uri){
    	
    	return doGet(uri, null, null);
    }
    
    public String doGet(String uri,Map<String,String> params){
    	
    	return doGet(uri, params, null);
    }
    
    
    /*实现httpClient中的post方法
     * 知识回顾 :
     * 	1.get localhost:8091/add?id=1&name=tom
     *  2.post localhost:8091/add
     *  	流处理方式(id=1,name=tom)
     *  		
     */
    public String doPost(String uri,Map<String,String> params,String charset){
    	
    	//1.创建提交方式
    	HttpPost httpPost = new HttpPost(uri);
    	
    	//2.设定请求的参数
    	httpPost.setConfig(requestConfig);
    	
    	//3.设定字符集
    	if(charset == null){
    		charset = "UTF-8";
    	}
    	
    	//4.为post提交准备参数
    	if(params !=null){
    		
    		//4.1 创建一个提交的List集合对象
    		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    		
    		//4.2为list集合赋值
    		for (Map.Entry<String,String> entry : params.entrySet()) {
				
    			parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
    		try {	
    		//4.3创建提交对象
    		UrlEncodedFormEntity formEntity = 
    				new UrlEncodedFormEntity(parameters, charset);
    		
    		//4.4将提交的实体添加到post请求中
    		httpPost.setEntity(formEntity);
  
    		} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	
    	//5.发起http请求
    	String result = null;
    	try {
    		CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
    		
    		if(httpResponse.getStatusLine().getStatusCode() == 200){
    			
    			result = EntityUtils.toString(httpResponse.getEntity(),charset);
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return  result;
    }
    
    public String doPost(String uri){
    	
    	return doPost(uri,null,null);
    }
    
    public String doPost(String uri,Map<String,String> params){
    	
    	return doPost(uri, params,null);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
