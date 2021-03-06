package cn.kykys.index.data.kykys;

import cn.kykys.index.data.KykysDB;
import cn.kykys.index.model.blog.PostsModel;

import java.util.List;
import java.util.Map;

@KykysDB
public interface PostsModelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PostsModel record);

    int insertSelective(PostsModel record);

    PostsModel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PostsModel record);

    int updateByPrimaryKeyWithBLOBs(PostsModel record);

    int updateByPrimaryKey(PostsModel record);

    List<PostsModel> selectByPage(Map<String,?> map);

    int count(Map<String,?> map);


    List<PostsModel> searchByPage(Map<String,?> map);

    int searchCount(Map<String,?> map);


    List<PostsModel> selectByTag(Map map);

    int selectByTagCount(Map map);
}