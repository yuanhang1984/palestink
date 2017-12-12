package library.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import library.execute.Run;

class ExecuteThread implements Runnable {
        /*
         * 线程消息打印对象
         */
        ThreadMessagePrint threadMessagePrint;

        /*
         * 命令
         */
        private String cmd;

        /*
         * 是否阻塞运行程序。true，阻塞运行；false，非阻塞运行。
         */
        private boolean isBlock;

        /*
         * 重入锁
         */
        private Lock lock;

        /*
         * 执行结束的条件
         */
        private Condition condition;

        /**
         * 构造函数
         * 
         * @param threadMessagePrint
         * @param cmd
         * @param isBlock
         * @param lock
         * @param condition
         */
        public ExecuteThread(ThreadMessagePrint threadMessagePrint, String cmd, boolean isBlock, Lock lock, Condition condition) {
                this.threadMessagePrint = threadMessagePrint;
                this.cmd = cmd;
                this.isBlock = isBlock;
                this.lock = lock;
                this.condition = condition;
        }

        @Override
        public void run() {
                // 阻塞运行程序
                try {
                        // 阻塞运行程序
                        Run.executeProgram(this.cmd, null, null, this.isBlock);
                        Block.unlock(this.lock, this.condition);
                } catch (Exception e) {
                        this.threadMessagePrint.print(e.toString());
                }
        }
}

/**
 * 多线程执行<br />
 * 用于同时开启多个线程执行一个“外部应用程序”，多线程用于提高效率，而只有isBlock设置为true的时候，才能确保所有线程下的“执行操作”全部完成。
 */
public class MultiThreadExecute {
        /*
         * 线程消息打印对象
         */
        ThreadMessagePrint threadMessagePrint;

        /*
         * 命令数组
         */
        private String[] cmd;

        /*
         * 是否阻塞运行程序。true，阻塞运行；false，非阻塞运行。
         */
        private boolean isBlock;

        /*
         * 重入锁
         */
        private Lock lock;

        /*
         * 最大开启线程数
         */
        private int maxThreadNumber;

        /**
         * 构造函数
         * 
         * @param threadMessagePrint
         * @param cmd 命令数组
         * @param isBlock 是否阻塞运行程序。true，阻塞运行；false，非阻塞运行。<br />
         *                注意：只有isBlock设置为true的情况下，函数返回时才能确保所有进程执行结束。
         * @param maxThreadNumber 最大开启线程数
         */
        public MultiThreadExecute(ThreadMessagePrint threadMessagePrint, String[] cmd, boolean isBlock, int maxThreadNumber) {
                this.threadMessagePrint = threadMessagePrint;
                this.cmd = cmd;
                this.isBlock = isBlock;
                this.lock = new ReentrantLock();
                this.maxThreadNumber = maxThreadNumber;
        }

        /**
         * 所有线程执行完毕时，方法调用结束。
         */
        public void execute() {
                ExecutorService pool = Executors.newFixedThreadPool(this.maxThreadNumber);
                Condition[] condition = new Condition[this.cmd.length];
                // 开始多线程执行
                for (int i = 0; i < this.cmd.length; i++) {
                        condition[i] = this.lock.newCondition();
                        pool.execute(new ExecuteThread(this.threadMessagePrint, this.cmd[i], this.isBlock, this.lock, condition[i]));
                }
                try {
                        for (int i = 0; i < condition.length; i++) {
                                Block.lock(this.lock, condition[i]);
                        }
                } catch (Exception e) {
                        this.threadMessagePrint.print(e.toString());
                }
        }
}