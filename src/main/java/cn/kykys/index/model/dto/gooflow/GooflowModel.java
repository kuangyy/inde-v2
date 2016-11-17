package cn.kykys.index.model.dto.gooflow;

import java.util.Map;

/**
 * Created by kuangye on 2016/11/17.
 */
public class GooflowModel {

    private String title;

    private Map<String, GooflowNodeModel> nodes;

    private Map<String, GooflowLineModel> lines;

    private Map<String, GooflowAreaModel> areas;

    private Integer initNum;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, GooflowNodeModel> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, GooflowNodeModel> nodes) {
        this.nodes = nodes;
    }

    public Map<String, GooflowLineModel> getLines() {
        return lines;
    }

    public void setLines(Map<String, GooflowLineModel> lines) {
        this.lines = lines;
    }

    public Map<String, GooflowAreaModel> getAreas() {
        return areas;
    }

    public void setAreas(Map<String, GooflowAreaModel> areas) {
        this.areas = areas;
    }

    public Integer getInitNum() {
        return initNum;
    }

    public void setInitNum(Integer initNum) {
        this.initNum = initNum;
    }
}
