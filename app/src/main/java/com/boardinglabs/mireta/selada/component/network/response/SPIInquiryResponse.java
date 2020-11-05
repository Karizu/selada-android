package com.boardinglabs.mireta.selada.component.network.response;

import com.boardinglabs.mireta.selada.component.network.gson.GSPICmd;
import com.boardinglabs.mireta.selada.component.network.gson.GSeladaTransaction;

public class SPIInquiryResponse {
    public Boolean status;
    public String message;
    public GSPICmd data;
}
