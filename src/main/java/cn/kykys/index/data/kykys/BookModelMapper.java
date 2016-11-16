package cn.kykys.index.data.kykys;

import cn.kykys.index.data.KykysDB;
import cn.kykys.index.model.blog.BookModel;

import java.util.List;
import java.util.Map;

@KykysDB
public interface BookModelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BookModel record);

    int insertSelective(BookModel record);

    BookModel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BookModel record);

    int updateByPrimaryKeyWithBLOBs(BookModel record);

    int updateByPrimaryKey(BookModel record);







    List<BookModel> searchByPage(Map<String,?> map);

    int searchCount(Map<String,?> map);


    List<BookModel> selectByTag(Map map);

    int selectByTagCount(Map map);
}