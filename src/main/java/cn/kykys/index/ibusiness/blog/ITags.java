package cn.kykys.index.ibusiness.blog;

import cn.kykys.index.model.blog.TagsModel;
import cn.kykys.index.model.page.PageWeb;

import java.util.List;
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


    List<TagsModel> selectHotTagByPage(PageWeb pageWeb);

    List<TagsModel> selectHotTagByPage(PageWeb pageWeb, Integer postsCount, boolean needPosts);


    List<TagsModel> getByPostId(Long id);
    List<TagsModel> getByBookId(Long id);

    void addTagAndLinkPost(String name, Long postid);

    void deleteByPostid(Long postid);

    TagsModel getByName(String name);
}
