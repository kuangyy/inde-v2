package cn.kykys.index.business.weixin;

import cn.kykys.index.data.wechat.DramaPlayModelMapper;
import cn.kykys.index.data.wechat.PeopleModelMapper;
import cn.kykys.index.ibusiness.weixin.IPeople;
import cn.kykys.index.model.enumeration.DramaPeopleStatusEnum;
import cn.kykys.index.model.wechat.DramaModel;
import cn.kykys.index.model.wechat.DramaPlayModelKey;
import cn.kykys.index.model.wechat.PeopleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kuangye on 2016/11/16.
 */
@Service
public class PeopleBusiness implements IPeople {


    @Autowired
    PeopleModelMapper peopleModelMapper;
    @Autowired
    DramaPlayModelMapper dramaPlayModelMapper;

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


    public List<DramaPlayModelKey> getInPlayDramaByPeopleId(Integer id) {

        Map<String, Object> map = new HashMap<>();
        map.put("peopleId", id);
        map.put("status", DramaPeopleStatusEnum.IN.getStatus());
        return dramaPlayModelMapper.select(map);
    }


    public DramaPlayModelKey getPlayDrama(Integer id, Integer dramaId) {
        Map<String, Object> map = new HashMap<>();
        map.put("peopleId", id);
        map.put("dramaId", dramaId);
        List<DramaPlayModelKey> dramaPlayModelKeyList = dramaPlayModelMapper.select(map);

        if (dramaPlayModelKeyList != null && dramaPlayModelKeyList.size() > 0) {
            return dramaPlayModelKeyList.get(0);
        }
        return null;
    }


    public boolean updateInDramaStatus(DramaPlayModelKey dramaPlayModelKey) {
        return dramaPlayModelMapper.updateStatus(dramaPlayModelKey) > 0;
    }


}
