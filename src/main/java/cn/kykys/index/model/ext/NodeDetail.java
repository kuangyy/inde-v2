package cn.kykys.index.model.ext;

import cn.kykys.index.model.wechat.NodeModelWithBLOBs;

import java.util.List;

/**
 * Created by kuangye on 2016/11/17.
 */
public class NodeDetail extends NodeModelWithBLOBs {

    private List<ChooseModel> chooseModelList;

    public List<ChooseModel> getChooseModelList() {
        return chooseModelList;
    }

    public void setChooseModelList(List<ChooseModel> chooseModelList) {
        this.chooseModelList = chooseModelList;
    }
}
