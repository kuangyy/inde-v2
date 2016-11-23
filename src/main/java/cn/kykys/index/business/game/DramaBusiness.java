package cn.kykys.index.business.game;

import cn.kykys.index.data.TransactionManagerName;
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
import cn.kykys.index.model.wechat.DramaPlayModelKey;
import cn.kykys.index.model.wechat.NodeModelWithBLOBs;
import cn.kykys.index.utils.DateUtils;
import cn.kykys.index.utils.LogUtil;
import cn.kykys.index.utils.data.DataUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
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

    public NodeDetail getNodeByNodeId(String nodeId) {

        if (StringUtils.hasText(nodeId)) {
            NodeModelWithBLOBs nodeModelWithBLOBs = nodeModelMapper.selectByPrimaryKey(nodeId);

            return this.nodeHandler(nodeModelWithBLOBs);
        }
        return null;
    }


    private NodeDetail nodeHandler(NodeModelWithBLOBs nodeModelWithBLOBs) {

        NodeDetail nodeDetail = new NodeDetail();

        if (StringUtils.hasText(nodeModelWithBLOBs.getChoices())) {

            List<ChooseModel> chooseModelList = JSON.parseObject(nodeModelWithBLOBs.getChoices(), new TypeReference<List<ChooseModel>>() {
            });
            nodeDetail.setChooseModelList(chooseModelList);
        }

        BeanUtils.copyProperties(nodeModelWithBLOBs, nodeDetail);

        return nodeDetail;
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


    @Transactional(value = TransactionManagerName.wechatDbTransactionManager, rollbackFor = Exception.class)
    public boolean updateDrama(Integer dramaId, GooflowModel gooflowModel, String data) {

        DramaModel dramaModel = dramaModelMapper.selectByPrimaryKey(dramaId);

        dramaModel.setData(data);

        dramaModelMapper.updateByPrimaryKeySelective(dramaModel);

        updateDramaNode(dramaId, gooflowModel, data);

        return true;
    }


    private String gernerateANodeId(Integer dramaId) {
        return dramaId + "-" + UUID.randomUUID().toString();
    }

    /**
     * 需要先指定id 不然不好创建关系
     *
     * @param dramaId
     * @param gooflowModel
     * @param data
     * @return
     */
    @Transactional(value = TransactionManagerName.wechatDbTransactionManager, rollbackFor = Exception.class)
    private boolean updateDramaNode(Integer dramaId, GooflowModel gooflowModel, String data) {

        //先删除
        nodeModelMapper.deleteByDramaId(dramaId);

        DramaModel dramaModel = dramaModelMapper.selectByPrimaryKey(dramaId);
        //保存流程
        dramaModel.setData(data);
        //获取所有节点
        Map<String, GooflowNodeModel> gooflowNodeModelMap = gooflowModel.getNodes();
        //获取所有关系
        Map<String, GooflowLineModel> gooflowLineModelMap = gooflowModel.getLines();

        //节点列表
        Map<String, NodeDetail> nodeDetailMap = new HashMap<>(0);

        Set<Map.Entry<String, GooflowNodeModel>> set = gooflowNodeModelMap.entrySet();

        //所有的选项
        Map<String, ChooseModel> chooseMap = new HashMap<>();
        //第一步 获取所有场景 获取所有选项
        for (Map.Entry<String, GooflowNodeModel> entry : set) {
            String key = entry.getKey();
            GooflowNodeModel gooflowNodeModel = entry.getValue();

            //插入场景
            if (gooflowNodeModel.getType().equals(GooflowNodeTypeEnum.TASK.getStatus())) {
                NodeDetail nodeDetail = new NodeDetail();
                nodeDetail.setDramaId(dramaId);
                nodeDetail.setDescription(gooflowNodeModel.getName());
                //获取ID
                nodeDetail.setId(this.gernerateANodeId(dramaId));

                nodeDetailMap.put(key, nodeDetail);
            }
            //插入选项
            ChooseModel chooseModel = chooseMap.get(key);
            if (gooflowNodeModel.getType().equals(GooflowNodeTypeEnum.NODE.getStatus())) {
                if (chooseModel == null) {
                    chooseModel = new ChooseModel();
                }
                chooseModel.setDescription(gooflowNodeModel.getName());
                chooseMap.put(key, chooseModel);
            }
        }

        //第二步 获取关系 拼装
        //此次先把选项 添加 下一节点
        for (Map.Entry<String, GooflowLineModel> entry : gooflowLineModelMap.entrySet()) {
            String key = entry.getKey();
            GooflowLineModel gooflowLineModel = entry.getValue();
            //节点名
            String from = gooflowLineModel.getFrom();
            String to = gooflowLineModel.getTo();
            //节点对象
            GooflowNodeModel fromNode = gooflowNodeModelMap.get(from);
            GooflowNodeModel toNode = gooflowNodeModelMap.get(to);

            //如果是选项
            if (fromNode.getType().equals(GooflowNodeTypeEnum.NODE.getStatus())) {
                //选项->场景
                if (toNode.getType().equals(GooflowNodeTypeEnum.TASK.getStatus())) {
                    //找到选项
                    Optional<Map.Entry<String, ChooseModel>> op1 = chooseMap.entrySet().stream().filter(e -> e.getKey().equals(from)).findFirst();
                    //找到下一节点
                    Optional<Map.Entry<String, NodeDetail>> op2 = nodeDetailMap.entrySet().stream().filter(e -> e.getKey().equals(to)).findFirst();

                    if (op1.isPresent() && op2.isPresent()) {
                        ChooseModel chooseModel = op1.get().getValue();
                        //设置下一节点
                        chooseModel.setNextNodeId(op2.get().getValue().getId());
                    }
                } else {
                    throw new BusinessException("节点连接不正确");
                }
            }
        }

        //第三步 还是关系
        // 这次获取 节点 选项关系
        for (Map.Entry<String, GooflowLineModel> entry : gooflowLineModelMap.entrySet()) {
            String key = entry.getKey();
            GooflowLineModel gooflowLineModel = entry.getValue();

            String from = gooflowLineModel.getFrom();
            String to = gooflowLineModel.getTo();


            GooflowNodeModel fromNode = gooflowNodeModelMap.get(from);
            GooflowNodeModel toNode = gooflowNodeModelMap.get(to);

            //如果是开始节点
            if (fromNode.getType().equals(GooflowNodeTypeEnum.START.getStatus())) {
                //获取场景
                Optional<Map.Entry<String, NodeDetail>> op1 = nodeDetailMap.entrySet().stream().filter(e -> e.getKey().equals(to)).findFirst();
                //设置节点为开始节点
                if (op1.isPresent()) {
                    op1.get().getValue().setType(NodeTypeEnum.START.getStatus());
                }
            } else if (fromNode.getType().equals(GooflowNodeTypeEnum.TASK.getStatus())) {
                //如果是场景
                //场景->选项
                if (toNode.getType().equals(GooflowNodeTypeEnum.NODE.getStatus())) {
                    //获取场景
                    Optional<Map.Entry<String, NodeDetail>> op1 = nodeDetailMap.entrySet().stream().filter(e -> e.getKey().equals(from)).findFirst();
                    //获取选项
                    Optional<Map.Entry<String, ChooseModel>> op2 = chooseMap.entrySet().stream().filter(e -> e.getKey().equals(to)).findFirst();

                    if (op1.isPresent() && op2.isPresent()) {
                        NodeDetail nodeDetail = op1.get().getValue();
                        //设置为中继节点
                        nodeDetail.setType(NodeTypeEnum.REPALY.getStatus());

                        List<ChooseModel> chooseModelList = nodeDetail.getChooseModelList();
                        if (chooseModelList == null) {
                            chooseModelList = new ArrayList<>();
                            nodeDetail.setChooseModelList(chooseModelList);
                        }
                        ChooseModel chooseModel = op2.get().getValue();
                        chooseModelList.add(chooseModel);
                    }
                } else if (toNode.getType().equals(GooflowNodeTypeEnum.END.getStatus())) {
                    //场景->结束
                    //获取场景
                    Optional<Map.Entry<String, NodeDetail>> op1 = nodeDetailMap.entrySet().stream().filter(e -> e.getKey().equals(from)).findFirst();
                    //设置节点为结束节点
                    if (op1.isPresent()) {
                        op1.get().getValue().setType(NodeTypeEnum.END.getStatus());
                    }
                } else {
                    throw new BusinessException("节点连接不正确");
                }
            }
        }

        //拼装结束获取所有节点修改数据库

        if (nodeDetailMap != null && nodeDetailMap.size() > 0) {
            for (Map.Entry<String, NodeDetail> entry : nodeDetailMap.entrySet()) {
                NodeDetail nodeDetail = entry.getValue();
                //选项用JSON存储
                if (nodeDetail.getChooseModelList() != null && nodeDetail.getChooseModelList().size() > 0) {
                    String choices = JSON.toJSONString(nodeDetail.getChooseModelList());
                    nodeDetail.setChoices(choices);
                }
                nodeModelMapper.insertSelective(nodeDetail);
            }
        }

        return true;
    }


    public boolean addDramaPeopleRelation(DramaPlayModelKey dramaPlayModelKey) {
        if (dramaPlayModelKey != null) {
            return dramaPlayModelMapper.insertSelective(dramaPlayModelKey) > 0;
        }
        return false;
    }


    public boolean updateDramaPeopleRelation(DramaPlayModelKey dramaPlayModelKey) {
        if (dramaPlayModelKey != null) {
            return dramaPlayModelMapper.updateStatus(dramaPlayModelKey) > 0;
        }
        return false;
    }
}
