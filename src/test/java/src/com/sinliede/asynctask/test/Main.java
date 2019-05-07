package src.com.sinliede.asynctask.test;

import com.sinliede.asynctask.AsyncTaskService;
import com.sinliede.asynctask.task.Producer;

/**
 * @author sinliede
 *
 * @date 19-5-7 下午2:34
 * @desc
 */
public class Main {
    public static void main(String[] args) {
        AsyncTaskService<Integer> asyncTaskService = new AsyncTaskService<>(10000, 10, 20, 1000, new TestConsumer());
        Producer<Integer> producer = new TestProducer();
        int i = 0;
        for (; i < 11000; i++) {
            asyncTaskService.submitTask(producer);
            System.out.println("iterate : " + i);
        }
    }
}
