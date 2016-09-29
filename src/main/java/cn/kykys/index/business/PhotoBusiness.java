package cn.kykys.index.business;

import cn.kykys.index.data.PhotoModelMapper;
import cn.kykys.index.ibusiness.IPhoto;
import cn.kykys.index.model.PhotoModel;
import cn.kykys.index.model.page.PageWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kuangye on 2016/9/28.
 */
@Service
public class PhotoBusiness implements IPhoto {

    @Autowired
    PhotoModelMapper photoModelMapper;


    public Map<String, ?> searchByPage( PageWeb pageWeb) {
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> param = new HashMap<>();
        param.put("offset", pageWeb.getOffset());
        param.put("limit", pageWeb.getPageSize());

        List<PhotoModel> photoModelList = photoModelMapper.searchByPage(param);
        result.put("photoModelList", photoModelList);

        int count = photoModelMapper.searchCount(param);
        pageWeb.setPageIndex(pageWeb.getPageIndex());
        pageWeb.setCount(count);

        result.put("pageWeb", pageWeb);

        return result;
    }

}
