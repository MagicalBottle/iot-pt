package com.service;

import java.util.List;

public interface RouterService {

    List<String> getAllOnlinePT();

    String getOneOnlinePT() throws Exception;

    String getCachedToken(String clientId);

}
