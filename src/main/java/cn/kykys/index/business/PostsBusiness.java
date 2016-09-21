package cn.kykys.index.business;

import cn.kykys.index.data.PostsModelMapper;
import cn.kykys.index.ibusiness.IPosts;
import cn.kykys.index.model.PostsModel;
import cn.kykys.index.model.dto.ContentModel;
import cn.kykys.index.model.page.PageWeb;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public PostsModel getById(Long id) {
        if (id != null && id > 0) {
            PostsModel postsModel = postsModelMapper.selectByPrimaryKey(id);
            postsModel.setContentModel(JSON.parseObject(postsModel.getContent(), ContentModel.class));

            return postsModel;
        }
        return null;
    }

    public boolean add(PostsModel postsModel) {
        if (postsModel != null) {

            //default publish now
            if(postsModel.getPublishTime()==null){
                postsModel.setPublishTime(new Date());
            }

            return postsModelMapper.insertSelective(postsModel) > 0;
        }

        return false;
    }

    public boolean update(PostsModel postsModel) {
        if (postsModel != null) {
            return postsModelMapper.updateByPrimaryKeySelective(postsModel) > 0;
        }

        return false;
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
        param.put("page", pageWeb);

        List<PostsModel> postsModelList = postsModelMapper.selectByPage(param);
        result.put("postsModelList", postsModelList);

        int count = postsModelMapper.count(param);
        pageWeb.setPageIndex(pageWeb.getPageIndex());
        pageWeb.setCount(count);

        result.put("pageWeb", pageWeb);

        return result;
    }

}
