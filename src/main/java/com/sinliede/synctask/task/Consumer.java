package com.sinliede.synctask.task;

/**
 *
 * @author sinliede
 * @date 19-4-16 下午6:05
 * @desc
 */
public interface Consumer<T> {

    void consume(T t);
}
