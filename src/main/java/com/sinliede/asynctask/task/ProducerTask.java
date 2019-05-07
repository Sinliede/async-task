package com.sinliede.asynctask.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * tasks that do producing results
 *
 * @author sinliede
 * @date 19-4-16 下午4:26
 * @desc
 */
public class ProducerTask<T> extends AbstractTask<T> {

    AtomicInteger producedCount;

    CountDownLatch countDownLatch;

    Producer<T> producer;

    public ProducerTask(BlockingQueue<T> resultQueue, AtomicInteger producedCount,
                        CountDownLatch countDownLatch, Producer<T> producer) {
        super(resultQueue);
        this.producedCount = producedCount;
        this.countDownLatch = countDownLatch;
        this.producer = producer;
    }

    @Override
    public void run() {
        resultQueue.offer(producer.produce());
        countDownLatch.countDown();
    }
}
