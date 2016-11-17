package cn.kykys.index.model.dto.gooflow;

/**
 * Created by kuangye on 2016/11/17.
 */
public class GooflowNodeModel {

    private String name;
    private Integer left;
    private Integer top;
    private String type;
    private Integer width;
    private Integer height;
    private Boolean alt;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLeft() {
        return left;
    }

    public void setLeft(Integer left) {
        this.left = left;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Boolean getAlt() {
        return alt;
    }

    public void setAlt(Boolean alt) {
        this.alt = alt;
    }
}
