package src.com.sinliede.asynctask.test;

import com.sinliede.asynctask.task.Consumer;


/**
 * @author sinliede
 *
 * @date 19-5-7 下午2:30
 * @desc
 */
public class TestConsumer implements Consumer<Integer> {

    @Override
    public void consume(Integer integer) {
        System.out.println("consumed : " + integer);
    }
}
