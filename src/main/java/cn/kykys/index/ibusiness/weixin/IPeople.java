package cn.kykys.index.ibusiness.weixin;

import cn.kykys.index.model.wechat.DramaPlayModelKey;
import cn.kykys.index.model.wechat.PeopleModel;

import java.util.List;

/**
 * Created by kuangye on 2016/11/16.
 */
public interface IPeople {

    PeopleModel selectByOpenId(String openId);

    boolean addPeople(PeopleModel peopleModel);

    boolean updatePeople(PeopleModel peopleModel);


    boolean nameExist(String name);


    List<DramaPlayModelKey> getInPlayDramaByPeopleId(Integer id);

    DramaPlayModelKey getPlayDrama(Integer id,Integer dramaId);

    boolean updateInDramaStatus(DramaPlayModelKey dramaPlayModelKey);
}
