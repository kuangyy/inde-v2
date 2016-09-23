package cn.kykys.index.data;

import cn.kykys.index.model.TagsModel;

import java.util.List;
import java.util.Map;

@KykysDB
public interface TagsModelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TagsModel record);

    int insertSelective(TagsModel record);

    TagsModel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TagsModel record);

    int updateByPrimaryKey(TagsModel record);

    List<TagsModel> selectByPage(Map<String,?> map);

    int count(Map<String,?> map);

}