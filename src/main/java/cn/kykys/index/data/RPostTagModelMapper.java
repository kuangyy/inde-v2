package cn.kykys.index.data;

import cn.kykys.index.model.RPostTagModelKey;

import java.util.List;
import java.util.Map;

@KykysDB
public interface RPostTagModelMapper {
    int deleteByPrimaryKey(RPostTagModelKey key);

    int insert(RPostTagModelKey record);

    int insertSelective(RPostTagModelKey record);

    List<Long> selectTagidByPostid(Long id);

    int deleteByPostid(Long id);

}