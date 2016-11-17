package cn.kykys.index.business.game;

import cn.kykys.index.data.wechat.DramaModelMapper;
import cn.kykys.index.data.wechat.DramaPlayModelMapper;
import cn.kykys.index.data.wechat.NodeModelMapper;
import cn.kykys.index.ibusiness.game.IDrama;
import cn.kykys.index.model.enumeration.NodeTypeEnum;
import cn.kykys.index.model.ext.NodeDetail;
import cn.kykys.index.model.wechat.DramaModel;
import cn.kykys.index.model.wechat.NodeModelWithBLOBs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Created by kuangye on 2016/11/17.
 */
@Service
public class DramaBusiness implements IDrama {


    @Autowired
    DramaModelMapper dramaModelMapper;
    @Autowired
    DramaPlayModelMapper dramaPlayModelMapper;

    @Autowired
    NodeModelMapper nodeModelMapper;


    public DramaModel getById(Integer dramaId) {
        if (dramaId != null && dramaId > 0) {
            return dramaModelMapper.selectByPrimaryKey(dramaId);
        }
        return null;
    }



    NodeDetail getFirstNodeByDramaId(Integer dramaId){

        HashMap map = new HashMap();
        map.put("dramaId",dramaId);
        map.put("type", NodeTypeEnum.START.getStatus());

        NodeModelWithBLOBs nodeModelWithBLOBs = nodeModelMapper.selectByType(map);


    }

    NodeDetail getNextNodeByDramaId(Integer dramaId){

    }

}
