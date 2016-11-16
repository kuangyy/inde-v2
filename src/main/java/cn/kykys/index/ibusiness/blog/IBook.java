package cn.kykys.index.ibusiness.blog;

import cn.kykys.index.model.blog.BookModel;
import cn.kykys.index.model.page.PageWeb;

import java.util.List;
import java.util.Map;

/**
 * Created by kuangye on 2016/9/27.
 */
public interface IBook {

    BookModel getById(Long id);

    BookModel getByIdAddViewCount(Long id);

    boolean add(BookModel bookModel);

    boolean update(BookModel bookModel);

    boolean add(BookModel bookModel, String tag);

    boolean update(BookModel bookModel, String tag);

    boolean delete(Long id);


    Map<String, ?> searchByPage(String word, PageWeb pageWeb);


    List<BookModel> selectByTag(Long tagId, PageWeb pageWeb);

    Map<String, ?> selectByTagWithPage(Long tagId, PageWeb pageWeb);

}
