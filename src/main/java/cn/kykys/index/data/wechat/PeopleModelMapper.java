package cn.kykys.index.data.wechat;

import cn.kykys.index.data.WechatDB;
import cn.kykys.index.model.wechat.PeopleModel;

@WechatDB
public interface PeopleModelMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PeopleModel record);

    int insertSelective(PeopleModel record);

    PeopleModel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PeopleModel record);

    int updateByPrimaryKey(PeopleModel record);




    PeopleModel selectByOpenId(String openId);
}