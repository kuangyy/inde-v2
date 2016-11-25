package cn.kykys.index.utils.cache;

/**
 * CacheConfig
 *
 * @author liwei
 * @date 16/8/4
 * @description
 */
public class CacheConfig {
    // redis 服务地址
    private String host;

    // redis 服务端口
    private int port;

    // 用户名
    private String userName;

    // 密码
    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
