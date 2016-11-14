package cn.kykys.index.utils.weixin.properties;

/**
 * Created by kuangye on 2016/11/14.
 */
public class AccessToken {


    private String token;

    private Long expiresIn;

    public AccessToken() {
    }

    public AccessToken(String token, Long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
