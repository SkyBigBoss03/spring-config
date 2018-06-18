package jedis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class TestJedis {
	
	@Test
	public void sentinel(){
    	Set<String> sentinels = new HashSet<String>();
    	sentinels.add(new HostAndPort("192.168.163.200",26379).toString());
    	sentinels.add(new HostAndPort("192.168.163.200",26380).toString());
    	
    	//mymaster是在sentinel.conf中配置的名称
    	//sentinel monitor mymaster 192.168.163.200 6380 1
    	JedisSentinelPool pool = new JedisSentinelPool("mymaster", sentinels);
    	System.out.println("当前master：" + pool.getCurrentHostMaster());
    	
    	Jedis jedis = pool.getResource();
		//jedis.auth("123456");

    	System.out.println(jedis.get("num"));
    	pool.returnResource(jedis);   
    	
    	pool.destroy();
    	System.out.println("ok");
	}
}
