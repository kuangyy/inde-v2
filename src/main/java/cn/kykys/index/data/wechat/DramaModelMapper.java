package cn.kykys.index.data.wechat;

import cn.kykys.index.data.WechatDB;
import cn.kykys.index.model.wechat.DramaModel;

import java.util.List;
import java.util.Map;

@WechatDB
public interface DramaModelMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DramaModel record);

    int insertSelective(DramaModel record);

    DramaModel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DramaModel record);

    int updateByPrimaryKeyWithBLOBs(DramaModel record);

    int updateByPrimaryKey(DramaModel record);



    List<DramaModel> searchByPage(Map map);

    int searchCount(Map map);
}