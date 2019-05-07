package src.com.sinliede.asynctask.test;

import com.sinliede.asynctask.task.Producer;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sinliede
 *
 * @date 19-5-7 下午2:23
 * @desc
 */
public class TestProducer implements Producer<Integer> {

    private Random random = new Random();

    private static AtomicInteger producedCount = new AtomicInteger();

    @Override
    public Integer produce() {

        int waitTime = random.nextInt(200) + 300;
        //imitate time consuming task
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " produced : " + producedCount.incrementAndGet());
        return producedCount.get();
    }
}
