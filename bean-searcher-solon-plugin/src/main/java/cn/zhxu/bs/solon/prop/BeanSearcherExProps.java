package cn.zhxu.bs.solon.prop;

import java.time.Duration;

public class BeanSearcherExProps {

    /**
     * 默认每批次查询的条数，默认 1000
     */
    private int batchSize = 1000;

    /**
     * 每批次查询后的延迟时间，默认 100毫秒，用于降低数据库压力
     */
    private Duration batchDelay = Duration.ofMillis(100);

    /**
     * 最大同时导出的并发数，当同时导出操作的人达到这个值（默认 10）后，新导出的人会处于等待状态
     */
    private int maxExportingThreads = 10;

    /**
     * 最大线程数，当同时导出操作的人太多（默认 30），将不再接受新的导出（新导出的人会收到稍后操作的提示，或抛出异常）
     */
    private int maxThreads = 30;

    /**
     * 文件名是否包含时间戳，默认是
     */
    private boolean timestampFilename = true;

    /**
     * 导出人数太多时返回的提示信息，默认是 "大人请息怒，当前导出数据的人实在太多了，请稍后再试一下子哈！"
     */
    private String tooManyRequestsMessage = "大人请息怒，当前导出数据的人实在太多了，请稍后再试一下子哈！";

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public Duration getBatchDelay() {
        return batchDelay;
    }

    public void setBatchDelay(Duration batchDelay) {
        this.batchDelay = batchDelay;
    }

    public int getMaxExportingThreads() {
        return maxExportingThreads;
    }

    public void setMaxExportingThreads(int maxExportingThreads) {
        this.maxExportingThreads = maxExportingThreads;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public boolean isTimestampFilename() {
        return timestampFilename;
    }

    public void setTimestampFilename(boolean timestampFilename) {
        this.timestampFilename = timestampFilename;
    }

    public String getTooManyRequestsMessage() {
        return tooManyRequestsMessage;
    }

    public void setTooManyRequestsMessage(String tooManyRequestsMessage) {
        this.tooManyRequestsMessage = tooManyRequestsMessage;
    }

}
