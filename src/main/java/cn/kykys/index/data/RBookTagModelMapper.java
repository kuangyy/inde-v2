package cn.kykys.index.data;

import cn.kykys.index.model.RBookTagModelKey;

import java.util.List;

@KykysDB
public interface RBookTagModelMapper {
    int deleteByPrimaryKey(RBookTagModelKey key);

    int insert(RBookTagModelKey record);

    int insertSelective(RBookTagModelKey record);


    List<Long> selectTagidByBookid(Long id);
}