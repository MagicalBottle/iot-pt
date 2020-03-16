package com.service;

import java.util.List;

public interface RouterService {

    List<String> getAllOnlinePT() throws Exception;

    String getOneOnlinePT() throws Exception;

}
