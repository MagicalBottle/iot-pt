package com.excep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;


/**
*   @desc : 消息处理线程池繁忙
*   @auth : TYF
*   @date : 2020/3/19 - 21:23
*/
public class MsgExecutorBusyException implements RejectedExecutionHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        logger.info("消息被丢弃,请根据cpu和内存调整客户端数量上限,客户端消息限流,线程池大小,任务队列长度");
    }
}
