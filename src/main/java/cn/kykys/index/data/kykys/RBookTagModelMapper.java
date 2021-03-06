package cn.kykys.index.data.kykys;

import cn.kykys.index.data.KykysDB;
import cn.kykys.index.model.blog.RBookTagModelKey;

import java.util.List;

@KykysDB
public interface RBookTagModelMapper {
    int deleteByPrimaryKey(RBookTagModelKey key);

    int insert(RBookTagModelKey record);

    int insertSelective(RBookTagModelKey record);


    List<Long> selectTagidByBookid(Long id);
}