package com.service;

import com.entry.ClientTb;

public interface ClientService {

    int create(ClientTb clientTb);

    int delete(ClientTb clientTb);

    int update(ClientTb clientTb);

    int query(ClientTb clientTb);

}
