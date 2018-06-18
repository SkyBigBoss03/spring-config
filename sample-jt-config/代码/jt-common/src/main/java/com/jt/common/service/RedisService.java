package com.jt.common.service;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

//@Service
public class RedisService {
	
	//@Autowired
	private StringRedisTemplate redisTemplate;
	
	//获取数据
	public String get(String key){
		ValueOperations<String, String>  operations =redisTemplate.opsForValue();
		return operations.get(key);
	}
	
	//插入数据	
	public boolean set(String key,String value){
		ValueOperations<String, String> operations = redisTemplate.opsForValue();
		
		try {
			operations.set(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//插入缓存定义超时时间为秒
	public boolean set(String key,String value,Long expireTime){
		try {
			ValueOperations<String, String> operations = redisTemplate.opsForValue();
			operations.set(key, value, expireTime, TimeUnit.DAYS);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
