package cn.kykys.index.utils.cache;

import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * LocalCache
 *
 * @author liwei
 * @date 16/8/4
 * @description
 *
 * 本地的缓存类,使用HashMap存储缓存数据
 * 注意:目前此类对于过期缓存使用懒清除机制(即过期的缓存项,再次访问时才会清除并释放内存,在没有访问时不会自动清除),使用时请酌情考虑内存问题。
 */
public class LocalCache implements ICache
{
    /**
     * 内部类,用于存放缓存元素及过期时间
     */
    private class CacheObject{
        // 缓存的值
        private Object value;

        //缓存的过期时间
        private Date expireTime;

        public CacheObject(Object value){
            this.value = value;
        }

        public CacheObject(Object value,Date expireTime){
            this.value = value;
            this.expireTime = expireTime;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Date getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(Date expireTime) {
            this.expireTime = expireTime;
        }
    }

    // 用于存放缓存对象
    private static Map<String,CacheObject> cacheMap = new HashMap();

    /**
     * 设置一个缓存
     * @param key       缓存key
     * @param object    缓存项
     * @throws IllegalArgumentException
     */
    protected void set(String key,CacheObject object) throws IllegalArgumentException{
        if(StringUtils.isEmpty(key)){
            throw new IllegalArgumentException("设置缓存时key不能为空!");
        }
        if(object.expireTime != null &&  object.expireTime.before(new Date())){
            throw new IllegalArgumentException("缓存的过期时间不能早于当前时间!");
        }

        cacheMap.put(key, object);
    }

    /**
     * 设置一个缓存,无过期时间
     * @param key       缓存key
     * @param value     缓存值
     */
    @Override
    public void set(String key, Object value) {
        set(key, new CacheObject(value));
    }

    /**
     * 设置一个缓存,有固定的过期时间
     * @param key       缓存key
     * @param value     缓存值
     * @param expireTime    过期时间
     */
    @Override
    public void set(String key, Object value, Date expireTime) {
        set(key,new CacheObject(value,expireTime));
    }

    /**
     * 设置一个缓存,使用浮动的过期时间(秒数)
     * @param key       缓存key
     * @param value     缓存值
     * @param expireSecond  多少秒后过期
     */
    @Override
    public void set(String key, Object value, int expireSecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND,expireSecond);
        set(key,new CacheObject(value,calendar.getTime()));
    }

    /**
     * 获取一个缓存项
     * @param key   缓存key
     * @return
     */
    @Override
    public <T> T get(Class<T> t,String key) {
        return get(key);
    }

    @Override
    public <T> T get(TypeReference<T> type, String key) {
        return get(key);
    }

    private <T> T get(String key){
        T returnObject = null;
        CacheObject cacheObject = cacheMap.get(key);
        if(cacheObject != null){
            returnObject = (T)cacheObject.getValue();

            // 如果有缓存,但是缓存过期,删除
            if(cacheObject.getExpireTime() !=null && cacheObject.getExpireTime().before(new Date())){
                returnObject = null;
                cacheMap.remove(key);
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
        cacheMap.remove(key);
    }
}
