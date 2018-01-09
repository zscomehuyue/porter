/**
 * All rights Reserved, Designed By Suixingpay.
 *
 * @author: zhangkewei[zhang_kw@suixingpay.com]
 * @date: 2017年12月20日 13:45
 * @Copyright ©2017 Suixingpay. All rights reserved.
 * 注意：本内容仅限于随行付支付有限公司内部传阅，禁止外泄以及用于其他的商业用途。
 */
package com.suixingpay.datas.common.cluster.data;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 *
 * @author: zhangkewei[zhang_kw@suixingpay.com]
 * @date: 2017年12月20日 13:45
 * @version: V1.0
 * @review: zhangkewei[zhang_kw@suixingpay.com]/2017年12月20日 13:45
 */
public class DTaskStat  extends DObject {
    private String taskId;
    private String nodeId;
    private String topic;
    //final用于保证不可变状态，同时保证多线程内存可见性
    private final AtomicLong insertRow = new AtomicLong(0);
    private final AtomicLong updateRow = new AtomicLong(0);
    private final AtomicLong deleteRow = new AtomicLong(0);
    private final AtomicLong errorUpdateRow = new AtomicLong(0);
    private final AtomicLong errorInsertRow = new AtomicLong(0);
    private final AtomicLong errorDeleteRow = new AtomicLong(0);
    private Date statedTime;
    private Date lastCheckedTime;
    private Date lastLoadedTime;
    private final AtomicLong alertedTimes = new AtomicLong(0);
    @JSONField(serialize = false, deserialize = false)
    private final AtomicBoolean updateStat = new AtomicBoolean(false);


    //处理进度,如果是mq就是topic的消费进度
    private String progress;
    public DTaskStat() {
        statedTime = new Date();
    }
    public DTaskStat(String taskId, String nodeId, String topic) {
        this();
        this.taskId = taskId;
        this.nodeId = nodeId;
        this.topic = topic;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }




    public Date getLastCheckedTime() {
        return lastCheckedTime;
    }

    public AtomicLong getAlertedTimes() {
        return alertedTimes;
    }


    public Date getLastLoadedTime() {
        return lastLoadedTime;
    }

    public void setLastLoadedTime(Date lastLoadedTime) {
        this.lastLoadedTime = lastLoadedTime;
    }

    public void setLastCheckedTime(Date lastCheckedTime) {
        this.lastCheckedTime = lastCheckedTime;
    }

    @Override
    public <T> void merge(T data) {
        DTaskStat stat = (DTaskStat) data;
        if (taskId.equals(stat.getTaskId()) && stat.getTopic().equals(topic)) {
            if (!StringUtils.isBlank(stat.nodeId)) this.nodeId = stat.nodeId;
            if (!StringUtils.isBlank(stat.progress)) this.progress = stat.progress;
            this.deleteRow.addAndGet(stat.deleteRow.longValue());
            this.insertRow.addAndGet(stat.insertRow.longValue());
            this.updateRow.addAndGet(stat.updateRow.longValue());
            this.errorDeleteRow.addAndGet(stat.errorDeleteRow.longValue());
            this.errorInsertRow.addAndGet(stat.errorInsertRow.longValue());
            this.errorUpdateRow.addAndGet(stat.errorUpdateRow.longValue());
            this.alertedTimes.addAndGet(stat.alertedTimes.longValue());
            this.statedTime = new Date();
            if (null != stat.lastLoadedTime) this.lastLoadedTime = stat.lastLoadedTime;
            if (null != stat.lastCheckedTime) this.lastCheckedTime = stat.lastCheckedTime;
        }
    }
    public void reset() {
        this.deleteRow.set(0);
        this.insertRow.set(0);
        this.updateRow.set(0);
        this.errorDeleteRow.set(0);
        this.errorInsertRow.set(0);
        this.errorUpdateRow.set(0);
        this.alertedTimes.set(0);
    }

    public AtomicBoolean getUpdateStat() {
        return updateStat;
    }

    public Date getStatedTime() {
        return statedTime;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }


    public synchronized void incrementInsertRow() {
        insertRow.incrementAndGet();
    }


    public synchronized void incrementUpdateRow() {
        updateRow.incrementAndGet();
    }


    public synchronized void incrementDeleteRow() {
        deleteRow.incrementAndGet();
    }

    public synchronized void incrementErrorUpdateRow() {
        errorUpdateRow.incrementAndGet();
    }

    public synchronized void incrementErrorInsertRow() {
        errorInsertRow.incrementAndGet();
    }

    public synchronized void incrementErrorDeleteRow() {
        errorDeleteRow.incrementAndGet();
    }

    /**
     * 仅用于JSON转化注入
     * 状态会被多线程访问，字段变更通过incrementInsertRow进行
     * @see TaskWorker.start()
     * @return
     */
    public AtomicLong getInsertRow() {
        return insertRow;
    }

    /**
     * 仅用于JSON转化注入
     * 状态会被多线程访问，字段变更通过incrementUpdateRow进行
     * @see TaskWorker.start()
     * @return
     */
    public AtomicLong getUpdateRow() {
        return updateRow;
    }

    /**
     * 仅用于JSON转化注入
     * 状态会被多线程访问，字段变更通过incrementDeleteRow进行
     * @see TaskWorker.start()
     * @return
     */
    public AtomicLong getDeleteRow() {
        return deleteRow;
    }

    /**
     * 仅用于JSON转化注入
     * 状态会被多线程访问，字段变更通过incrementErrorUpdateRow进行
     * @see TaskWorker.start()
     * @return
     */
    public AtomicLong getErrorUpdateRow() {
        return errorUpdateRow;
    }

    /**
     * 仅用于JSON转化注入
     * 状态会被多线程访问，字段变更通过incrementInsertRow进行
     * @see TaskWorker.start()
     * @return
     */
    public AtomicLong getErrorInsertRow() {
        return errorInsertRow;
    }

    /**
     * 仅用于JSON转化注入
     * 状态会被多线程访问，字段变更通过incrementErrorDeleteRow进行
     * @see TaskWorker.start()
     * @return
     */
    public AtomicLong getErrorDeleteRow() {
        return errorDeleteRow;
    }
}
