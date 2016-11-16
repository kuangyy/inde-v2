package cn.kykys.index.data.kykys;

import cn.kykys.index.data.KykysDB;
import cn.kykys.index.model.blog.TagsModel;

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

    List<Map<String,Object>> selectByPostsCount(Map map);


    List<TagsModel> selectByPostId(List<Long> id);

    TagsModel selectByName(String name);
}