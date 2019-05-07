package com.sinliede.asynctask;

import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

/**
 * A ThreadPoolExecutor that could block task producing threads when pool is full and retry to submit the rejected tasks
 * when pool is available for more tasks.
 *
 * @author sinliede
 * @date 19-4-16 上午9:42
 * @desc
 */
public class RetryThreadPoolExecutor extends ThreadPoolExecutor {

    //since this queue stores blocked producer threads, it is unnecessary to give a maximum queue size
    private BlockingQueue<Thread> rejectedTaskQueue = new LinkedBlockingQueue<>();

    private CountdownListener countdownListener;

    public RetryThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        setRejectedExecutionHandler(new RetryRejectExecutionHandler());
        countdownListener = new ThreadPoolCountdownListener(this);
    }

    public RetryThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.setThreadFactory(threadFactory);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (getQueue().remainingCapacity() != 0 && !rejectedTaskQueue.isEmpty()) {
            Thread thread = rejectedTaskQueue.poll();
            if (null != thread) {
                LockSupport.unpark(thread);
            }
        }
    }

    interface CountdownListener {

        void listenCountdown(CountDownLatch countDownLatch);
    }

    class ThreadPoolCountdownListener implements CountdownListener {

        private ThreadPoolExecutor threadPoolExecutor;

        public ThreadPoolCountdownListener(ThreadPoolExecutor threadPoolExecutor) {
            this.threadPoolExecutor = threadPoolExecutor;
        }

        @Override
        public void listenCountdown(CountDownLatch countDownLatch) {
            new Thread(() -> {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                threadPoolExecutor.shutdown();
            }).start();
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

    public CountdownListener getCountdownListener() {
        return countdownListener;
    }

    public RetryThreadPoolExecutor setCountdownListener(CountdownListener countdownListener) {
        this.countdownListener = countdownListener;
        return this;
    }

    public BlockingQueue<Thread> getRejectedTaskQueue() {
        return rejectedTaskQueue;
    }

    public RetryThreadPoolExecutor setRejectedTaskQueue(BlockingQueue<Thread> rejectedTaskQueue) {
        this.rejectedTaskQueue = rejectedTaskQueue;
        return this;
    }
}
