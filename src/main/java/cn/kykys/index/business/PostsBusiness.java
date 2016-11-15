package cn.kykys.index.business;

import cn.kykys.index.data.kykys.PostsModelMapper;
import cn.kykys.index.ibusiness.IPosts;
import cn.kykys.index.ibusiness.ITags;
import cn.kykys.index.model.PostsModel;
import cn.kykys.index.model.TagsModel;
import cn.kykys.index.model.dto.ContentModel;
import cn.kykys.index.model.page.PageWeb;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kuangye on 2016/9/21.
 */
@Service
public class PostsBusiness implements IPosts {

    @Autowired
    PostsModelMapper postsModelMapper;
    @Autowired
    ITags iTags;

    public PostsModel getById(Long id) {
        if (id != null && id > 0) {
            PostsModel postsModel = postsModelMapper.selectByPrimaryKey(id);
            postsModel.setContentModel(JSON.parseObject(postsModel.getContent(), ContentModel.class));

            List<TagsModel> tagsModelList = iTags.getByPostId(id);
            postsModel.setTagsModelList(tagsModelList);

            return postsModel;
        }
        return null;
    }

    public PostsModel getByIdAddViewCount(Long id) {

        PostsModel postsModel = this.getById(id);
        if (postsModel != null) {
            PostsModel model = new PostsModel();
            model.setId(postsModel.getId());
            model.setViewCount(postsModel.getViewCount() + 1);
            postsModelMapper.updateByPrimaryKeySelective(model);


            List<TagsModel> tagsModelList = iTags.getByPostId(id);
            postsModel.setTagsModelList(tagsModelList);
        }

        return postsModel;
    }

    public boolean add(PostsModel postsModel) {
        return this.add(postsModel, null);
    }


    public boolean add(PostsModel postsModel, String tag) {
        if (postsModel != null) {

            //default publish now
            if (postsModel.getPublishTime() == null) {
                postsModel.setPublishTime(new Date());
            }

            int i = postsModelMapper.insertSelective(postsModel);

            if (StringUtils.hasText(tag)) {
                this.addTag(postsModel.getId(), tag);
            }

            return i > 0;
        }

        return false;
    }

    public boolean update(PostsModel postsModel) {
        return this.update(postsModel, null);
    }

    public boolean update(PostsModel postsModel, String tag) {
        if (postsModel != null) {

            if (StringUtils.hasText(tag)) {
                this.addTag(postsModel.getId(), tag);
            }

            return postsModelMapper.updateByPrimaryKeySelective(postsModel) > 0;
        }

        return false;
    }

    private void addTag(Long id, String tag) {

        iTags.deleteByPostid(id);

        if (StringUtils.hasText(tag)) {
            String[] tags = tag.split("[,，]");

            for (String t : tags) {
                iTags.addTagAndLinkPost(t, id);
            }
        }
    }


    public boolean delete(Long id) {
        if (id != null) {
            return postsModelMapper.deleteByPrimaryKey(id) > 0;
        }
        return false;
    }

    public Map<String, ?> selectByPage(PostsModel postsModel, PageWeb pageWeb) {

        Map<String, Object> result = new HashMap<>();

        Map<String, Object> param = new HashMap<>();
        param.put("model", postsModel);
        param.put("offset", pageWeb.getOffset());
        param.put("limit", pageWeb.getLimit());

        List<PostsModel> postsModelList = postsModelMapper.selectByPage(param);
        result.put("postsModelList", postsModelList);

        int count = postsModelMapper.count(param);
        pageWeb.setPageIndex(pageWeb.getPageIndex());
        pageWeb.setCount(count);

        result.put("pageWeb", pageWeb);

        return result;
    }


    public Map<String, ?> searchByPage(String word, PageWeb pageWeb) {
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> param = new HashMap<>();
        param.put("word", word);
        param.put("offset", pageWeb.getOffset());
        param.put("limit", pageWeb.getLimit());

        List<PostsModel> postsModelList = postsModelMapper.searchByPage(param);
        result.put("postsModelList", postsModelList);

        int count = postsModelMapper.searchCount(param);
        pageWeb.setPageIndex(pageWeb.getPageIndex());
        pageWeb.setCount(count);

        result.put("pageWeb", pageWeb);

        return result;
    }


    public List<PostsModel> selectByTag(Long tagId, PageWeb pageWeb) {

        Map<String, Object> param = new HashMap<>();
        param.put("tagId", tagId);
        param.put("offset", pageWeb.getOffset());
        param.put("limit", pageWeb.getLimit());

        return postsModelMapper.selectByTag(param);
    }

    public  Map<String, ?>  selectByTagWithPage(Long tagId, PageWeb pageWeb) {

        Map<String, Object> result = new HashMap<>();

        Map<String, Object> param = new HashMap<>();
        param.put("tagId", tagId);
        param.put("offset", pageWeb.getOffset());
        param.put("limit", pageWeb.getLimit());

        List<PostsModel> postsModelList = postsModelMapper.selectByTag(param);

        result.put("postsModelList", postsModelList);
        int count = postsModelMapper.selectByTagCount(param);
        pageWeb.setCount(count);
        result.put("pageWeb", pageWeb);

        return result;
    }

}
