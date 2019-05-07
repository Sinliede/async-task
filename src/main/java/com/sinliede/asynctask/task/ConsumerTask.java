package com.sinliede.asynctask.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author sinliede
 * @date 19-4-16 下午4:38
 * @desc
 */
public class ConsumerTask<T> extends AbstractTask<T> {

    AtomicInteger producedCount;

    AtomicInteger consumedCount;

    int totalLimit;

    Consumer<T> consumer;

    public ConsumerTask(BlockingQueue<T> resultQueue, int totalLimit, AtomicInteger producedCount,
                        AtomicInteger consumedCount, Consumer<T> consumer) {
        super(resultQueue);
        this.totalLimit = totalLimit;
        this.producedCount = producedCount;
        this.consumedCount = consumedCount;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        do {
            try {
                T t = resultQueue.take();
                consumer.consume(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (consumedCount.incrementAndGet() < totalLimit || !resultQueue.isEmpty());
    }
}
