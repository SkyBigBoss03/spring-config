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

@Service
public class RedisService {
	
	@Autowired
	private JedisSentinelPool jedisSentinelPool;
	
	public void set(String key,String value){
		Jedis jedis = jedisSentinelPool.getResource();
		jedis.set(key, value);
		jedis.close();
	}
	
	public String get(String key){
		Jedis jedis = jedisSentinelPool.getResource();
		
		String value = jedis.get(key);
		jedis.close();
		return value;
	}
	
}
