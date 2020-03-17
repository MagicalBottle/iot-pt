package com.entry;

/**
*   @desc : 客户端表
*   @auth : TYF
*   @date : 2020-03-17 - 10:30
*/
public class ClientTb {

    Long id;

    Long lastLoginTime;//上次登陆时间

    Integer type;//客户端类型  -1无 1移动app 2iot设备(相机,小票机,销售柜) 3桌面app 等

    Integer state;//状态

    String name;//客户端名称

    String remark;//备注


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
