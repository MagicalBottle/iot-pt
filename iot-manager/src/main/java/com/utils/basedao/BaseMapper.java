package com.utils.basedao;


import java.util.List;
import java.util.Map;

/**
*   @desc : 通用单表操作接口
*   @auth : TYF
*   @data : 2019-01-25 - 14:46
*/
public interface BaseMapper {

    List<Map<String, Object>> selectByConditions(List<String> selective,
                                                 String tableName,
                                                 Map<String, Object> params,
                                                 PageOrderConfig pageOrderConfig,
                                                 List<SearchBean> searchBeans);


    int selectCountByConditions(String tableName,
                                Map<String, Object> params,
                                List<SearchBean> searchBeans);

    Map<String,Object> selectByPrimaryKey(List<String> selective,
                                          String tableName,
                                          Long id);

    int deleteByConditions(String tableName,
                           Map<String, Object> params,
                           List<SearchBean> searchBeans);

    int insert(String tableName,
               Map<String, Object> params);


    int updateByConditions(String tableName,
                           Map<String, Object> fields,
                           Map<String, Object> params,
                           List<SearchBean> searchBeans);

    int updateByPrimaryKey(String tableName,
                           Map<String, Object> params);

    Long selectSequence(String seqName);

    List<Map<String, Object>> selectObjectBySql(String sql);
}
