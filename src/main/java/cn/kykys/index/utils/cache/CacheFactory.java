package cn.kykys.index.utils.cache;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * CacheFactory
 *
 * @author liwei
 * @date 16/8/5
 * @description
 */
public class CacheFactory {
    // 本地缓存
    private static ICache localCache = new LocalCache();

    // Redis缓存
    private static Map<Integer,ICache> redisCacheMap = new HashMap<>();

    /**
     * 获取一个本地缓存操作实例
     * @return
     */
    public static ICache getLocalCache(){
        return localCache;
    }

    /**
     * 获取一个Redis缓存操作实例, 在内网使用时, config请传入null或config.getHost置为空, 这时会返回一个本地Cache的操作接口
     * @param config
     * @return
     */
    public static ICache getRedisCache(RedisConfig config){
        // 没有config或者config中的Host为空时,返回一个本地缓存的操作接口
        if(config == null || StringUtils.isEmpty(config.getHost())){
            return localCache;
        }
        else {
            if (redisCacheMap.get(config.hashCode()) == null) {
                synchronized (redisCacheMap) {
                    if (redisCacheMap.get(config.hashCode()) == null) {
                        redisCacheMap.put(config.hashCode(), new RedisCache(config));
                    }
                }
            }

            return redisCacheMap.get(config.hashCode());
        }
    }
}
