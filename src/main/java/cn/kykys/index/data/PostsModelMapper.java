package cn.kykys.index.data;

import cn.kykys.index.model.PostsModel;

public interface PostsModelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PostsModel record);

    int insertSelective(PostsModel record);

    PostsModel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PostsModel record);

    int updateByPrimaryKeyWithBLOBs(PostsModel record);

    int updateByPrimaryKey(PostsModel record);
}