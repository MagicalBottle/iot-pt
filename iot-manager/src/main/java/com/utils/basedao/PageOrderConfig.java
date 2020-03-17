package com.utils.basedao;


/**
*   @desc : 分页实体
*   @auth : TYF
*   @data : 2019-01-25 - 14:23
*/
public class PageOrderConfig {

    //排序字段(默认id)
    private String orderField = "id";
    //排序(默认降序)
    private String orderType = "desc";
    //起始量
    private Integer limit = 20;
    //偏移量
    private Integer offset = 0;

    public String getOrderField() {
        return orderField;
    }
    public String getOrderType() { return orderType; }

    public void setOrderInfo(String orderField, String orderType) {
        this.orderType = orderType;
        this.orderField = orderField;
    }

    public Integer getLimit() { return limit; }
    public Integer getOffset() {
        return offset;
    }

    public void setPageInfo(Integer pageNum, Integer pageSize) {
        if(pageSize==null||pageNum==null){
            this.limit = null;
            this.offset = null;
        }else{
            this.limit = pageSize;
            this.offset = pageNum==0?0:(pageNum - 1) * pageSize;
        }
    }

    @Override
    public String toString() {
        return "PageOrderConfig{" +
                "orderField='" + orderField + '\'' +
                ", orderType='" + orderType + '\'' +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}