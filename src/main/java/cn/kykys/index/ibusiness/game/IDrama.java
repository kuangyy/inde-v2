package cn.kykys.index.ibusiness.game;

import cn.kykys.index.model.dto.gooflow.GooflowModel;
import cn.kykys.index.model.ext.NodeDetail;
import cn.kykys.index.model.page.PageWeb;
import cn.kykys.index.model.wechat.DramaModel;
import cn.kykys.index.model.wechat.DramaPlayModelKey;

import java.util.Map;

/**
 * Created by kuangye on 2016/11/17.
 */
public interface IDrama {

    DramaModel getById(Integer dramaId);



    NodeDetail getFirstNodeByDramaId(Integer dramaId);

    NodeDetail getNodeByNodeId(String nodeId);

    Map<String, ?> searchByPage(String word, PageWeb pageWeb);



    boolean updateDrama(Integer dramaId, GooflowModel gooflowModel,DramaModel dramaModel);


    boolean addDramaPeopleRelation(DramaPlayModelKey dramaPlayModelKey);

    boolean updateDramaPeopleRelation(DramaPlayModelKey dramaPlayModelKey);
}
