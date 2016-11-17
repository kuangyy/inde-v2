package cn.kykys.index.model.wechat;

public class NodeModelWithBLOBs extends NodeModel {
    private String description;

    private String choices;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getChoices() {
        return choices;
    }

    public void setChoices(String choices) {
        this.choices = choices == null ? null : choices.trim();
    }
}