package com.jt.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Service
public class RedisService {
	
	//表示调用方法时,才动态注入对象
	@Autowired(required=false)
	//private ShardedJedisPool shardedJedisPool;
	private JedisSentinelPool sentinelPool;
	
	public void set(String key,String value){
		Jedis jedis = sentinelPool.getResource();
		jedis.set(key, value);
		sentinelPool.returnResource(jedis);		
	}
	
	public String get(String key){
		Jedis jedis = sentinelPool.getResource();
		String value = jedis.get(key);
		sentinelPool.returnResource(jedis);
		return value;
	}
}
