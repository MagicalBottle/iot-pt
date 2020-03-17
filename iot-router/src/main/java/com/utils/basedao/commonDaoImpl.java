package com.utils.basedao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
*   @desc : 单表通用操作类
*   @auth : TYF
*   @data : 2019-01-25 - 14:52
*/
@Repository
@ComponentScan(basePackages={"com.utils.basedao"})
public class commonDaoImpl<T> implements CommonDao<T> {


    @Autowired
    private BaseMapper baseMapper;
    @Autowired
    private OrmUtil<T> ormUtil;

    @Override
    public List<T> selectListByConditions(T conditions) {
        return selectListByConditions(null,conditions,null,new PageOrderConfig());
    }

    @Override
    public List<T> selectListByConditions(T conditions, PageOrderConfig pageOrderConfig) {
        return selectListByConditions(null,conditions,null,pageOrderConfig);
    }

    @Override
    public List<T> selectListByConditions(List<String> selective, T conditions) {
        return selectListByConditions(selective,conditions,null,new PageOrderConfig());
    }

    @Override
    public List<T> selectListByConditions(List<String> selective, T conditions, PageOrderConfig pageOrderConfig) {
        return selectListByConditions(selective,conditions,null,pageOrderConfig);
    }

    @Override
    public List<T> selectListByConditions(T conditions, List<SearchBean> searchBeans) {
        return selectListByConditions(null,conditions,searchBeans,new PageOrderConfig());
    }

    @Override
    public List<T> selectListByConditions(T conditions, List<SearchBean> searchBeans, PageOrderConfig pageOrderConfig) {
        return selectListByConditions(null,conditions,searchBeans,pageOrderConfig);
    }

    @Override
    public List<T> selectListByConditions(List<String> selective, T conditions, List<SearchBean> searchBeans) {
        return selectListByConditions(selective,conditions,searchBeans,new PageOrderConfig());
    }

    @Override
    public List<T> selectListByConditions(List<String> selective, T conditions, List<SearchBean> searchBeans, PageOrderConfig pageOrderConfig) {
        if(conditions==null){
            throw new RuntimeException("selectListByConditions: conditions can not be null!");
        }
        String tableName = ormUtil.getTableName(conditions.getClass());
        ormUtil.checkSearchBeans(searchBeans);
        Map<String, Object> params = ormUtil.pojoToMap(conditions);
        List<Map<String, Object>> list = baseMapper.selectByConditions(selective, tableName, params, pageOrderConfig, searchBeans);
        return ormUtil.getPojoList(list, (Class<T>) conditions.getClass());
    }

    @Override
    public int selectCountByConditions(T conditions) {
        return selectCountByConditions(conditions,null);
    }

    @Override
    public int selectCountByConditions(T conditions, List<SearchBean> searchBeans) {
        if(conditions==null){
            throw new RuntimeException("selectCountByConditions: conditions can not be null!");
        }
        String tableName = ormUtil.getTableName(conditions.getClass());
        Map<String, Object> params = ormUtil.pojoToMap(conditions);
        ormUtil.checkSearchBeans(searchBeans);
        return baseMapper.selectCountByConditions(tableName, params, searchBeans);
    }

    @Override
    public T selectObjectByConditions(T conditions) {
        return selectObjectByConditions(null,conditions,null);
    }

    @Override
    public T selectObjectByConditions(List<String> selective, T conditions) {
        return selectObjectByConditions(selective,conditions,null);
    }

    @Override
    public T selectObjectByConditions(T conditions, List<SearchBean> searchBeans) {
        return selectObjectByConditions(null,conditions,searchBeans);
    }

    @Override
    public T selectObjectByConditions(List<String> selective, T conditions, List<SearchBean> searchBeans) {
        if(conditions==null){
            throw new RuntimeException("selectObjectByConditions: conditions can not be null");
        }
        String tableName = ormUtil.getTableName(conditions.getClass());
        Map<String, Object> params = ormUtil.pojoToMap(conditions);
        PageOrderConfig pageOrderConfig = new PageOrderConfig();
        pageOrderConfig.setPageInfo(1,1);
        ormUtil.checkSearchBeans(searchBeans);
        List<Map<String, Object>> list = baseMapper.selectByConditions(selective, tableName, params, pageOrderConfig, searchBeans);
        if(list!=null&&list.size()>0){
            List<T> pojoList = ormUtil.getPojoList(list, (Class<T>) conditions.getClass());
            return pojoList.get(0);
        }
        return null;
    }

    @Override
    public T selectObjectByPrimaryKey(Long id, Class<T> modelClass) {
        return selectObjectByPrimaryKey(null,id,modelClass);
    }

    @Override
    public T selectObjectByPrimaryKey(List<String> selective, Long id, Class<T> modelClass) {
        String tableName = ormUtil.getTableName(modelClass);
        Map<String, Object> map = baseMapper.selectByPrimaryKey(selective, tableName, id);
        if(map!=null){
            try {
                T t = modelClass.newInstance();
                ormUtil.mapToPojo(map,t);
                return t;
            } catch (InstantiationException |IllegalAccessException e) {
                throw new RuntimeException("Create Pojo Error!");
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public int insert(T insertData) {
        String tableName = ormUtil.getTableName(insertData.getClass());
        Map<String, Object> map = ormUtil.pojoToMap(insertData);
        return baseMapper.insert(tableName, map);
    }

    @Override
    public int deleteByConditions(T conditions) {
        return deleteByConditions(conditions,null);
    }

    @Override
    public int deleteByConditions(T conditions, List<SearchBean> searchBeans) {
        if(conditions==null){
            throw new RuntimeException("deleteByConditions: conditions can not be null");
        }
        String tableName = ormUtil.getTableName(conditions.getClass());
        Map<String, Object> map = ormUtil.pojoToMap(conditions);
        if((map==null||map.isEmpty())&&(searchBeans==null||searchBeans.isEmpty())){//两者都为空
            throw new RuntimeException("deleteByConditions: Delete conditions can not be null!");
        }
        ormUtil.checkSearchBeans(searchBeans);
        return baseMapper.deleteByConditions(tableName,map,searchBeans);
    }

    @Override
    public int updateByPrimaryKey(T updateData) {
        if(updateData==null){
            throw new RuntimeException("deleteByConditions: conditions can not be null");
        }
        String tableName = ormUtil.getTableName(updateData.getClass());
        Map<String, Object> map = ormUtil.pojoToMap(updateData);
        if(map==null||map.isEmpty()){
            throw new RuntimeException("updateByPrimaryKey: Update conditions can not be null!");
        }
        Object id = map.get("id");
        if(id==null){
            throw new RuntimeException("updateByPrimaryKey: PrimaryKey can not be null!");
        }
        return baseMapper.updateByPrimaryKey(tableName,map);
    }

    @Override
    public int updateByConditions(T updateData, T conditions) {
        return updateByConditions(updateData,conditions,null);
    }

    @Override
    public int updateByConditions(T updateData, T conditions, List<SearchBean> searchBeans) {
        System.out.println("123");
        if(conditions==null){
            throw new RuntimeException("updateByConditions: conditions can not be null");
        }
        String tableName = ormUtil.getTableName(conditions.getClass());
        Map<String, Object> fields = ormUtil.pojoToMap(updateData);
        if(fields==null||fields.isEmpty()){
            throw new RuntimeException("updateByConditions: UpdateData is null, nothing will be update!");
        }
        Map<String, Object> params = ormUtil.pojoToMap(conditions);
        if((params==null||params.isEmpty())&&(searchBeans==null||searchBeans.isEmpty())){
            throw new RuntimeException("updateByConditions: Update conditions can not be null!");
        }
        ormUtil.checkSearchBeans(searchBeans);
        return baseMapper.updateByConditions(tableName,fields,params,searchBeans);
    }

    @Override
    public int updateByConditions(T updateData, List<SearchBean> searchBeans) {
        return updateByConditions(updateData,null,searchBeans);
    }

    @Override
    public Long selectSequence(Class<T> modelClass) {
        String tableName = ormUtil.getTableName(modelClass);
        return baseMapper.selectSequence(tableName+"_sequence");
    }

    @Override
    public List<Map<String,Object>> getObjectBySql(String sql) {
        return baseMapper.selectObjectBySql(sql);
    }
}
