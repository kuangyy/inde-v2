package cn.kykys.index.business;

import cn.kykys.index.data.TagsModelMapper;
import cn.kykys.index.ibusiness.ITags;
import cn.kykys.index.model.TagsModel;
import cn.kykys.index.model.page.PageWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/23.
 */
@Service
public class TagsBusiness implements ITags {

    @Autowired
    TagsModelMapper tagsModelMapper;

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

}





