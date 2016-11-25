package cn.kykys.index.utils.cache;

/**
 * RedisConfig
 *
 * @author liwei
 * @date 16/8/4
 * @description
 */
public class RedisConfig extends CacheConfig {
    // 最大空闲连接数
    private int maxIdle;

    // 最大连接数
    private int maxTotal;

    // 当调用borrow Object方法时，是否进行有效性检查
    private boolean testOnBorrow;

    // 当调用return Object方法时，是否进行有效性检查
    private boolean testOnReturn;

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }
}
