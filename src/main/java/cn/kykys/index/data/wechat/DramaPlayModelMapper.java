package cn.kykys.index.data.wechat;

import cn.kykys.index.data.WechatDB;
import cn.kykys.index.model.wechat.DramaPlayModelKey;

@WechatDB
public interface DramaPlayModelMapper {
    int deleteByPrimaryKey(DramaPlayModelKey key);

    int insert(DramaPlayModelKey record);

    int insertSelective(DramaPlayModelKey record);
}