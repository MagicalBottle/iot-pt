package com.service.impl;

import com.entry.ClientTb;
import com.service.ClientService;
import com.utils.basedao.CommonDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements ClientService{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CommonDao commonDao;


    @Override
    public int create(ClientTb clientTb) {
        return commonDao.insert(clientTb);
    }

    @Override
    public int delete(ClientTb clientTb) {
        return commonDao.deleteByConditions(clientTb);
    }

    @Override
    public int update(ClientTb clientTb) {
        return commonDao.updateByPrimaryKey(clientTb);
    }

    @Override
    public int query(ClientTb clientTb) {
        return 0;
    }
}
