package com.service;


public interface PTService {

    void registryToZk(String host,int port) throws Exception;

    void clientCountReport(String host,int port,int count,int exipre) throws Exception;


}
