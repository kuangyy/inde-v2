package cn.kykys.index.utils.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Date;

/**
 * RedisCache
 *
 * @author liwei
 * @date 16/8/4
 * @description
 */
public class RedisCache implements ICache {
    // redis 连接池
    private JedisPool jedisPool;

    /**
     * 禁用无参构造
     */
    private RedisCache(){}

    /**
     * 构造函数
     * @param redisConfig
     */
    public RedisCache(RedisConfig redisConfig) {
        if(redisConfig == null){
            throw new IllegalArgumentException("使用redis时,redisConfig不能为空!");
        }

        JedisPoolConfig config = new JedisPoolConfig();
        // 配置文件
        config.setMaxIdle(redisConfig.getMaxIdle());
        config.setMaxTotal(redisConfig.getMaxTotal());
        config.setTestOnBorrow(redisConfig.isTestOnBorrow());
        config.setTestOnReturn(redisConfig.isTestOnReturn());

        jedisPool = new JedisPool(config, redisConfig.getHost(), redisConfig.getPort(), 3000, String.format("%s:%s",redisConfig.getUserName(),redisConfig.getPassword()));

    }

    /**
     * 设置一个缓存,无过期时间
     *
     * @param key   缓存key
     * @param value 缓存值
     */
    @Override
    public void set(String key, Object value) {
        set(key,value,null);
    }

    /**
     * 设置一个缓存,有固定的过期时间
     *
     * @param key        缓存key
     * @param value      缓存值
     * @param expireTime 过期时间
     */
    @Override
    public void set(String key, Object value, Date expireTime) {
        if(expireTime != null &&  expireTime.before(new Date())){
            throw new IllegalArgumentException("缓存的过期时间不能早于当前时间!");
        }

        int expireSecond = 0;
        if(expireTime != null){
            expireSecond = (int)(expireTime.getTime() - new Date().getTime())/1000;
        }

        set(key,value,expireSecond);
    }

    /**
     * 设置一个缓存,使用浮动的过期时间(秒数)
     *
     * @param key          缓存key
     * @param value        缓存值
     * @param expireSecond 多少秒后过期
     */
    @Override
    public void set(String key, Object value, int expireSecond) {
        if(StringUtils.isEmpty(key)){
            throw new IllegalArgumentException("设置缓存时key不能为空!");
        }

        Jedis jedis = null;

        try {
            String stringValue;
            if(value instanceof String){
                stringValue = value.toString();
            }
            else{
                stringValue = JSON.toJSONString(value);
            }
            jedis = jedisPool.getResource();
            jedis.set(key, stringValue);

            if(expireSecond > 0){
                jedis.expire(key,expireSecond);
            }
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    /**
     * 获取一个缓存项
     *
     * @param key 缓存key
     * @return
     */
    @Override
    public <T> T get(Class<T> t,String key) {
        T returnObject = null;
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String value = jedis.get(key);
            if(value!=null) {
                returnObject = JSON.parseObject(value.getBytes(),t);
            }
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return returnObject;
    }

    /**
     * 获取一个缓存项
     *
     * @param key 缓存key
     * @return
     */
    @Override
    public <T> T get(TypeReference<T> type, String key) {
        T returnObject = null;
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String value = jedis.get(key);
            if(value!=null) {
                returnObject = JSON.parseObject(value,type);
            }
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return returnObject;
    }

    /**
     * 删除一个缓存项
     *
     * @param key
     */
    @Override
    public void remove(String key) {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            jedis.del(key);
        }
        finally {
            if(jedis != null){
                jedis.close();
            }
        }

    }
}
