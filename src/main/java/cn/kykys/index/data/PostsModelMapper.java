package cn.kykys.index.data;

import cn.kykys.index.model.PostsModel;

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
}