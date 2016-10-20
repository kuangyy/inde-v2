package cn.kykys.index.business;

import cn.kykys.index.data.BookModelMapper;
import cn.kykys.index.ibusiness.IBook;
import cn.kykys.index.ibusiness.ITags;
import cn.kykys.index.model.BookModel;
import cn.kykys.index.model.PostsModel;
import cn.kykys.index.model.TagsModel;
import cn.kykys.index.model.dto.ContentModel;
import cn.kykys.index.model.page.PageWeb;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by kuangye on 2016/9/27.
 */
@Service
public class BookBusiness implements IBook {

    @Autowired
    BookModelMapper bookModelMapper;
    @Autowired
    ITags iTags;

    public BookModel getById(Long id) {
        if (id != null && id > 0) {
            BookModel postsModel = bookModelMapper.selectByPrimaryKey(id);

            List<TagsModel> tagsModelList = iTags.getByBookId(id);
            postsModel.setTagsModelList(tagsModelList);

            return postsModel;
        }
        return null;
    }

    public BookModel getByIdAddViewCount(Long id) {

        BookModel postsModel = this.getById(id);
        if (postsModel != null) {
            BookModel model = new BookModel();
            model.setId(postsModel.getId());
            model.setViewCount(postsModel.getViewCount() + 1);
            bookModelMapper.updateByPrimaryKeySelective(model);


            List<TagsModel> tagsModelList = iTags.getByBookId(id);
            postsModel.setTagsModelList(tagsModelList);
        }

        return postsModel;
    }

    public boolean add(BookModel postsModel) {
        return this.add(postsModel, null);
    }


    public boolean add(BookModel postsModel, String tag) {
        if (postsModel != null) {

            int i = bookModelMapper.insertSelective(postsModel);

            if (StringUtils.hasText(tag)) {
                this.addTag(postsModel.getId(), tag);
            }

            return i > 0;
        }

        return false;
    }

    public boolean update(BookModel postsModel) {
        return this.update(postsModel, null);
    }

    public boolean update(BookModel postsModel, String tag) {
        if (postsModel != null) {

            if (StringUtils.hasText(tag)) {
                this.addTag(postsModel.getId(), tag);
            }

            return bookModelMapper.updateByPrimaryKeySelective(postsModel) > 0;
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
            return bookModelMapper.deleteByPrimaryKey(id) > 0;
        }
        return false;
    }



    public Map<String, ?> searchByPage(String word, PageWeb pageWeb) {
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> param = new HashMap<>();
        param.put("word", word);
        param.put("offset", pageWeb.getOffset());
        param.put("limit", pageWeb.getPageSize());

        List<BookModel> bookModelList = bookModelMapper.searchByPage(param);
        result.put("bookModelList", bookModelList);

        int count = bookModelMapper.searchCount(param);
        pageWeb.setPageIndex(pageWeb.getPageIndex());
        pageWeb.setCount(count);
        Pattern.compile("");
        result.put("pageWeb", pageWeb);

        return result;
    }


    public List<BookModel> selectByTag(Long tagId, PageWeb pageWeb) {

        Map<String, Object> param = new HashMap<>();
        param.put("tagId", tagId);
        param.put("offset", pageWeb.getOffset());
        param.put("limit", pageWeb.getLimit());

        return bookModelMapper.selectByTag(param);
    }

    public  Map<String, ?>  selectByTagWithPage(Long tagId, PageWeb pageWeb) {

        Map<String, Object> result = new HashMap<>();

        Map<String, Object> param = new HashMap<>();
        param.put("tagId", tagId);
        param.put("offset", pageWeb.getOffset());
        param.put("limit", pageWeb.getLimit());

        List<BookModel> bookModelList = bookModelMapper.selectByTag(param);

        result.put("bookModelList", bookModelList);
        int count = bookModelMapper.selectByTagCount(param);
        pageWeb.setCount(count);
        result.put("pageWeb", pageWeb);

        return result;
    }

}
