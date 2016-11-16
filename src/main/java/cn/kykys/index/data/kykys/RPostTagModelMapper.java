package cn.kykys.index.data.kykys;

import cn.kykys.index.data.KykysDB;
import cn.kykys.index.model.blog.RPostTagModelKey;

import java.util.List;

@KykysDB
public interface RPostTagModelMapper {
    int deleteByPrimaryKey(RPostTagModelKey key);

    int insert(RPostTagModelKey record);

    int insertSelective(RPostTagModelKey record);

    List<Long> selectTagidByPostid(Long id);

    int deleteByPostid(Long id);

}