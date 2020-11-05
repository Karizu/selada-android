
package com.cloudpos.apidemo.action;

import java.util.Map;

import android.util.Log;

import com.cloudpos.apidemo.activity.R;
import com.cloudpos.apidemo.common.Common;
import com.cloudpos.apidemo.function.ActionCallbackImpl;
import com.cloudpos.apidemo.util.ByteConvert;
import com.cloudpos.apidemo.util.StringUtility;
import com.cloudpos.jniinterface.RFCardInterfaces;

public class RFCardAction extends ConstantAction {
    private int sectorIndex = 0;
    private int pinType = 0;
    private int blockIndex = 1;

    private void setParams(Map<String, Object> param, ActionCallbackImpl callback) {
        this.mCallback = callback;
    }

    public void open(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        if (isOpened) {
            callback.sendFailedMsg(mContext.getResources().getString(R.string.device_opened));
        } else {
            try {
                int result = RFCardInterfaces.open();
                if (result < 0) {
                    callback.sendFailedMsg("open " + mContext.getResources().getString(
                            R.string.operation_with_error)
                            + result);
                } else {
                    isOpened = true;
                    callback.sendSuccessMsg("open " + mContext.getResources().getString(
                            R.string.operation_successful));
                    CallBackThread thread = new CallBackThread();
                    thread.start();
                    Thread.sleep(100);
                    searchBegin();
                }
            } catch (Throwable e) {
                e.printStackTrace();
                callback.sendFailedMsg("open "
                        + mContext.getResources().getString(R.string.operation_failed));
            }
        }

    }

    public void close(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        searchEnd();
        checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                isOpened = false;
                int result = RFCardInterfaces.close();
                return result;
            }
        });
    }

    public void searchBegin() {
        checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = RFCardInterfaces.searchBegin(
                        RFCardInterfaces.CONTACTLESS_CARD_MODE_AUTO, 1, -1);
                return result;
            }
        });
    }

    public void searchEnd() {
        checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = RFCardInterfaces.searchEnd();
                return result;
            }
        });
    }

    public void queryInfo(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        final int[] hasMoreCard = new int[1];
        final int[] cardType = new int[1];
        int result = checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = RFCardInterfaces.queryInfo(hasMoreCard, cardType);
                return result;
            }
        });
        Log.e(TAG, "hasMoreCard = " + hasMoreCard[0]);
        Log.e(TAG, "cardType = " + cardType[0]);
        if (result >= 0) {
            if (hasMoreCard[0] != 0) {
                // if there are more than one card, return fail
                callback.sendFailedMsg("There is more than one card in the field!");
            } else {
                if (cardType[0] == 0x0000 || cardType[0] == 0x0100) {
                    callback.sendSuccessMsg("CPU_Card");
                } else if (cardType[0] == 0x0001 || cardType[0] == 0x0002 || cardType[0] == 0x0003) {
                    callback.sendSuccessMsg("Mifare_Card");
                } else if (cardType[0] == 0x0004 || cardType[0] == 0x0005) {
                    callback.sendSuccessMsg("Mifare_Ultralight_Card");
                } else {
                    callback.sendSuccessMsg("Unknown_Type_Card");
                }
            }
        }
    }

    public void verify(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        final byte[] arryKey = {
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
        };
        checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = RFCardInterfaces.verify(sectorIndex, pinType, arryKey, arryKey.length);
                return result;
            }
        });
    }

    public void read(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);

        final byte[] arryData = new byte[16];
        int result = checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = RFCardInterfaces.read(sectorIndex, blockIndex, arryData,
                        arryData.length);
                return result;
            }
        });
        if (result >= 0) {
            callback.sendSuccessMsg("Read Data = "
                    + StringUtility.ByteArrayToString(arryData, result));
        }
    }

    public void write(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        final byte[] arryData = Common.createMasterKey(16);
        // final byte[] arryData = new byte[] {
        // (byte) 0x38, (byte) 0x38, (byte) 0x38, (byte) 0x38
        // };
        int result = checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = RFCardInterfaces.write(sectorIndex, blockIndex, arryData,
                        arryData.length);
                return result;
            }
        });
        if (result >= 0) {
            callback.sendSuccessMsg("Written Data = "
                    + StringUtility.ByteArrayToString(arryData, result));
        }
    }

    public void attach(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        final byte[] arryATR = new byte[255];
        int result = checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = RFCardInterfaces.attach(arryATR);
                return result;
            }
        });
        if (result >= 0) {
            callback.sendSuccessMsg("ATR = " + StringUtility.ByteArrayToString(arryATR, result));
        }
    }

    public void detach(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = RFCardInterfaces.detach();
                return result;
            }
        });
    }

    public void transmit(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        // final byte[] arryAPDU = new byte[] {
        // 0x00, (byte) 0x84, 0x00, 0x00, (byte) 0x08
        // };

        final byte[] arryAPDU = new byte[] {
                (byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00,
                (byte) 0x0E, (byte) 0x32, (byte) 0x50, (byte) 0x41,
                (byte) 0x59, (byte) 0x2E, (byte) 0x53, (byte) 0x59,
                (byte) 0x53, (byte) 0x2E, (byte) 0x44, (byte) 0x44,
                (byte) 0x46, (byte) 0x30, (byte) 0x31
        };
        final byte[] arryAPDUResponse = new byte[255];
        int result = checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = RFCardInterfaces.transmit(arryAPDU, arryAPDU.length, arryAPDUResponse);
                return result;
            }
        });
        if (result >= 0) {
            callback.sendSuccessMsg("APDU Response = "
                    + StringUtility.ByteArrayToString(arryAPDUResponse, result));
        }
    }

    public void readMoneyValue(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        final byte[] arryMoney = new byte[4];
        final byte[] arryUserData = new byte[1];
        int result = checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = RFCardInterfaces.readValue(sectorIndex, blockIndex, arryMoney,
                        arryMoney.length, arryUserData);
                return result;
            }
        });
        if (result >= 0) {
            callback.sendSuccessMsg("Read Money Value = "
                    + StringUtility.ByteArrayToString(arryMoney, result));
        }
    }

    public void readMoneyValue2(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        final byte[] arryMoney = new byte[4];
        final byte[] arryUserData = new byte[1];
        int result = checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = RFCardInterfaces.readValue(sectorIndex, blockIndex + 1, arryMoney,
                        arryMoney.length, arryUserData);
                return result;
            }
        });
        if (result >= 0) {
            callback.sendSuccessMsg("Read Money Value = "
                    + StringUtility.ByteArrayToString(arryMoney, result));
        }
    }

    public void writeMoneyValue(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        long max = 0x40000000;
        final int money = (int) Math.round((Math.random() * 2 - 1) * max * 2);
        final byte userData = (byte) 0x00;
        int result = checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = RFCardInterfaces
                        .writeValue(sectorIndex, blockIndex, money, 4, userData);
                return result;
            }
        });
        if (result >= 0) {
            callback.sendSuccessMsg("Written Money Value = "
                    + StringUtility.ByteArrayToString(ByteConvert.int2byte4(money), 4));
        }
    }

    public void increment(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        restore(param, callback);
        final int money = 2;
        int result = checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = RFCardInterfaces.increment(sectorIndex, blockIndex, money);
                return result;
            }
        });
        if (result >= 0) {
            callback.sendSuccessMsg("Increment Value = "
                    + StringUtility.ByteArrayToString(ByteConvert.int2byte4(money), 4));
        }
        transfer(param, callback);
    }

    public void decrement(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        // restore(param, callback);
        final int money = 2;
        int result = checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = RFCardInterfaces.decrement(sectorIndex, blockIndex, money);
                return result;
            }
        });
        if (result >= 0) {
            callback.sendSuccessMsg("Decrement Value = "
                    + StringUtility.ByteArrayToString(ByteConvert.int2byte4(money), 4));
        }
        transfer2(param, callback);
    }

    public void transfer(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = RFCardInterfaces.transfer(sectorIndex, blockIndex + 1);
                return result;
            }
        });
    }

    public void transfer2(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = RFCardInterfaces.transfer(sectorIndex, blockIndex);
                return result;
            }
        });
    }

    public void restore(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = RFCardInterfaces.restore(sectorIndex, blockIndex);
                return result;
            }
        });
    }

    public void touch(Map<String, Object> param, ActionCallbackImpl callback) {
        setParams(param, callback);
        checkOpenedAndGetData(new DataAction() {

            @Override
            public int getResult() {
                int result = RFCardInterfaces.touch();
                if (result == 0) {
                    mCallback.sendResponse("no card in the fields");
                } else if (result > 0) {
                    mCallback.sendResponse("fina a card");
                }
                return result;
            }
        });
    }

    class CallBackThread extends Thread {

        @Override
        public void run() {
//            synchronized (RFCardInterfaces.object) {
//                try {
//                    RFCardInterfaces.object.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (RFCardInterfaces.nEventID == RFCardInterfaces.CONTACTLESS_CARD_EVENT_FOUND_CARD) {
//                mCallback.sendSuccessMsg("ATS = "
//                        + StringUtility.ByteArrayToString(RFCardInterfaces.arryEventData,
//                                RFCardInterfaces.arryEventData.length));
//            }
        	try {
				RFCardInterfaces.waitForCardPresent();
				if (RFCardInterfaces.isCallBackCalled &&
						RFCardInterfaces.notifyEvent.eventID == RFCardInterfaces.CONTACTLESS_CARD_EVENT_FOUND_CARD) {
					mCallback.sendSuccessMsg("ATS = "
	                        + StringUtility.ByteArrayToString(RFCardInterfaces.notifyEvent.eventData,
	                                RFCardInterfaces.notifyEvent.eventData.length));
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    }
}
