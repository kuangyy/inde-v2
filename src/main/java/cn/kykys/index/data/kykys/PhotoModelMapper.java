package cn.kykys.index.data.kykys;

import cn.kykys.index.data.KykysDB;
import cn.kykys.index.model.blog.PhotoModel;

import java.util.List;
import java.util.Map;

@KykysDB
public interface PhotoModelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PhotoModel record);

    int insertSelective(PhotoModel record);

    PhotoModel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PhotoModel record);

    int updateByPrimaryKey(PhotoModel record);



    List<PhotoModel> searchByPage(Map<String,?> map);

    int searchCount(Map<String,?> map);


}