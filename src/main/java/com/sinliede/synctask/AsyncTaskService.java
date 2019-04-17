package com.sinliede.synctask;

import com.sinliede.synctask.task.Consumer;
import com.sinliede.synctask.task.ConsumerTask;
import com.sinliede.synctask.task.Producer;
import com.sinliede.synctask.task.ProducerTask;


import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author sinliede
 * @date 19-4-16 下午5:02
 * @desc
 */

public class AsyncTaskService<T> {

    //total task counts to execute, default value is maximum int value
    private int totalLimit = Integer.MAX_VALUE;

    private int producerCoreThreads = 1;

    private int producerMaxThreads = 1;

    //for purpose of preventing OOM, default 10000
    private int queuedProducerTaskLimit = 10000;

    private ThreadPoolExecutor threadPoolExecutor;


    private CountDownLatch countDownLatch;

    private BlockingQueue<T> resultQueue;

    private Consumer<T> consumer;

    private AtomicInteger producedCount;

    private AtomicInteger consumedCount;

    public AsyncTaskService(int totalLimit, int producerCoreThreads, int producerMaxThreads, int queuedProducerTaskLimit, Consumer<T> consumer) {
        if (totalLimit < 1 || producerCoreThreads < 1 || producerMaxThreads < 1 || queuedProducerTaskLimit < 1)
            throw new IllegalArgumentException("properties could not be less than 1");
        this.totalLimit = totalLimit;
        this.producerCoreThreads = producerCoreThreads;
        this.producerMaxThreads = producerMaxThreads;
        this.queuedProducerTaskLimit = queuedProducerTaskLimit;

        this.countDownLatch = new CountDownLatch(totalLimit);
        this.threadPoolExecutor = new RetryableThreadPoolExecutor(producerCoreThreads, producerMaxThreads, 0L,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(queuedProducerTaskLimit));
        this.resultQueue = new LinkedBlockingQueue<>();
        this.producedCount = new AtomicInteger();
        this.consumedCount = new AtomicInteger();

        startConsumer();
    }

    /**
     * submit producer task
     * @param producer
     */
    public void submitTask(Producer<T> producer) {
        ProducerTask<T> producerTask = new ProducerTask<>(resultQueue, totalLimit, producedCount, countDownLatch, producer);
        threadPoolExecutor.execute(producerTask);
    }

    private void startConsumer() {
        ConsumerTask<T> consumerTask = new ConsumerTask<>(resultQueue, totalLimit, producedCount, consumedCount, consumer);
        new Thread(consumerTask).start();
    }
}
