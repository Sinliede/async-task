package com.sinliede.asynctask.task;

import java.util.concurrent.BlockingQueue;

/**
 *
 * @author sinliede
 * @date 19-1-22 下午7:16
 * @desc
 */
public abstract class AbstractTask<T> implements Runnable{

    BlockingQueue<T> resultQueue;

    public AbstractTask(BlockingQueue<T> resultQueue) {
        this.resultQueue = resultQueue;
    }
}
