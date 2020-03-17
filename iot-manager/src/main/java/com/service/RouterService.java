package com.service;


import java.util.Map;

public interface RouterService {

    Map<Integer,String> getAllOnlinePT();

    String getOneOnlinePT() throws Exception;

    String getCachedToken(Long clientId);

}
