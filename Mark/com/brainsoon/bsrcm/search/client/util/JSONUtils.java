package com.brainsoon.bsrcm.search.client.util;

import net.sf.json.JSONArray;

public class JSONUtils {

    public static String[] getStringArray4Json(String jsonString){
        
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        String[] stringArray = new String[jsonArray.size()];
        for( int i = 0 ; i<jsonArray.size() ; i++ ){
            stringArray[i] = jsonArray.getString(i);
            
        }
        return stringArray;
    }
    
}
