package cn.kykys.index.business.weixin;

import cn.kykys.index.data.wechat.PeopleModelMapper;
import cn.kykys.index.ibusiness.weixin.IPeople;
import cn.kykys.index.model.wechat.PeopleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Created by kuangye on 2016/11/16.
 */
@Service
public class PeopleBusiness implements IPeople {


    @Autowired
    PeopleModelMapper peopleModelMapper;

    public PeopleModel selectByOpenId(String openId) {
        if (StringUtils.hasText(openId)) {
            return peopleModelMapper.selectByOpenId(openId);
        }
        return null;
    }


    public boolean addPeople(PeopleModel peopleModel) {
        if (peopleModel != null) {
            return peopleModelMapper.insertSelective(peopleModel) > 0;
        }
        return false;
    }

    public boolean updatePeople(PeopleModel peopleModel) {
        if (peopleModel != null) {
            return peopleModelMapper.updateByPrimaryKeySelective(peopleModel) > 0;
        }
        return false;
    }


    public boolean nameExist(String name) {
        if (StringUtils.hasText(name)) {
            return peopleModelMapper.selectByName(name) > 0;
        }
        return true;
    }
}
