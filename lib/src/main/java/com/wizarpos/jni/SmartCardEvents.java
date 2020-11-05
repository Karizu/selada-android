package com.wizarpos.jni;

public class SmartCardEvents {


	public static int SMART_CARD_EVENT_INSERT_CARD = 0;
	public static int SMART_CARD_EVENT_REMOVE_CARD	= 1;
	public int nEventID;
	public int nSlotIndex;
	public SmartCardEvents()
	{
		nEventID = -1;
		nSlotIndex = -1;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// wastodo

	}

}
