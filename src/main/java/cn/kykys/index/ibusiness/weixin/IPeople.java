package cn.kykys.index.ibusiness.weixin;

import cn.kykys.index.model.wechat.PeopleModel;

/**
 * Created by kuangye on 2016/11/16.
 */
public interface IPeople {

    PeopleModel selectByOpenId(String openId);

    boolean addPeople(PeopleModel peopleModel);

    boolean updatePeople(PeopleModel peopleModel);
}
