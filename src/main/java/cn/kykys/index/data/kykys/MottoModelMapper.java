package cn.kykys.index.data.kykys;

import cn.kykys.index.data.KykysDB;
import cn.kykys.index.model.blog.MottoModel;

import java.util.List;
import java.util.Map;

@KykysDB
public interface MottoModelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MottoModel record);

    int insertSelective(MottoModel record);

    MottoModel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MottoModel record);

    int updateByPrimaryKey(MottoModel record);

    MottoModel selectByRandom();

    List<MottoModel> selectByPage(Map<String,?> map);

    int count(Map<String,?> map);
}