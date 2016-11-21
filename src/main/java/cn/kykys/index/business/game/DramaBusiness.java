package cn.kykys.index.business.game;

import cn.kykys.index.data.wechat.DramaModelMapper;
import cn.kykys.index.data.wechat.DramaPlayModelMapper;
import cn.kykys.index.data.wechat.NodeModelMapper;
import cn.kykys.index.ibusiness.game.IDrama;
import cn.kykys.index.model.dto.gooflow.GooflowLineModel;
import cn.kykys.index.model.dto.gooflow.GooflowModel;
import cn.kykys.index.model.dto.gooflow.GooflowNodeModel;
import cn.kykys.index.model.enumeration.GooflowNodeTypeEnum;
import cn.kykys.index.model.enumeration.NodeTypeEnum;
import cn.kykys.index.model.exception.BusinessException;
import cn.kykys.index.model.ext.ChooseModel;
import cn.kykys.index.model.ext.NodeDetail;
import cn.kykys.index.model.page.PageWeb;
import cn.kykys.index.model.wechat.DramaModel;
import cn.kykys.index.model.wechat.NodeModelWithBLOBs;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

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


    public NodeDetail getFirstNodeByDramaId(Integer dramaId) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("dramaId", dramaId);
        map.put("type", NodeTypeEnum.START.getStatus());

        NodeModelWithBLOBs nodeModelWithBLOBs = nodeModelMapper.selectByType(map);

        return this.nodeHandler(nodeModelWithBLOBs);
    }

    public NodeDetail getNextNodeByDramaId(Long nodeId) {

        if (nodeId != null && nodeId > 0) {
            NodeModelWithBLOBs nodeModelWithBLOBs = nodeModelMapper.selectByPrimaryKey(nodeId);

            return this.nodeHandler(nodeModelWithBLOBs);
        }
        return null;
    }


    private NodeDetail nodeHandler(NodeModelWithBLOBs nodeModelWithBLOBs) {

        if (StringUtils.hasText(nodeModelWithBLOBs.getChoices())) {

            List<ChooseModel> chooseModelList = JSON.parseObject(nodeModelWithBLOBs.getChoices(), new TypeReference<List<ChooseModel>>() {
            });

            NodeDetail nodeDetail = new NodeDetail();
            BeanUtils.copyProperties(nodeModelWithBLOBs, nodeDetail);
            nodeDetail.setChooseModelList(chooseModelList);

            return nodeDetail;
        }

        return null;
    }


    public Map<String, ?> searchByPage(String word, PageWeb pageWeb) {
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> param = new HashMap<>();
        param.put("word", word);
        param.put("offset", pageWeb.getOffset());
        param.put("limit", pageWeb.getPageSize());

        List<DramaModel> dramaModelList = dramaModelMapper.searchByPage(param);
        result.put("dramaModelList", dramaModelList);

        int count = dramaModelMapper.searchCount(param);
        pageWeb.setPageIndex(pageWeb.getPageIndex());
        pageWeb.setCount(count);
        result.put("pageWeb", pageWeb);

        return result;
    }


    public boolean updateDrama(Integer dramaId, GooflowModel gooflowModel, String data) {

        DramaModel dramaModel = dramaModelMapper.selectByPrimaryKey(dramaId);

        dramaModel.setData(data);

        return true;
    }

    public boolean updateDrama2(Integer dramaId, GooflowModel gooflowModel, String data) {

        DramaModel dramaModel = dramaModelMapper.selectByPrimaryKey(dramaId);

        dramaModel.setData(data);

        List<NodeDetail> nodeDetailList = new ArrayList<>(0);


        Map<String, GooflowNodeModel> gooflowNodeModelMap = gooflowModel.getNodes();

        Set<Map.Entry<String, GooflowNodeModel>> set = gooflowNodeModelMap.entrySet();

        //第一步 获取所有场景
        for (Map.Entry<String, GooflowNodeModel> entry : set) {
//            String key = entry.getKey();
            GooflowNodeModel gooflowNodeModel = entry.getValue();
            //插入场景
            if (gooflowNodeModel.getType().equals(GooflowNodeTypeEnum.TASK)) {
                NodeDetail nodeDetail = new NodeDetail();
                nodeDetail.setDescription(gooflowNodeModel.getName());
                nodeDetailList.add(nodeDetail);
            }
        }

        //第二步 获取所有选项
        Map<String, List<ChooseModel>> chooseMap = new HashMap<>();

        for (Map.Entry<String, GooflowNodeModel> entry : set) {
            String key = entry.getKey();
            GooflowNodeModel gooflowNodeModel = entry.getValue();

            List<ChooseModel> chooseModelList = chooseMap.get(key);
            //插入场景
            if (gooflowNodeModel.getType().equals(GooflowNodeTypeEnum.NODE)) {
                ChooseModel chooseModel = new ChooseModel();
                chooseModel.setDescription(gooflowNodeModel.getName());
                if (chooseModelList == null) {
                    chooseModelList = new ArrayList<>();
                }
                chooseModelList.add(chooseModel);

                chooseMap.put(key, chooseModelList);
            }
        }

        //第三步 获取关系 拼装
        //此次先把选项 添加 下节点
        Map<String, GooflowLineModel> gooflowLineModelMap = gooflowModel.getLines();
        for (Map.Entry<String, GooflowLineModel> entry : gooflowLineModelMap.entrySet()) {
            String key = entry.getKey();
            GooflowLineModel gooflowLineModel = entry.getValue();

            String from = gooflowLineModel.getFrom();
            String to = gooflowLineModel.getTo();


            GooflowNodeModel fromNode = gooflowNodeModelMap.get(from);
            GooflowNodeModel toNode = gooflowNodeModelMap.get(to);
//

            //如果是选项
            if (fromNode.getType().equals(GooflowNodeTypeEnum.NODE)) {
                //选项->场景
                if (toNode.getType().equals(GooflowNodeTypeEnum.TASK)) {

                    Optional<NodeDetail> optional = nodeDetailList.stream().filter(e -> e.getDescription().equals(from)).findFirst();
                    if (optional.isPresent()) {
                        NodeDetail nodeDetail = optional.get();

                        List<ChooseModel> chooseModelList = nodeDetail.getChooseModelList();
                        if (chooseModelList == null) {
                            chooseModelList = new ArrayList<>();
                        }

                        List<ChooseModel> chooseModelList1 = chooseMap.get(to);

                        chooseModelList.addAll(chooseModelList1);
                    }
                } else if (toNode.getType().equals(GooflowNodeTypeEnum.END)) {

                } else {
                    throw new BusinessException("节点连接不正确");
                }
            }
        }

        for (Map.Entry<String, GooflowLineModel> entry : gooflowLineModelMap.entrySet()) {
            String key = entry.getKey();
            GooflowLineModel gooflowLineModel = entry.getValue();

            String from = gooflowLineModel.getFrom();
            String to = gooflowLineModel.getTo();


            GooflowNodeModel fromNode = gooflowNodeModelMap.get(from);
            GooflowNodeModel toNode = gooflowNodeModelMap.get(to);
            //如果是开始节点 无操作
//            if(fromNode.getType().equals(GooflowNodeTypeEnum.START))
            //如果是场景
            if (fromNode.getType().equals(GooflowNodeTypeEnum.TASK)) {
                //场景->选项
                if (toNode.getType().equals(GooflowNodeTypeEnum.NODE)) {

                    Optional<NodeDetail> optional = nodeDetailList.stream().filter(e -> e.getDescription().equals(from)).findFirst();
                    if (optional.isPresent()) {
                        NodeDetail nodeDetail = optional.get();

                        List<ChooseModel> chooseModelList = nodeDetail.getChooseModelList();
                        if (chooseModelList == null) {
                            chooseModelList = new ArrayList<>();
                        }

                        List<ChooseModel> chooseModelList1 = chooseMap.get(to);

                        chooseModelList.addAll(chooseModelList1);
                    }
                } else {
                    throw new BusinessException("节点连接不正确");
                }
            }
        }


        return true;
    }

}
