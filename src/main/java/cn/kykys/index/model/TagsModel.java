package cn.kykys.index.model;

import java.util.Date;
import java.util.List;

public class TagsModel {
    private Long id;

    private String name;

    private String simbol;

    private Date createTime;

    private Date updateTime;




    private Long postsCount;

    private List<PostsModel> postsModelList;

    public Long getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(Long postsCount) {
        this.postsCount = postsCount;
    }

    public List<PostsModel> getPostsModelList() {
        return postsModelList;
    }

    public void setPostsModelList(List<PostsModel> postsModelList) {
        this.postsModelList = postsModelList;
    }








    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getSimbol() {
        return simbol;
    }

    public void setSimbol(String simbol) {
        this.simbol = simbol == null ? null : simbol.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}