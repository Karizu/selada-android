package com.cloudpos.jniinterface;

public class RFCardEvents
{
	public static int nMaxEventDataLength = 0xFF;
	public int nEventID;
	public byte arryEventData[];
	public int nEventDataLength;
	
	public RFCardEvents()
	{
		arryEventData = new byte[nMaxEventDataLength];
		nEventDataLength = 0;
		nEventID = -1;
	}
}
