package com.utils.basedao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;


/**
*   @desc : 自定义JdbcTemplate包装
*   @auth : TYF
*   @data : 2019-01-25 - 14:55
*/
@Component
@ComponentScan(basePackages={"com.utils.basedao"})
public class JdbcTemplateMapper extends JdbcTemplate implements BaseMapper {

    @Autowired
    private GeneratorSql getSql ;//sql语句生成工具

    /**
    *   @desc : 注入beanNamedata=Source
    *   @auth : TYF
    *   @data : 2019-01-25 - 15:02
    */
    @Autowired
    @Override
    public void setDataSource(@Qualifier("dataSource")DataSource dataSource) {
        super.setDataSource(dataSource);
    }


    @Override
    public List<Map<String, Object>> selectByConditions(List<String> selective, String tableName, Map<String, Object> params,
                                                        PageOrderConfig pageOrderConfig, List<SearchBean> searchBeans) {
        Map<String,Object> preSqlMap = getSql.selectByConditions(selective,tableName,params,pageOrderConfig,searchBeans);
        if(preSqlMap!=null){
            String sql =(String) preSqlMap.get("sql");
            logger.info("Search:"+preSqlMap);
            List<Object> values = (List<Object>)preSqlMap.get("values");
            Object[] objects = null;
            if(params != null){
                objects = new Object[params.size()];
                objects = values.toArray(objects);
            }
            return queryForList(sql,objects);
        }

        return null;
    }

    @Override
    public int selectCountByConditions(String tableName, Map<String, Object> params, List<SearchBean> searchBeans) {
        Map<String,Object> preSqlMap = getSql.selectCountByConditions(tableName,params,searchBeans);
        if(preSqlMap!=null) {
            String sql = (String) preSqlMap.get("sql");
            List<Object> values = (List<Object>)preSqlMap.get("values");
            Object[] objects = null;
            if(params != null){
                objects = new Object[params.size()];
                objects = values.toArray(objects);
            }
            logger.info("Search:"+preSqlMap);
            return queryForObject(sql,objects, Integer.class);
        }
        return 0;
    }

    @Override
    public Map<String, Object> selectByPrimaryKey(List<String> selective, String tableName, Long id) {
        Map<String,Object> preSqlMap = getSql.selectByPrimaryKey(selective,tableName,id);
        if(preSqlMap!=null) {
            String sql = (String) preSqlMap.get("sql");
            List<Object> values = (List<Object>)preSqlMap.get("values");
            Object[] objects = null;
            if(selective != null){
                objects = new Object[selective.size()];
                objects = values.toArray(objects);
            }
            logger.info("Search:"+preSqlMap);
            return queryForObject(sql,objects,Map.class);
        }
        return null;
    }

    @Override
    public int deleteByConditions(String tableName, Map<String, Object> params, List<SearchBean> searchBeans) {
        Map<String,Object> preSqlMap = getSql.deleteByConditions(tableName,params,searchBeans);
        return doUpdate(preSqlMap,params.size());
    }

    @Override
    public int insert(String tableName, Map<String, Object> params) {
        Map<String,Object> preSqlMap = getSql.insert(tableName,params);
        return doUpdate(preSqlMap,params.size());
    }

    @Override
    public int updateByConditions(String tableName, Map<String, Object> fields,
                                  Map<String, Object> params, List<SearchBean> searchBeans) {
        Map<String,Object> preSqlMap = getSql.updateByConditions(tableName,fields,params,searchBeans);
        logger.info("Search:"+preSqlMap);
        return doUpdate(preSqlMap,params.size());
    }

    @Override
    public int updateByPrimaryKey(String tableName, Map<String, Object> params) {
        Map<String,Object> preSqlMap = getSql.updateByPrimaryKey(tableName,params);
        return doUpdate(preSqlMap,params.size());
    }

    @Override
    public Long selectSequence(String seqName) {
        String sql = getSql.selectSequence(seqName);
        return queryForObject(sql,Long.class);
    }

    @Override
    public List<Map<String, Object>> selectObjectBySql(String sql) {
        if(sql!=null&&(sql.toUpperCase().trim().startsWith("SELECT"))&&sql.toUpperCase().trim().contains("WHERE"))
            return queryForList(sql);
        return null;
    }

    private int doUpdate(Map<String,Object> preSqlMap, int length){
        if(preSqlMap!=null) {
            String sql = (String) preSqlMap.get("sql");
            List<Object> values = (List<Object>)preSqlMap.get("values");
            Object[] objects = null;
            if(length>0){
                objects = new Object[length];
                objects = values.toArray(objects);
            }
            logger.info("Search:"+preSqlMap);
            return update(sql,objects);
        }
        return 0;
    }


}
