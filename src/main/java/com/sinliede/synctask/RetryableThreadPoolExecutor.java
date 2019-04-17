package com.sinliede.synctask;

import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

/**
 * A ThreadPoolExecutor that could block task producing threads when pool is full and retry to submit the rejected tasks
 * when pool is available for more tasks
 *
 * @author sinliede
 * @date 19-4-16 上午9:42
 * @desc
 */
public class RetryableThreadPoolExecutor extends ThreadPoolExecutor {

    BlockingQueue<Thread> rejectedTaskQueue = new LinkedBlockingQueue<>();

    public RetryableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        setRejectedExecutionHandler(new RetryRejectExecutionHandler());
    }

    public RetryableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        setRejectedExecutionHandler(new RetryRejectExecutionHandler());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        if (getQueue().remainingCapacity() == 0 && !rejectedTaskQueue.isEmpty()) {
            Thread thread = rejectedTaskQueue.poll();
            if (null != thread) {
                LockSupport.unpark(thread);
            }
        }
    }

    class RetryRejectExecutionHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            Thread thread = Thread.currentThread();
            rejectedTaskQueue.offer(thread);
            LockSupport.park(thread);
            executor.execute(r);
        }
    }
}
