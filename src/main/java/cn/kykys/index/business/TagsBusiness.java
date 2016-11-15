package cn.kykys.index.business;

import cn.kykys.index.data.kykys.RBookTagModelMapper;
import cn.kykys.index.data.kykys.RPostTagModelMapper;
import cn.kykys.index.data.kykys.TagsModelMapper;
import cn.kykys.index.ibusiness.IPosts;
import cn.kykys.index.ibusiness.ITags;
import cn.kykys.index.model.PostsModel;
import cn.kykys.index.model.RPostTagModelKey;
import cn.kykys.index.model.TagsModel;
import cn.kykys.index.model.page.PageWeb;
import cn.kykys.index.utils.language.Chinese2Pinyin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/23.
 */
@Service
public class TagsBusiness implements ITags {

    @Autowired
    IPosts iPosts;

    @Autowired
    TagsModelMapper tagsModelMapper;
    @Autowired
    RPostTagModelMapper rPostTagModelMapper;
    @Autowired
    RBookTagModelMapper rBookTagModelMapper;

    public TagsModel getById(Long id) {
        if (id != null && id > 0) {
            return tagsModelMapper.selectByPrimaryKey(id);
        }
        return null;
    }


    public boolean add(TagsModel tagsModel) {
        if (tagsModel != null) {
            return tagsModelMapper.insertSelective(tagsModel) > 0;
        }
        return false;
    }

    public boolean update(TagsModel tagsModel) {
        if (tagsModel != null) {
            return tagsModelMapper.updateByPrimaryKeySelective(tagsModel) > 0;
        }
        return false;
    }

    public boolean delete(Long id) {
        if (id != null) {
            return tagsModelMapper.deleteByPrimaryKey(id) > 0;
        }
        return false;
    }

    public Map<String, ?> selectByPage(TagsModel postsModel, PageWeb pageWeb) {
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> param = new HashMap<>();
        param.put("model", postsModel);
        param.put("offset", pageWeb.getOffset());
        param.put("limit", pageWeb.getLimit());

        List<TagsModel> tagsModelList = tagsModelMapper.selectByPage(param);
        result.put("tagsModelList", tagsModelList);

        int count = tagsModelMapper.count(param);
        pageWeb.setPageIndex(pageWeb.getPageIndex());
        pageWeb.setCount(count);

        result.put("pageWeb", pageWeb);

        return result;
    }


    public List<TagsModel> selectHotTagByPage(PageWeb pageWeb) {
        return this.selectHotTagByPage(pageWeb, 0, false);
    }

    public List<TagsModel> selectHotTagByPage(PageWeb pageWeb, Integer postsCount, boolean needPosts) {

        List<TagsModel> tagsModelList = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();

        if (pageWeb != null) {
            map.put("offset", pageWeb.getOffset());
            map.put("limit", pageWeb.getLimit());
        }

        //获取 hot tag
        List<Map<String, Object>> mapList = tagsModelMapper.selectByPostsCount(map);

        if (mapList != null && mapList.size() > 0) {
            for (Map<String, Object> result : mapList) {
                //set tag
                TagsModel tagsModel = new TagsModel();
                tagsModel.setId((Long) result.get("id"));
                tagsModel.setSimbol((String) result.get("simbol"));
                tagsModel.setName((String) result.get("name"));
                tagsModel.setPostsCount((Long) result.get("postCount"));

                if (needPosts) {
                    postsCount = postsCount == null ? 0 : postsCount;
                    //set posts
                    PageWeb pageWeb1 = new PageWeb();
                    pageWeb1.setPageSize(postsCount);

                    List<PostsModel> postsModels = iPosts.selectByTag(tagsModel.getId(), pageWeb1);
                    tagsModel.setPostsModelList(postsModels);
                }

                tagsModelList.add(tagsModel);
            }
        }

        return tagsModelList;
    }


    public List<TagsModel> getByPostId(Long id) {
        if (id != null) {

            List<Long> ids = rPostTagModelMapper.selectTagidByPostid(id);
            if (ids != null && ids.size() > 0) {
                return tagsModelMapper.selectByPostId(ids);
            }
        }
        return null;
    }

    public List<TagsModel> getByBookId(Long id){
        if (id != null) {

            List<Long> ids = rBookTagModelMapper.selectTagidByBookid(id);
            if (ids != null && ids.size() > 0) {
                return tagsModelMapper.selectByPostId(ids);
            }
        }
        return null;
    }


    public void addTagAndLinkPost(String name, Long postid) {

        TagsModel tagsModel = tagsModelMapper.selectByName(name);

        Long tagid;
        if (tagsModel != null) {
            tagid = tagsModel.getId();
        } else {
            TagsModel model = new TagsModel();
            model.setName(name);
            model.setSimbol(Chinese2Pinyin.cn2Spell(name));
            tagsModelMapper.insertSelective(model);

            tagid = model.getId();
        }


        RPostTagModelKey rPostTagModelKey = new RPostTagModelKey();
        rPostTagModelKey.setPostId(postid);
        rPostTagModelKey.setTagId(tagid);
        rPostTagModelMapper.insert(rPostTagModelKey);
    }


    public void deleteByPostid(Long postid) {
        rPostTagModelMapper.deleteByPostid(postid);
    }

    public TagsModel getByName(String name) {
        if (StringUtils.hasText(name)) {
            return tagsModelMapper.selectByName(name);
        }
        return null;
    }
}





