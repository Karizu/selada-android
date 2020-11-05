package com.synjones.bluetooth;

import com.cloudpos.jniinterface.JNILoads;

public class DecodeWlts {
	
	// wlt数据的存储路径，
	
	static{
		String fileName = "DecodeWlts";
		JNILoads.jniLoad(fileName);
		Runtime.getRuntime().loadLibrary(fileName);
	}
	
	/**
	 * @param wltPath : 以wlt为格式的文件的存储路径，
	 * @param bmpPath : 对wlt为格式的文件进行解密后的bmp格式的图片的保存路径。
	 * @param errorCode : 返回错误码
	 * */
	public static native int Wlt2Bmp(String wltPath, String bmpPath);
}
