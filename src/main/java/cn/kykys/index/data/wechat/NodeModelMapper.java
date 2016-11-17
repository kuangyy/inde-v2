package cn.kykys.index.data.wechat;

import cn.kykys.index.data.WechatDB;
import cn.kykys.index.model.wechat.NodeModel;
import cn.kykys.index.model.wechat.NodeModelWithBLOBs;

@WechatDB
public interface NodeModelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(NodeModelWithBLOBs record);

    int insertSelective(NodeModelWithBLOBs record);

    NodeModelWithBLOBs selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(NodeModelWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(NodeModelWithBLOBs record);

    int updateByPrimaryKey(NodeModel record);
}