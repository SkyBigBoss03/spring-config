package com.jt.manage.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.vo.ItemCatData;
import com.jt.common.vo.ItemCatResult;
import com.jt.manage.mapper.ItemCatMapper;
import com.jt.manage.pojo.Item;
import com.jt.manage.pojo.ItemCat;

import redis.clients.jedis.JedisCluster;
@Service
public class ItemCatServiceImpl implements ItemCatService {
	
	@Autowired
	private ItemCatMapper itemCatMapper;
	
	//@Autowired
	//private RedisService redisService;
	
	//@Autowired
	//private RedisSentinelService redisService;
	
	@Autowired
	private JedisCluster jedisCluster;
	
	private static final Logger logger = Logger.getLogger(ItemCatServiceImpl.class);
	
	@Override
	public List<ItemCat> findItemCatList(Long parentId) {
		List<ItemCat> itemCatList = new ArrayList<ItemCat>();
		ObjectMapper objectMapper = new ObjectMapper();
		
		String ITEM_CAT_KEY = "ITEM_CAT_"+parentId;
		String result = jedisCluster.get(ITEM_CAT_KEY);
		//如果数据为空应该查询数据库
		if(StringUtils.isNotEmpty(result)){
			try {
				//将json串转化为对象
				ItemCat[]  itemCats = objectMapper.readValue(result, ItemCat[].class);
				for (ItemCat itemCat : itemCats) {
					itemCatList.add(itemCat);
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
			return itemCatList;
		}
		try {
			ItemCat itemCat = new ItemCat();
			itemCat.setParentId(parentId);
			itemCatList = itemCatMapper.select(itemCat);
			String jsonString = objectMapper.writeValueAsString(itemCatList);
			//将数据存入缓存中
			jedisCluster.set(ITEM_CAT_KEY,jsonString);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return itemCatList;			
	}
	
	
	//查询全部商品的分类列表信息
	public ItemCatResult findItemCatAll(){
		/**
		 * 查询要求：
		 * 	1.只查询状态正常的数据
		 */
		ItemCat itemCat = new ItemCat();
		itemCat.setStatus(1);		//表示状态正常的数据
		List<ItemCat> itemCatList = itemCatMapper.select(itemCat);
		
		//准备一级菜单数据
		Map<Long,List<ItemCat>> map = new HashMap<Long,List<ItemCat>>();
		
		for (ItemCat itemcat : itemCatList) {			
			if(!map.containsKey(itemcat.getParentId())){
				//表示没有改父级id
				map.put(itemcat.getParentId(), new ArrayList<ItemCat>());
			}
			//表示当前map集合中还有改上级id
			map.get(itemcat.getParentId()).add(itemcat);
		}
		//以上表示循环一次将全部的数据准备子父级关系
		
		//构建三级集合目录
		List<ItemCatData> itemCatDataList1 = new ArrayList();
		
		//循环遍历一级菜单
		for (ItemCat  itemCat1 : map.get(0L)) {
			ItemCatData itemCatData1 = new ItemCatData(); //一级菜单
			itemCatData1.setUrl("/products/"+itemCat1.getId()+".html");
			itemCatData1.setName("<a href='"+itemCatData1.getUrl()+"'>"+itemCat1.getName()+"</a>");
			
			//拼接二级菜单
			List<ItemCatData> itemCatDataList2 = new ArrayList<ItemCatData>();
			for(ItemCat itemCat2 : map.get(itemCat1.getId())){
				//拼接2级菜单
				ItemCatData itemCatData2 = new ItemCatData();
				itemCatData2.setUrl("/products/"+itemCat2.getId()+".html");
				itemCatData2.setName(itemCat2.getName());
				
				//拼接三级菜单
				List<String> stringList3 = new ArrayList<String>();
				for (ItemCat itemCat3 : map.get(itemCat2.getId())) {
					stringList3.add("/products/"+itemCat3.getId()+".html|"+itemCat3.getName());
				}
				
				itemCatData2.setItems(stringList3);	
				itemCatDataList2.add(itemCatData2);
			}
			
			itemCatData1.setItems(itemCatDataList2);
			itemCatDataList1.add(itemCatData1);
			
			//如果数据大于14条则跳出循环
			if(itemCatDataList1.size() >=14){
				break;
			}
		}
		
		ItemCatResult itemCatResult = new ItemCatResult();
		itemCatResult.setItemCats(itemCatDataList1);
		
		return itemCatResult;		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
