package com.hnu.anew;

import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;

/**
 * @classname Abstractclassname+Impl
 * @param
 */
public class AutoFocusCallbackImpl implements Camera.AutoFocusCallback {
    protected  final String TAG = Camera.AutoFocusCallback.class.getName();
    private static final long AUTO_FOCUS_INTERVAL_MS = 1300L; //自动对焦时间

    private Handler mAutoFocusHandler;
    private int mAutoFocusMessage;

    public void setHandler(Handler autoFocusHandler, int autoFocusMessage) {
        this.mAutoFocusHandler = autoFocusHandler;
        this.mAutoFocusMessage = autoFocusMessage;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        Log.v("zzw", "autof focus "+success);
        if (mAutoFocusHandler != null) {
            mAutoFocusHandler.sendEmptyMessageDelayed(mAutoFocusMessage,AUTO_FOCUS_INTERVAL_MS);
//            mAutoFocusHandler = null;
        } else {
            Log.v(TAG, "Got auto-focus callback, but no handler for it");
        }
    }
}