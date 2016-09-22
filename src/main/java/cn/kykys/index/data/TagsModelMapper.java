package cn.kykys.index.data;

import cn.kykys.index.model.TagsModel;

@KykysDB
public interface TagsModelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TagsModel record);

    int insertSelective(TagsModel record);

    TagsModel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TagsModel record);

    int updateByPrimaryKey(TagsModel record);
}