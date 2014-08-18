/*
 * Copyright (C) 2013 北京活动时文化传媒有限公司
 * 
 *             http://www.mosh.cn
 * 
 * All rights reserved.
 */

package com.tiger.quicknews.http.json;

import android.content.Context;

import org.json.JSONObject;

/**
 * JSON解析抽象类
 * 
 * @author wang.wei
 */
public abstract class JsonPacket {

    private final Context mContext;

    /**
     * @param context
     */
    public JsonPacket(Context context) {
        mContext = context;
    }

    /**
     * @return
     */
    protected Context getContext() {
        return mContext;
    }

    /**
     * @param key
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public static String getString(String key, JSONObject jsonObject) throws Exception {
        String res = "";
        if (jsonObject.has(key)) {
            if (key == null) {
                return "";
            }
            res = jsonObject.getString(key);
        }
        return res;
    }

    /**
     * @param key
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public static int getInt(String key, JSONObject jsonObject) throws Exception {
        int res = -1;
        if (jsonObject.has(key)) {
            res = jsonObject.getInt(key);
        }
        return res;
    }

    /**
     * @param key
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public static double getDouble(String key, JSONObject jsonObject) throws Exception {
        double res = 0l;
        if (jsonObject.has(key)) {
            res = jsonObject.getDouble(key);
        }
        return res;
    }

}
