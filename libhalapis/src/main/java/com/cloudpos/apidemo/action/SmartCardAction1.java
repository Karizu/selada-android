
package com.cloudpos.apidemo.action;

import java.util.Map;

import android.util.Log;

import com.cloudpos.apidemo.activity.R;
import com.cloudpos.apidemo.common.Common;
import com.cloudpos.apidemo.function.ActionCallbackImpl;
import com.cloudpos.apidemo.util.StringUtility;
import com.cloudpos.jniinterface.SmartCardInterfaces;
import com.cloudpos.jniinterface.SmartCardSlotInfos;

public class SmartCardAction1 extends ConstantAction {

    private int slotIndex = 0;
    private static boolean isRun = false;
    private int handle = 0;

    private void setParams(Map<String, Object> param, ActionCallbackImpl callback) {
        this.mCallback = callback;
    }

    public void queryMaxNumber(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        int result = getData(new DataAction() {

            @Override
            public int getResult() {
                return SmartCardInterfaces.queryMaxNumber();
            }
        });
        if (result >= 0) {
            mCallback.sendSuccessMsg("Max Slot Number = " + result);
        }
    }

    public void queryPresence(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        int result = getData(new DataAction() {

            @Override
            public int getResult() {
                return SmartCardInterfaces.queryPresence(slotIndex);
            }
        });
        if (result >= 0) {
            String msg = String.format("SlotIndex : %d Event : %s", slotIndex,
                    result == 0 ? "Absence" : "Presence");
            mCallback.sendSuccessMsg(msg);
        }
    }

    public void open(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        if (isOpened) {
            mCallback.sendFailedMsg(mContext.getResources().getString(R.string.device_opened));
        } else {
            try {
                int result = SmartCardInterfaces.open(slotIndex);
                if (result < 0) {
                    mCallback.sendFailedMsg(mContext.getResources().getString(
                            R.string.operation_with_error)
                            + result);
                } else {
                    isOpened = true;
                    handle = result;
                    mCallback.sendSuccessMsg(mContext.getResources().getString(
                            R.string.operation_successful));
                     CallBackThread presentThread = new
                     CallBackThread(callback,
                     SmartCardInterfaces.objPresent);
                     presentThread.start();
                     CallBackThread absentThread = new
                     CallBackThread(callback,
                     SmartCardInterfaces.objAbsent);
                     absentThread.start();
                }
            } catch (Throwable e) {
                e.printStackTrace();
                mCallback.sendFailedMsg(mContext.getResources()
                        .getString(R.string.operation_failed));
            }
        }
    }

    public void close(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        cancelRequest();
        checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                isOpened = false;
                Log.e(TAG, "handle = " + handle);
                int result = SmartCardInterfaces.close(handle);
                return result;
            }
        });
    }

    public void powerOn(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        final SmartCardSlotInfos slotInfo = new SmartCardSlotInfos();
        final byte[] arryATR = new byte[64];

        checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = SmartCardInterfaces.powerOn(handle, arryATR, slotInfo);
                if (result >= 0) {
                    mCallback.sendSuccessMsg("Data = "
                            + StringUtility.ByteArrayToString(arryATR, result));
                }
                return result;
            }
        });
    }

    public void powerOff(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                return SmartCardInterfaces.powerOff(handle);
            }
        });
    }

    public void transmit(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        final byte[] arryAPDU = new byte[] {
                0x00, (byte) 0x84, 0x00, 0x00, 0x08
        };
        final byte[] arryResponse = new byte[32];
        checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = SmartCardInterfaces.transmit(handle, arryAPDU, arryResponse);
                if (result >= 0) {
                    mCallback.sendSuccessMsg("APDUResponse = "
                            + StringUtility.ByteArrayToString(arryResponse, result));
                }
                return result;
            }
        });
    }

    public void verify(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        final byte[] arryKey = {
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
        };
        checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                return SmartCardInterfaces.verify(handle, arryKey);
            }
        });
    }

    public void read(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        final byte[] arryData = new byte[16];
        checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = SmartCardInterfaces.read(handle, 0, arryData, 0);
                if (result >= 0) {
                    mCallback.sendSuccessMsg("Read data = "
                            + StringUtility.ByteArrayToString(arryData, result));
                }
                return result;
            }
        });
    }

    public void write(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        final byte[] arryData = Common.createMasterKey(16);
        checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = SmartCardInterfaces.read(handle, 0, arryData, 0);
                if (result >= 0) {
                    mCallback.sendSuccessMsg("Written data = "
                            + StringUtility.ByteArrayToString(arryData, result));
                }
                return result;
            }
        });
    }

    public void cancelRequest() {
        if (isRun) {
            SmartCardInterfaces.notifyCancel();
        } else {
            mCallback.sendFailedMsg(mContext.getResources().getString(R.string.operation_failed));
        }
    }

    class CallBackThread extends Thread {
        private Object object;

        public CallBackThread(ActionCallbackImpl callback, Object object) {
            this.object = object;
        }

        @Override
        public void run() {
            isRun = true;
            while (isRun) {
                synchronized (object) {
                    try {
                        object.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
                if (SmartCardInterfaces.notifyEvent.eventID == SmartCardInterfaces.EVENT_CANCEL) {
                    break;
                } else if (SmartCardInterfaces.notifyEvent.eventID == SmartCardInterfaces.EVENT_INSERT_CARD) {
                    String msg = String.format("SlotIndex : %d Event : %s",
                            SmartCardInterfaces.notifyEvent.slotIndex, "Inserted");
                    mCallback.sendSuccessMsg(msg);
                } else if (SmartCardInterfaces.notifyEvent.eventID == SmartCardInterfaces.EVENT_REMOVE_CARD) {
                    String msg = String.format("SlotIndex : %d Event : %s",
                            SmartCardInterfaces.notifyEvent.slotIndex, "Removed");
                    mCallback.sendSuccessMsg(msg);
                }
            }
            isRun = false;
        }
    }

}
