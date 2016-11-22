package cn.kykys.index.data.wechat;

import cn.kykys.index.data.WechatDB;
import cn.kykys.index.model.wechat.NodeModel;
import cn.kykys.index.model.wechat.NodeModelWithBLOBs;

import java.util.Map;

@WechatDB
public interface NodeModelMapper {
    int deleteByPrimaryKey(String id);

    int insert(NodeModelWithBLOBs record);

    int insertSelective(NodeModelWithBLOBs record);

    NodeModelWithBLOBs selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(NodeModelWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(NodeModelWithBLOBs record);

    int updateByPrimaryKey(NodeModel record);





    NodeModelWithBLOBs selectByType(Map map);

    int deleteByDramaId(Integer dramaId);
}