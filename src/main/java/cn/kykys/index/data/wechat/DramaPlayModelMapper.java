package cn.kykys.index.data.wechat;

import cn.kykys.index.data.WechatDB;
import cn.kykys.index.model.wechat.DramaPlayModelKey;

import java.util.List;
import java.util.Map;

@WechatDB
public interface DramaPlayModelMapper {
    int deleteByPrimaryKey(DramaPlayModelKey key);

    int insert(DramaPlayModelKey record);

    int insertSelective(DramaPlayModelKey record);


    List<DramaPlayModelKey> select(Map map);

    int updateStatus(DramaPlayModelKey dramaPlayModelKey);
}