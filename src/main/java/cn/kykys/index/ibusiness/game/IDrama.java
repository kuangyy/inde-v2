package cn.kykys.index.ibusiness.game;

import cn.kykys.index.model.ext.NodeDetail;
import cn.kykys.index.model.wechat.DramaModel;

/**
 * Created by kuangye on 2016/11/17.
 */
public interface IDrama {

    DramaModel getById(Integer dramaId);



    NodeDetail getFirstNodeByDramaId(Integer dramaId);

    NodeDetail getNextNodeByDramaId(Integer dramaId);


}
