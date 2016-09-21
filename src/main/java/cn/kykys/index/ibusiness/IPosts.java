package cn.kykys.index.ibusiness;

import cn.kykys.index.model.PostsModel;
import cn.kykys.index.model.page.PageWeb;

import java.util.List;
import java.util.Map;

/**
 * Created by kuangye on 2016/9/21.
 */
public interface IPosts {

    PostsModel getById(Long id);

    boolean add(PostsModel postsModel);

    boolean update(PostsModel postsModel);

    boolean delete(Long id);

    Map<String, ?> selectByPage(PostsModel postsModel, PageWeb pageWeb);
}
