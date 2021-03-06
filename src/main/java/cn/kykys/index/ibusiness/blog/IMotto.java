package cn.kykys.index.ibusiness.blog;

import cn.kykys.index.model.blog.MottoModel;
import cn.kykys.index.model.page.PageWeb;

import java.util.Map;

/**
 * Created by kuangye on 2016/9/25.
 */
public interface IMotto {

    MottoModel getById(Long id);

    boolean add(MottoModel mottoModel);

    boolean update(MottoModel mottoModel);

    boolean delete(Long id);

    Map<String, ?> selectByPage(MottoModel mottoModel, PageWeb pageWeb);


}
