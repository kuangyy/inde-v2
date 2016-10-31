package cn.kykys.index.utils.data;

import java.util.Collection;

/**
 * Created by kuangye on 2016/10/26.
 */
public class DataUtils {


    public static <T extends Collection> T intersection(T a, T b) {
        a.retainAll(b);
        return a;
    }


}
