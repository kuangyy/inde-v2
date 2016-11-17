package cn.kykys.index.data.wechat;

import cn.kykys.index.data.WechatDB;
import cn.kykys.index.model.wechat.PeopleLoginModel;

@WechatDB
public interface PeopleLoginModelMapper {
    int insert(PeopleLoginModel record);

    int insertSelective(PeopleLoginModel record);
}