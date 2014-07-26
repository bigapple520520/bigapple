package com.winupon.andframe.bigapple.bitmap;

import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Handler;
import android.os.Message;
import android.os.Process;

import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 为android2.3之前的版本提供一个兼容executeOnExecutor的AsyncTask
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-22 下午6:28:18 $
 */
public abstract class CompatibleAsyncTask<Params, Progress, Result> {
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 128;
    private static final int KEEP_ALIVE = 1;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "CompatibleAsyncTask #" + mCount.getAndIncrement());
        }
    };

    /**
     * 默认线程池使用的阻塞队列
     */
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(10);

    /**
     * 默认线程池
     */
    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

    /**
     * 顺序单个执行线程池
     */
    public static final Executor SERIAL_EXECUTOR = new SerialExecutor();

    private static final int MESSAGE_POST_RESULT = 0x1;
    private static final int MESSAGE_POST_PROGRESS = 0x2;

    private static final InternalHandler sHandler = new InternalHandler();

    private static volatile Executor sDefaultExecutor = SERIAL_EXECUTOR;

    private final WorkerRunnable<Params, Result> mWorker;
    private final FutureTask<Result> mFuture;

    private volatile Status mStatus = Status.PENDING;// 标识此任务是否被执行过

    private final AtomicBoolean mCancelled = new AtomicBoolean();// 标识着是否是用户手动cancel还是自动执行完的
    private final AtomicBoolean mTaskInvoked = new AtomicBoolean();// 任务是否被调用执行

    /**
     * 构造，创建一个耗时任务，必须在UI线程中使用
     */
    public CompatibleAsyncTask() {
        mWorker = new WorkerRunnable<Params, Result>() {
            public Result call() throws Exception {
                mTaskInvoked.set(true);

                // 设置线程为后台线程
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                return postResult(doInBackground(mParams));
            }
        };

        mFuture = new FutureTask<Result>(mWorker) {
            @Override
            protected void done() {// done无论是取消获取正常计算完成都会被调用
                try {
                    postResultIfNotInvoked(get());
                }
                catch (InterruptedException e) {
                    LogUtils.w(e);
                }
                catch (ExecutionException e) {
                    throw new RuntimeException("An error occured while executing doInBackground()", e.getCause());
                }
                catch (CancellationException e) {
                    postResultIfNotInvoked(null);
                }
            }
        };
    }

    // /////////////////////////////////////////////外部使用方法//////////////////////////////////////////////////////
    /**
     * 按默认线程池，任务按顺序一个一个执行
     * 
     * @param params
     * @return
     */
    public final CompatibleAsyncTask<Params, Progress, Result> execute(Params... params) {
        return executeOnExecutor(sDefaultExecutor, params);
    }

    /**
     * 指定线程池执行
     * 
     * @param exec
     * @param params
     * @return
     */
    public final CompatibleAsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec, Params... params) {
        if (mStatus != Status.PENDING) {
            switch (mStatus) {
            case RUNNING:
                throw new IllegalStateException("Cannot execute task:" + " the task is already running.");
            case FINISHED:
                throw new IllegalStateException("Cannot execute task:" + " the task has already been executed "
                        + "(a task can be executed only once)");
            default:
                break;
            }
        }

        mStatus = Status.RUNNING;

        onPreExecute();

        mWorker.mParams = params;
        exec.execute(mFuture);

        return this;
    }

    public static void execute(Runnable runnable) {
        sDefaultExecutor.execute(runnable);
    }

    public final boolean isCancelled() {
        return mCancelled.get();
    }

    public final boolean cancel(boolean mayInterruptIfRunning) {
        mCancelled.set(true);
        return mFuture.cancel(mayInterruptIfRunning);
    }

    public final Result get() throws InterruptedException, ExecutionException {
        return mFuture.get();
    }

    public final Result get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
            TimeoutException {
        return mFuture.get(timeout, unit);
    }

    public final Status getStatus() {
        return mStatus;
    }

    public static void init() {
        sHandler.getLooper();
    }

    public static void setDefaultExecutor(Executor exec) {
        sDefaultExecutor = exec;
    }

    // /////////////////////////////////////////////需要被继承方法//////////////////////////////////////////////////////
    /**
     * 耗时操作
     * 
     * @param params
     * @return
     */
    protected abstract Result doInBackground(Params... params);

    /**
     * 耗时操作前UI调用
     */
    protected void onPreExecute() {
    }

    /**
     * 耗时操作后UI调用
     * 
     * @param result
     */
    protected void onPostExecute(Result result) {
    }

    /**
     * 耗时操作中UI更新
     * 
     * @param values
     */
    protected void onProgressUpdate(Progress... values) {
    }

    /**
     * 取消UI调用，手动取消才会调用
     * 
     * @param result
     */
    protected void onCancelled(Result result) {
        onCancelled();
    }

    /**
     * 取消UI调用，手动取消才会调用
     */
    protected void onCancelled() {
    }

    // /////////////////////////////////////////////内部方法///////////////////////////////////////////////////////////
    protected final void publishProgress(Progress... values) {
        if (!isCancelled()) {
            sHandler.obtainMessage(MESSAGE_POST_PROGRESS, new AsyncTaskResult<Progress>(this, values)).sendToTarget();
        }
    }

    private void finish(Result result) {
        if (isCancelled()) {
            onCancelled(result);
        }
        else {
            onPostExecute(result);
        }
        mStatus = Status.FINISHED;
    }

    private void postResultIfNotInvoked(Result result) {
        final boolean wasTaskInvoked = mTaskInvoked.get();
        if (!wasTaskInvoked) {
            postResult(result);
        }
    }

    private Result postResult(Result result) {
        Message message = sHandler.obtainMessage(MESSAGE_POST_RESULT, new AsyncTaskResult<Result>(this, result));
        message.sendToTarget();
        return result;
    }

    // /////////////////////////////////////////////内部类//////////////////////////////////////////////////////////////
    /**
     * Callable表示任务可有返回值
     * 
     * @author xuan
     * @version $Revision: 1.0 $, $Date: 2013-9-22 下午8:08:17 $
     */
    private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
        Params[] mParams;// 执行参数
    }

    /**
     * 异步任务类结果封装
     * 
     * @author xuan
     * @version $Revision: 1.0 $, $Date: 2013-9-22 下午7:46:25 $
     */
    private static class AsyncTaskResult<Data> {
        final CompatibleAsyncTask mTask;
        final Data[] mData;

        AsyncTaskResult(CompatibleAsyncTask task, Data... data) {
            mTask = task;
            mData = data;
        }
    }

    /**
     * handler实现，与UI交互
     * 
     * @author xuan
     * @version $Revision: 1.0 $, $Date: 2013-9-22 下午7:48:24 $
     */
    private static class InternalHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            AsyncTaskResult result = (AsyncTaskResult) msg.obj;
            switch (msg.what) {
            case MESSAGE_POST_RESULT:
                result.mTask.finish(result.mData[0]);
                break;
            case MESSAGE_POST_PROGRESS:
                result.mTask.onProgressUpdate(result.mData);
                break;
            }
        }
    }

    /**
     * 按顺序执行的线程池，但本质还是提交给THREAD_POOL_EXECUTOR执行
     * 
     * @author xuan
     * @version $Revision: 1.0 $, $Date: 2013-9-22 下午6:59:45 $
     */
    private static class SerialExecutor implements Executor {
        // 双向队列
        final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
        Runnable mActive;

        public synchronized void execute(final Runnable r) {
            mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    }
                    finally {
                        scheduleNext();
                    }
                }
            });

            if (mActive == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                THREAD_POOL_EXECUTOR.execute(mActive);
            }
        }
    }

    /**
     * 任务的执行状态
     * 
     * @author xuan
     * @version $Revision: 1.0 $, $Date: 2013-9-22 下午7:10:47 $
     */
    public enum Status {
        // 还未执行、执行中、执行完毕
        PENDING, RUNNING, FINISHED,
    }

}
