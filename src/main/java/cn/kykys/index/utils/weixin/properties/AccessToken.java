package cn.kykys.index.utils.weixin.properties;

/**
 * Created by kuangye on 2016/11/14.
 */
public class AccessToken {


    private String token;

    private Integer expiresIn;

    public AccessToken() {
    }

    public AccessToken(String token, Integer expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }
}
