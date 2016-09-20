package cn.kykys.index.annotations;

import java.lang.annotation.*;

/**
 * Created by kuangye on 2016/7/18.
 * 是否需要登录
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface MustLogin {
}
