package cn.kykys.index.ibusiness;

import cn.kykys.index.model.TagsModel;
import cn.kykys.index.model.page.PageWeb;

import java.util.Map;

/**
 * Created by kuangye on 2016/9/21.
 */
public interface ITags {

    TagsModel getById(Long id);


    boolean add(TagsModel tagsModel);

    boolean update(TagsModel tagsModel);

    boolean delete(Long id);

    Map<String, ?> selectByPage(TagsModel postsModel, PageWeb pageWeb);

    Map<String, ?> searchByPage(String word, PageWeb pageWeb);
}
