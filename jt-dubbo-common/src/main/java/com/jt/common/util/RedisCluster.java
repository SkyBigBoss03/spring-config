package com.jt.common.util;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class RedisCluster implements FactoryBean<JedisCluster>,InitializingBean,BeanNameAware,BeanFactoryAware{

	private Resource addressConfig;
	private String addressKeyPrefix;
	private JedisCluster jedisCluster;
	private Integer timeout;
	private Integer maxRedirections;
	private GenericObjectPoolConfig poolConfig;
	private Pattern p = Pattern.compile("^.+[:]\\d{1,5}\\s*$");
	
	public void setAddressConfig(Resource addressConfig) {
		this.addressConfig = addressConfig;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setMaxRedirections(int maxRedirections) {
		this.maxRedirections = maxRedirections;
	}

	public void setAddressKeyPrefix(String addressKeyPrefix) {
		this.addressKeyPrefix = addressKeyPrefix;
	}

	public void setGenericObjectPoolConfig(GenericObjectPoolConfig poolConfig) {
		this.poolConfig = poolConfig;
	}

	//通过工厂模式创建出来的对象
	public JedisCluster getObject() throws Exception {
		//System.out.println("通过工厂模式获取JedisCluster对象");
		return jedisCluster;
	}

	public Class<? extends JedisCluster> getObjectType() {
		return (this.jedisCluster != null ? this.jedisCluster.getClass() : JedisCluster.class);
	}

	public boolean isSingleton() {
		return true;
	}

	private Set<HostAndPort> parseHostAndPort() {
		try {
			Properties prop = new Properties();
			prop.load(this.addressConfig.getInputStream());

			Set<HostAndPort> haps = new HashSet<HostAndPort>();
			for (Object key : prop.keySet()) {
					
				if (!((String) key).startsWith(addressKeyPrefix)) {
					continue;
				}

				String val = (String) prop.get(key);

				boolean isIpPort = p.matcher(val).matches();

				if (!isIpPort) {
					throw new IllegalArgumentException("ip 或 port 不合法");
				}
				String[] ipAndPort = val.split(":");
				HostAndPort hap = new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
				haps.add(hap);
			}
			return haps;
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	public void afterPropertiesSet() throws Exception {
		//System.out.println("准备为jedisCluster赋值");
		//获取redis集群的全部信息
		Set<HostAndPort> haps = this.parseHostAndPort();
		jedisCluster = new JedisCluster(haps, timeout, maxRedirections, poolConfig);
	}

	@Override
	public void setBeanName(String name) {
		//System.out.println("通过spring获取bean的Id:"+name);
		
	}
	
	//代表整个Spring容器对象,内部可以获取任意的对象
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		
		//beanFactory.getBean(requiredType)
		//System.out.println("获取spring的工厂对象:"+beanFactory);
	}
}
