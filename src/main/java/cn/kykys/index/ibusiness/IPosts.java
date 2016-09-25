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

    PostsModel getByIdAddViewCount(Long id);

    boolean add(PostsModel postsModel);

    boolean update(PostsModel postsModel);

    boolean add(PostsModel postsModel, String tag);

    boolean update(PostsModel postsModel, String tag);

    boolean delete(Long id);

    Map<String, ?> selectByPage(PostsModel postsModel, PageWeb pageWeb);

    Map<String, ?> searchByPage(String word, PageWeb pageWeb);


    List<PostsModel> selectByTag(Long tagId, PageWeb pageWeb);

    Map<String, ?> selectByTagWithPage(Long tagId, PageWeb pageWeb);

}
