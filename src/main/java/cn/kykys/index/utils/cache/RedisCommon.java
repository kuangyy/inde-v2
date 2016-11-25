package cn.kykys.index.utils.cache;

public class RedisCommon {
	private static RedisCache redisCache = null;
	private static LocalCache localCache = null;
	private static Boolean cacheType;
	private static Integer maxIdle;
	private static Integer maxTotal;
	private static Boolean testOnBorrow;
	private static Boolean testOnReturn;
	private static String host;
	private static Integer port;
	private static String userName;
	private static String password;
	
	public static Boolean getCacheType() {
		return cacheType;
	}
	public static void setCacheType(Boolean cacheType) {
		RedisCommon.cacheType = cacheType;
	}
	public static Boolean getTestOnBorrow() {
		return testOnBorrow;
	}
	public static void setTestOnBorrow(Boolean testOnBorrow) {
		RedisCommon.testOnBorrow = testOnBorrow;
	}
	public static Boolean getTestOnReturn() {
		return testOnReturn;
	}
	public static void setTestOnReturn(Boolean testOnReturn) {
		RedisCommon.testOnReturn = testOnReturn;
	}
	public static RedisCache getRedisCache() {
		produce();
		return redisCache;
	}
	public static LocalCache getLocalCache() {
		produce();
		return localCache;
	}
	public static ICache getCache() {
		ICache cache;
		if (RedisCommon.getCacheType()) {
			cache = RedisCommon.getRedisCache();
		} else {
			cache = RedisCommon.getLocalCache();
		}
		return cache;
	}
	public static Integer getMaxIdle() {
		return maxIdle;
	}
	public static void setMaxIdle(Integer maxIdle) {
		RedisCommon.maxIdle = maxIdle;
	}
	public static Integer getMaxTotal() {
		return maxTotal;
	}
	public static void setMaxTotal(Integer maxTotal) {
		RedisCommon.maxTotal = maxTotal;
	}
	public static String getHost() {
		return host;
	}
	public static void setHost(String host) {
		RedisCommon.host = host;
	}
	public static Integer getPort() {
		return port;
	}
	public static void setPort(Integer port) {
		RedisCommon.port = port;
	}
	public static String getUserName() {
		return userName;
	}
	public static void setUserName(String userName) {
		RedisCommon.userName = userName;
	}
	public static String getPassword() {
		return password;
	}
	public static void setPassword(String password) {
		RedisCommon.password = password;
	}
	private static void produce(){
		if(cacheType){
			//生产
			if(redisCache==null){
				RedisConfig config = new RedisConfig();
				config.setMaxIdle(maxIdle);
				config.setMaxTotal(maxTotal);
				config.setTestOnBorrow(testOnBorrow);
				config.setTestOnReturn(testOnReturn);
				config.setHost(host);
				config.setPort(port);
				config.setUserName(userName);
				config.setPassword(password);
				redisCache = new RedisCache(config);
			}
		}else{
			//本地
			if(localCache==null){
				localCache = new LocalCache();
			}
		}
	}
}
