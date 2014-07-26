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
 * 为android2.3提供兼容executeOnExecutor的AsyncTask
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

    // 阻塞队列
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(10);

    // 线程池
    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

    // 按顺序执行的线程池
    public static final Executor SERIAL_EXECUTOR = new SerialExecutor();

    private static final int MESSAGE_POST_RESULT = 0x1;
    private static final int MESSAGE_POST_PROGRESS = 0x2;

    private static final InternalHandler sHandler = new InternalHandler();

    private static volatile Executor sDefaultExecutor = SERIAL_EXECUTOR;
    private final WorkerRunnable<Params, Result> mWorker;
    private final FutureTask<Result> mFuture;

    private volatile Status mStatus = Status.PENDING;// 标识此任务是否被执行过

    private final AtomicBoolean mCancelled = new AtomicBoolean();// 标识着是否是用户手动cancel还是自动执行完的
    private final AtomicBoolean mTaskInvoked = new AtomicBoolean();// 任务是否被调用完成

    /**
     * 按顺序执行的线程池
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
        // 分别为：还没被执行、正在执行、执行完毕
        PENDING, RUNNING, FINISHED,
    }

    /**
     * @hide Used to force static handler to be created.
     */
    public static void init() {
        sHandler.getLooper();
    }

    /**
     * @hide
     */
    public static void setDefaultExecutor(Executor exec) {
        sDefaultExecutor = exec;
    }

    /**
     * 构造，创建一个耗时任务，必须在UI线程中使用
     */
    public CompatibleAsyncTask() {
        mWorker = new WorkerRunnable<Params, Result>() {
            public Result call() throws Exception {
                mTaskInvoked.set(true);

                // 设置线程为后台线程
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                // noinspection unchecked
                return postResult(doInBackground(mParams));
            }
        };

        mFuture = new FutureTask<Result>(mWorker) {
            @Override
            protected void done() {
                // 无论是取消获取正常计算完成都会被调用
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

    // 如果没有被执行完成，也会调用postResult方法
    private void postResultIfNotInvoked(Result result) {
        final boolean wasTaskInvoked = mTaskInvoked.get();
        if (!wasTaskInvoked) {
            postResult(result);
        }
    }

    @SuppressWarnings("unchecked")
    private Result postResult(Result result) {
        Message message = sHandler.obtainMessage(MESSAGE_POST_RESULT, new AsyncTaskResult<Result>(this, result));
        message.sendToTarget();
        return result;
    }

    /**
     * 获取当前任务的执行情况
     * 
     * @return
     */
    public final Status getStatus() {
        return mStatus;
    }

    /**
     * 子类继承，耗时操作
     * 
     * @param params
     * @return
     */
    protected abstract Result doInBackground(Params... params);

    /**
     * 子类继承，耗时操作之前UI调用
     */
    protected void onPreExecute() {
    }

    /**
     * 子类继承，耗时操作之后UI调用
     * 
     * @param result
     */
    protected void onPostExecute(Result result) {
    }

    /**
     * 子类继承，在耗时操作内调用publishProgress方法，就会让UI调用该方法
     * 
     * @param values
     */
    protected void onProgressUpdate(Progress... values) {
    }

    /**
     * 子类继承，调用cancel之后会被调用，UI线程执行
     * 
     * @param result
     */
    protected void onCancelled(Result result) {
        onCancelled();
    }

    /**
     * 子类继承，调用cancel之后会被调用，UI线程执行
     */
    protected void onCancelled() {
    }

    /**
     * 判断任务在结束前是否被取消了，这个方法只保证cancel方法被调用了，而不保证任务是否正真的被取消了
     * 
     * @return
     */
    public final boolean isCancelled() {
        return mCancelled.get();
    }

    /**
     * 取消提交的任务
     * 
     * @param mayInterruptIfRunning
     *            如果任务已经在被执行，设置true就表示允许打断
     * @return
     */
    public final boolean cancel(boolean mayInterruptIfRunning) {
        mCancelled.set(true);
        return mFuture.cancel(mayInterruptIfRunning);
    }

    /**
     * 等待结果的返回
     * 
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public final Result get() throws InterruptedException, ExecutionException {
        return mFuture.get();
    }

    /**
     * 等待结果的返回，有过期时间
     * 
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public final Result get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
            TimeoutException {
        return mFuture.get(timeout, unit);
    }

    /**
     * 执行任务，会根据手机不同系统的版本，可能会单线程顺序执行，获取时一个线程池执行
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

    /**
     * 使用默认线程池执行一个任务
     * 
     * @param runnable
     */
    public static void execute(Runnable runnable) {
        sDefaultExecutor.execute(runnable);
    }

    /**
     * 更新UI方法
     * 
     * @param values
     */
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

    /**
     * handler实现
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
                // 这里只有一个结果
                result.mTask.finish(result.mData[0]);
                break;
            case MESSAGE_POST_PROGRESS:
                result.mTask.onProgressUpdate(result.mData);
                break;
            }
        }
    }

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

}
