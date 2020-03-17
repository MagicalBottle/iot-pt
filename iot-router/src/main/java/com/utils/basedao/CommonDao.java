package com.utils.basedao;

import java.util.List;
import java.util.Map;

/**
*   @desc : 单表公共操作接口
*   @auth : TYF
*   @params :
*   @return :
*   @data : 2019-01-25 - 16:36
*/
public interface CommonDao<T> {

    /**
     * 根据简单条件查询列表
     * @param conditions 简单条件
     * @return
     */
    List<T> selectListByConditions(T conditions);

    /**
     * 根据条件查询列表,带分页
     * @param conditions 简单条件
     * @param pageOrderConfig 分页条件
     * @return
     */
    List<T> selectListByConditions(T conditions, PageOrderConfig pageOrderConfig);

    /**
     * 根据挑选字段和简单条件查询列表
     * @param selective 查询字段集合
     * @param conditions 简单条件
     * @return
     */
    List<T> selectListByConditions(List<String> selective, T conditions);

    /**
     * 根据挑选字段和简单条件查询列表,带分页
     * @param selective 查询字段集合
     * @param conditions 简单条件
     * @param pageOrderConfig 分页条件
     * @return
     */
    List<T> selectListByConditions(List<String> selective, T conditions, PageOrderConfig pageOrderConfig);


    /**
     * 根据简单条件和高级自定义条件查询列表
     * @param conditions 简单条件
     * @param searchBeans 高级自定义条件
     * @return
     */
    List<T> selectListByConditions(T conditions, List<SearchBean> searchBeans);

    /**
     * 根据简单条件和高级自定义条件查询列表
     * @param conditions 简单条件
     * @param searchBeans 高级自定义条件
     * @param pageOrderConfig 分页条件
     * @return
     * @time 2017-9-4 下午9:22:13
     */
    List<T> selectListByConditions(T conditions, List<SearchBean> searchBeans, PageOrderConfig pageOrderConfig);

    /**
     * 根据挑选字段,简单条件和高级自定义条件查询列表
     * @param selective 查询字段集合
     * @param conditions 简单条件
     * @param searchBeans 高级自定义条件
     * @return
     */
    List<T> selectListByConditions(List<String> selective, T conditions, List<SearchBean> searchBeans);

    /**
     * 根据挑选字段,简单条件和高级自定义条件查询列表,带分页
     * @param selective 查询字段集合
     * @param conditions 简单条件
     * @param searchBeans 高级自定义条件
     * @param pageOrderConfig 分页条件
     * @return
     */
    List<T> selectListByConditions(List<String> selective, T conditions, List<SearchBean> searchBeans, PageOrderConfig pageOrderConfig);

    /**
     * 根据简单条件查询数量
     * @param conditions 简单条件
     * @return
     */
    int selectCountByConditions(T conditions);

    /**
     * 根据简单条件和高级查询条件查询数量
     * @param conditions 简单条件
     * @param searchBeans 高级自定义条件
     * @return
     */
    int selectCountByConditions(T conditions, List<SearchBean> searchBeans);

    /**
     * 根据简单条件查询单个对象
     * @param conditions 简单条件
     * @return
     */
    T selectObjectByConditions(T conditions);

    /**
     * 根据挑选字段和简单条件查询单个对象
     * @param selective 挑选字段
     * @param conditions 简单条件
     * @return
     */
    T selectObjectByConditions(List<String> selective, T conditions);

    /**
     * 根据基本查询条件和高级查询条件查询数量
     * @param conditions 简单条件
     * @param searchBeans 高级自定义条件
     * @return
     */
    T selectObjectByConditions(T conditions, List<SearchBean> searchBeans);

    /**
     * 根据挑选字段和简单条件和高级查询条件查询数量
     * @param selective 挑选字段
     * @param conditions 简单条件
     * @param searchBeans 高级自定义条件
     * @return
     */
    T selectObjectByConditions(List<String> selective, T conditions, List<SearchBean> searchBeans);

    /**
     * 根据主键查询单个对象
     * @param id 主键
     * @param modelClass 实体类型
     * @return
     */
    T selectObjectByPrimaryKey(Long id, Class<T> modelClass);

    /**
     * 根据挑选字段和主键查询单个对象
     * @param selective 挑选字段
     * @param id 主键
     * @param modelClass 实体类型
     * @return
     */
    T selectObjectByPrimaryKey(List<String> selective, Long id, Class<T> modelClass);

    /**
     * 插入单条记录
     * @param insertData 待插入实体对象
     * @return
     */
    int insert(T insertData);

    /**
     * 根据简单条件删除
     * @param conditions 简单条件
     * @return
     */
    int deleteByConditions(T conditions);

    /**
     * 根据简单条件和高级自定义条件删除记录
     * @param conditions 简单条件
     * @param searchBeans 高级自定义条件
     * @return
     */
    int deleteByConditions(T conditions, List<SearchBean> searchBeans);

    /**
     * 根据主键更新记录
     * @param updateData 待更新实体对象(id必传)
     * @return
     */
    int updateByPrimaryKey(T updateData);

    /**
     * 根据简单条件更新记录
     * @param updateData 待更新实体
     * @param conditions 简单条件
     * @return
     */
    int updateByConditions(T updateData, T conditions);

    /**
     * 根据简单条件和高级自定义条件更新记录
     * @param updateData 待更新实体
     * @param conditions 简单条件
     * @param searchBeans 高级自定义条件
     * @return
     */
    int updateByConditions(T updateData, T conditions, List<SearchBean> searchBeans);

    /**
     * 根据高级自定义条件更新记录
     * @param updateData 待更新实体
     * @param searchBeans 高级自定义条件
     * @return
     */
    int updateByConditions(T updateData, List<SearchBean> searchBeans);

    /**
     * 获取实体对应数据库表的序列值
     * @param modelClass 实体类型
     * @return
     */
    Long selectSequence(Class<T> modelClass);

    /**
     * 直接调用sql 返回map映射集合
     * @param
     * @return
     */
    List<Map<String,Object>> getObjectBySql(String sql);

}
