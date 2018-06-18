package com.jt.manage.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.RedisService;
import com.jt.common.util.RedisCluster;
import com.jt.manage.mapper.ItemCatMapper;
import com.jt.manage.pojo.ItemCat;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

@Service
public class ItemCatServiceImpl implements ItemCatService {
	
	@Autowired
	private ItemCatMapper itemCatMapper;
	
	@Autowired
	private JedisCluster jedisCluster;
	
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * redis缓存的实现思路
	 * redis应该存储分类的JSON串信息,并且以parentId为key
	 * 1.获取数据时,应该先从缓存中获取数据
	 * 2.如果缓存中能够获取数据,则直接返回
	 * 3.如果缓存中没有数据,则查询数据库获取数据,并且将数据存入缓存中,方便下次获取
	 * 
	 */
	@Override
	public List<ItemCat> findItemCatList(Long parentId) {
		
		List<ItemCat> itemCatList = new ArrayList<ItemCat>();
		
		//1.定义reids的key值  应该见名知意
		String key = "ITEM_CAT_"+parentId;
		
		//2.先从缓存中获取数据
		String result = jedisCluster.get(key);
		
		try {
			//3.判断是否值为空
			if(StringUtils.isEmpty(result)){
				//4.如果数据为空则查询数据库
				ItemCat itemCat = new ItemCat();
				itemCat.setParentId(parentId);
				itemCatList = itemCatMapper.select(itemCat);
				
				//5.将数据存入redis缓存中
				String dataJSON = objectMapper.writeValueAsString(itemCatList);
				jedisCluster.set(key, dataJSON);
				return itemCatList;
			}else {
				//6.表述数据不为空,需要将JSON串转化为List集合
				ItemCat[] itemCats = objectMapper.readValue(result, ItemCat[].class);
				for (ItemCat itemCat : itemCats) {
					itemCatList.add(itemCat);
				}
				return itemCatList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return itemCatList;
	}
}