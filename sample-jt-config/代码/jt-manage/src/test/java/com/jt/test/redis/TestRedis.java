package com.jt.test.redis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.data.redis.connection.NamedNode;
import org.springframework.data.redis.connection.PoolConfig;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class TestRedis {
	
	//@Test
	public void test01(){
		Jedis jedis = new Jedis("192.168.126.135", 6379);
		jedis.set("tom", "我是一只小猫咪");
		String value = jedis.get("tom");
		System.out.println(value);
		jedis.close();
	}
	
	//测试redis分片
	//@Test
	public void test02(){
		List<JedisShardInfo> shardList= new ArrayList<JedisShardInfo>();
		shardList.add(new JedisShardInfo("192.168.126.135", 6379));
		shardList.add(new JedisShardInfo("192.168.126.135", 6380));
		shardList.add(new JedisShardInfo("192.168.126.135", 6381));
		
		//定义jedis配置文件
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		ShardedJedisPool jedisPool = new ShardedJedisPool(poolConfig, shardList);
		
		//从分片池中获取数据
		ShardedJedis jedis = jedisPool.getResource();
		
		jedis.set("tom", "tom1");
		jedis.close();
	}
	
	//测试单台哨兵
	//@Test
	public void test03(){
		Set<String> sentinels = new HashSet<String>();
		sentinels.add(new HostAndPort("192.168.126.135", 26379).toString());
		sentinels.add(new HostAndPort("192.168.126.135", 26380).toString());
		sentinels.add(new HostAndPort("192.168.126.135", 26381).toString());
		JedisSentinelPool pool = new JedisSentinelPool("mymaster", sentinels);
		
		//从池中获取哨兵对象
		Jedis jedis = pool.getResource();
		jedis.set("name", "tom");
		String name = jedis.get("name");
		System.out.println("操作成功:"+name);
		
	}	
}
