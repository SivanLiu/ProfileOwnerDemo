package com.profileownerdemo;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lyg on 2018/6/17.
 */
public class MyHandler {
    private static final String TAG = "MyHandler";

    public final void open() {
        getReal().open();
    }

    public final void close() {
        getReal().close();
    }

    protected void handleMessage(Message message) {
    }

    private Handler handler = null;

    public MyHandler() {
        handler = new MyHandler.InnerHandler(this);
    }

    public MyHandler(Looper looper) {
        handler = new MyHandler.InnerHandler(this, looper);
    }

    public Handler get() {
        return handler;
    }

    private InnerHandler getReal() {
        return ((InnerHandler) get());
    }

    private static class InnerHandler extends Handler {

        private WeakReference<MyHandler> mOuter;
        private volatile AtomicBoolean close = new AtomicBoolean(false);

        public InnerHandler(MyHandler callback) {
            super();
            mOuter = new WeakReference<>(callback);
        }

        public InnerHandler(MyHandler callback, Looper looper) {
            super(looper);
            mOuter = new WeakReference<>(callback);
        }

        @Override
        public final void handleMessage(Message msg) {
            synchronized (this) {
                if (close.get()) {
                    Log.i(TAG, "handleMessage:close = " + close.get());
                    return;
                }
                MyHandler outer = mOuter.get();
                if (outer != null) {
                    outer.handleMessage(msg);
                }
            }
        }

        private final void open() {
            synchronized (this) {
                close.set(false);
            }
        }

        private final void close() {
            synchronized (this) {
                close.set(true);
            }
            this.removeCallbacksAndMessages(null);
        }

        @Override
        public final void dispatchMessage(Message msg) {
            if (close.get()) {
                Log.i(TAG, "dispatchMessage:close = " + close.get());
                return;
            }
            super.dispatchMessage(msg);
        }

        @Override
        public final boolean sendMessageAtTime(Message msg, long uptimeMillis) {
            synchronized (this) {
                if (close.get()) {
                    Log.i(TAG, "sendMessageAtTime:close = " + close.get());
                    return false;
                }
            }
            return super.sendMessageAtTime(msg, uptimeMillis);
        }
    }
}
