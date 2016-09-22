package cn.kykys.index.data;

import cn.kykys.index.model.RPostTagModelKey;

@KykysDB
public interface RPostTagModelMapper {
    int deleteByPrimaryKey(RPostTagModelKey key);

    int insert(RPostTagModelKey record);

    int insertSelective(RPostTagModelKey record);
}