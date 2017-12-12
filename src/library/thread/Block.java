package library.thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;

/**
 * 调用Demo<br />
 * 1、声明两个对象<br />
 * Lock lock = new ReentrantLock();<br />
 * Condition condition = lock.newCondition();<br />
 * 2、调用Block.lock(lock, condition);锁定。
 * 3、调用Block.unlock(lock, condition);解锁。
 */

/**
 * 线程阻塞
 */
public class Block {
        /**
         * 锁定
         * 
         * @param lock 可重入锁
         * @param condition 条件
         */
        public static void lock(Lock lock, Condition condition) throws Exception {
                // 防止并发调用，首先锁定。
                lock.lock();
                try {
                        condition.await();
                } finally {
                        // 条件设置结束，解除锁定。
                        lock.unlock();
                }
        }

        /**
         * 解锁
         * 
         * @param lock 可重入锁
         * @param condition 条件
         */
        public static void unlock(Lock lock, Condition condition) {
                // 防止并发调用，首先锁定。
                lock.lock();
                try {
                        condition.signal();
                } finally {
                        // 条件设置结束，解除锁定。
                        lock.unlock();
                }
        }
}