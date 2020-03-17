package com.utils;

import java.util.Date;

/**
*   @desc : 雪花id生成器
*   @auth : TYF
*   @date : 2019-07-10 - 13:38
*/
public  class SnowFlakeIdWorker {

    //起始时间
    private final long twepoch = 0;
    //机器Id 占用的位数：5
    private final long workerIdBits = 0L;
    //数据标识Id 占用的位数：5
    private final long datacenterIdBits = 0L;
    //机器Id 最大值：31
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    //数据标识Id 最大值：31
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    //序列号占用的位数：12
    private final long sequenceBits = 12L;
    //机器Id左移12位
    private final long workerIdShift = sequenceBits;
    //数据标识Id左移17位
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    //时间戳左移22位
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    //4095,序列号范围
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    //机器Id
    private long workerId;
    //数据中心Id
    private long datacenterId;
    //毫秒内序列(0-4095)
    private long sequence = 0L;
    //生词生成Id的时间戳
    private long lastTimestamp = -1L;

    //构造器
    private SnowFlakeIdWorker(){

    }
    private SnowFlakeIdWorker(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    //获得下一个ID
    public synchronized long nextId() {
        long timestamp = timeGen();
        //防止系统时间回退
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        //同一个毫秒时间戳,说明都在一个毫秒内生成的
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;//加1
            //4095用完,则丢弃timeGen()生成的时间戳,用tilNextMillis()生成的下一个毫秒内的时间戳
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        //更新
        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    //阻塞到下一个毫秒，直到获得新的时间戳
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    //获取当前时间戳
    protected long timeGen() {
        return System.currentTimeMillis();
    }
    //如果分布式需要自定义机器号new SnowFlakeIdWorker(1, 1)
    private final static SnowFlakeIdWorker idWorker= new SnowFlakeIdWorker();
    public static SnowFlakeIdWorker getInstance(){
        return idWorker;
    }

    public static void main(String[] args) {
        //如果 机器号5位/机器数据5位
        long id = 0L;
        //-----------------时间戳------------------|-机器号-|-机器号-|-0到4095序列-
        //10110011101011110001101001000100011100101| 00001 | 00001 | 000000011101
        //时间戳
        System.out.println("二进制:" + (Long.toBinaryString(id)));
        for (int i = 0; i < 5000; i++) {
            id = SnowFlakeIdWorker.getInstance().nextId();
            System.out.println("long:" + id + ",time:" + new Date((id >> 22)) + ",timeStamp:" + (id >> 22));
        }
    }

}
