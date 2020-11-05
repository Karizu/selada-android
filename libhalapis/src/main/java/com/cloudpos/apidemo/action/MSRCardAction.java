
package com.cloudpos.apidemo.action;

import java.util.Map;

import android.util.Log;

import com.cloudpos.apidemo.activity.R;
import com.cloudpos.apidemo.function.ActionCallbackImpl;
import com.cloudpos.jniinterface.MSRInterfaces;

public class MSRCardAction extends ConstantAction {

    private void setParams(Map<String, Object> param, ActionCallbackImpl callback) {
        this.mCallback = callback;
    }

    public void open(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        if (isOpened) {
            callback.sendFailedMsg(mContext.getResources().getString(R.string.device_opened));
        } else {
            try {
                int result = MSRInterfaces.open();
                if (result < 0) {
                    callback.sendFailedMsg(mContext.getResources().getString(
                            R.string.operation_with_error)
                            + result);
                } else {
                    isOpened = true;
                    callback.sendSuccessMsg(mContext.getResources().getString(
                            R.string.operation_successful));
                    CallBackThread thread = new CallBackThread();
                    thread.start();
                }
            } catch (Throwable e) {
                e.printStackTrace();
                callback.sendFailedMsg(mContext.getResources().getString(R.string.operation_failed));
            }
        }
    }

    public void close(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        cancelCallBack();
        checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = 0;
                isOpened = false;
                result = MSRInterfaces.close();
                return result;
            }
        });
    }

    public void cancelCallBack() {
        synchronized (MSRInterfaces.object) {
            Log.i("MSRCard", "notify");
            MSRInterfaces.object.notifyAll();
            MSRInterfaces.eventID = EVENT_ID_CANCEL;
        }
    }

    private int getTrackData(final int trackNo, final byte[] arryData) {
        int result = checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = MSRInterfaces.getTrackData(trackNo, arryData, arryData.length);
                return result;
            }
        });
        return result;
    }

    private int getTrackDataLength(final int trackNo) {
        int result = checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = MSRInterfaces.getTrackDataLength(trackNo);
                return result;
            }
        });
        return result;
    }

    private int getTrackError(final int trackNo) {
        int result = checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = MSRInterfaces.getTrackError(trackNo);
                Log.e(TAG, "getTrackError: " + result);
                return result;
            }
        });
        return result;
    }

    class CallBackThread extends Thread {

        @Override
        public void run() {
            synchronized (MSRInterfaces.object) {
                try {
                    MSRInterfaces.object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (MSRInterfaces.eventID == MSRInterfaces.CONTACTLESS_CARD_EVENT_FOUND_CARD) {
                mCallback.sendSuccessMsg("Find a card");
                int result = 0;
                for (int trackNo = 0; trackNo < MSRInterfaces.TRACK_COUNT; trackNo++) {
                    result = getTrackError(trackNo);
                    if (result < 0) {
                        continue;
                    }
                    result = getTrackDataLength(trackNo);
                    if (result < 0) {
                        break;
                    }
                    byte[] arryTrackData = new byte[result];
                    result = getTrackData(trackNo, arryTrackData);
                    if (result < 0) {
                        break;
                    }
                }
            } else if (MSRInterfaces.eventID == EVENT_ID_CANCEL) {
                mCallback.sendSuccessMsg("Cancel notifier");
            }
        }
    }

}
