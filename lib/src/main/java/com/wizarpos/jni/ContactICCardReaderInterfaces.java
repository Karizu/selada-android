package com.wizarpos.jni;

import com.wizarpos.emvsample.constant.Constants;

public class ContactICCardReaderInterfaces implements Constants
{
    /*native interface */
    static
    {
        System.loadLibrary("wizarpos_contact_ic_card");
    }

    public native static int init();
    public native static int terminate();

    public native static int pollEvent(int nTimeout_MS, SmartCardEvents event);

    public native static int queryMaxNumber();
    public native static int queryPresence(int nSlotIndex);

    public native static int open(int nSlotIndex);
    public native static int close(int handle);

    public native static int powerOn(int handle, byte byteArrayATR[], ContactICCardSlotInfos info);
    public native static int powerOff(int handle);

    public native static int setSlotInfo(int Handle, ContactICCardSlotInfos info);

    public native static int transmit(int handle, byte byteArrayAPDU[], int nAPDULength, byte byteArrayResponse[]);
}