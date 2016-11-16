package cn.kykys.index.business.blog;

import cn.kykys.index.data.kykys.MottoModelMapper;
import cn.kykys.index.ibusiness.blog.IMotto;
import cn.kykys.index.model.blog.MottoModel;
import cn.kykys.index.model.page.PageWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kuangye on 2016/9/25.
 */
@Service
public class MottoBusiness implements IMotto {

    @Autowired
    MottoModelMapper mottoModelMapper;

    public MottoModel getById(Long id) {
        if (id != null && id > 0) {
            return mottoModelMapper.selectByPrimaryKey(id);
        }
        return null;
    }

    public boolean add(MottoModel mottoModel) {
        if (mottoModel != null) {

            return mottoModelMapper.insertSelective(mottoModel)> 0;
        }

        return false;
    }

    public boolean update(MottoModel mottoModel) {
        if (mottoModel != null) {

            return mottoModelMapper.updateByPrimaryKeySelective(mottoModel) > 0;
        }

        return false;
    }

    public boolean delete(Long id) {
        if (id != null) {
            return mottoModelMapper.deleteByPrimaryKey(id) > 0;
        }
        return false;
    }

    public Map<String, ?> selectByPage(MottoModel mottoModel, PageWeb pageWeb) {

        Map<String, Object> result = new HashMap<>();

        Map<String, Object> param = new HashMap<>();
        param.put("model", mottoModel);
        param.put("offset", pageWeb.getOffset());
        param.put("limit", pageWeb.getLimit());

        List<MottoModel> mottoModelList = mottoModelMapper.selectByPage(param);
        result.put("mottoModelList", mottoModelList);

        int count = mottoModelMapper.count(param);
        pageWeb.setPageIndex(pageWeb.getPageIndex());
        pageWeb.setCount(count);

        result.put("pageWeb", pageWeb);

        return result;
    }

}
