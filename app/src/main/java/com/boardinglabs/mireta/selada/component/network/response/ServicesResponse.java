package com.boardinglabs.mireta.selada.component.network.response;


import com.boardinglabs.mireta.selada.component.network.gson.GSeladaService;
import com.boardinglabs.mireta.selada.component.network.gson.GServices;

import java.util.List;

/**
 * Created by Dhimas on 12/14/17.
 */

public class ServicesResponse extends ListResponse {
    public List<GSeladaService> data;
}
