
package com.tiger.quicknews.bean;

import android.content.ContentValues;
import android.database.SQLException;
import android.util.Log;

import com.tiger.quicknews.dao.ChannelDao;
import com.tiger.quicknews.db.SQLHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChannelManage {
    public static ChannelManage channelManage;
    /**
     * 默认的用户选择频道列表
     */
    public static List<ChannelItem> defaultUserChannels;
    /**
     * 默认的其他频道列表
     */
    public static List<ChannelItem> defaultOtherChannels;
    private ChannelDao channelDao;
    /** 判断数据库中是否存在用户数据 */
    private boolean userExist = false;
    static {
        defaultUserChannels = new ArrayList<ChannelItem>();
        defaultOtherChannels = new ArrayList<ChannelItem>();
        defaultUserChannels.add(new ChannelItem(1, "头条", 1, 1));
        defaultUserChannels.add(new ChannelItem(2, "足球", 2, 1));
        defaultUserChannels.add(new ChannelItem(3, "娱乐", 3, 1));
        defaultUserChannels.add(new ChannelItem(4, "体育", 4, 1));
        defaultUserChannels.add(new ChannelItem(5, "财经", 5, 1));
        defaultUserChannels.add(new ChannelItem(6, "科技", 6, 1));
        // defaultUserChannels.add(new ChannelItem(7, "图片", 1, 0));
        defaultOtherChannels.add(new ChannelItem(7, "CBA", 1, 0));
        defaultOtherChannels.add(new ChannelItem(8, "笑话", 2, 0));
        defaultOtherChannels.add(new ChannelItem(9, "汽车", 3, 0));
        defaultOtherChannels.add(new ChannelItem(10, "时尚", 4, 0));
        defaultOtherChannels.add(new ChannelItem(11, "北京", 5, 0));
        defaultOtherChannels.add(new ChannelItem(12, "军事", 6, 0));
        defaultOtherChannels.add(new ChannelItem(13, "房产", 7, 0));
        defaultOtherChannels.add(new ChannelItem(14, "游戏", 8, 0));
        defaultOtherChannels.add(new ChannelItem(15, "精选", 9, 0));
        defaultOtherChannels.add(new ChannelItem(16, "电台", 10, 0));
        defaultOtherChannels.add(new ChannelItem(17, "情感", 11, 0));
        defaultUserChannels.add(new ChannelItem(18, "电影", 12, 0));
        defaultUserChannels.add(new ChannelItem(19, "NBA", 13, 0));
        defaultUserChannels.add(new ChannelItem(20, "数码", 14, 0));
        defaultUserChannels.add(new ChannelItem(21, "移动", 15, 0));
        defaultUserChannels.add(new ChannelItem(22, "彩票", 16, 0));
        defaultUserChannels.add(new ChannelItem(23, "教育", 17, 0));
        defaultUserChannels.add(new ChannelItem(24, "论坛", 18, 0));
        defaultOtherChannels.add(new ChannelItem(25, "旅游", 19, 0));
        defaultOtherChannels.add(new ChannelItem(26, "手机", 20, 0));
        defaultOtherChannels.add(new ChannelItem(27, "博客", 21, 0));
        defaultOtherChannels.add(new ChannelItem(28, "社会", 22, 0));
        defaultOtherChannels.add(new ChannelItem(29, "家居", 23, 0));
        defaultOtherChannels.add(new ChannelItem(30, "暴雪", 24, 0));
        defaultUserChannels.add(new ChannelItem(31, "亲子", 25, 0));
    }

    private ChannelManage(SQLHelper paramDBHelper) throws SQLException {
        if (channelDao == null)
            channelDao = new ChannelDao(paramDBHelper.getContext());
        // NavigateItemDao(paramDBHelper.getDao(NavigateItem.class));
        initDefaultChannel();
        return;
    }

    /**
     * 初始化频道管理类
     * 
     * @param paramDBHelper
     * @throws SQLException
     */
    public static ChannelManage getManage(SQLHelper dbHelper) throws SQLException {
        if (channelManage == null)
            synchronized (ChannelManage.class) {
                if (channelManage == null) {
                    channelManage = new ChannelManage(dbHelper);
                }
            }
        return channelManage;
    }

    /**
     * 清除所有的频道
     */
    public void deleteAllChannel() {
        channelDao.clearFeedTable();
    }

    /**
     * 获取其他的频道
     * 
     * @return 数据库存在用户配置 ? 数据库内的用户选择频道 : 默认用户选择频道 ;
     */
    public List<ChannelItem> getUserChannel() {
        Object cacheList = channelDao.listCache(SQLHelper.SELECTED + "= ?", new String[] {
                "1"
        });
        if (cacheList != null && !((List<?>) cacheList).isEmpty()) {
            userExist = true;
            List<Map<String, String>> maplist = (List) cacheList;
            int count = maplist.size();
            List<ChannelItem> list = new ArrayList<ChannelItem>();
            for (int i = 0; i < count; i++) {
                ChannelItem navigate = new ChannelItem();
                navigate.setId(Integer.valueOf(maplist.get(i).get(SQLHelper.ID)));
                navigate.setName(maplist.get(i).get(SQLHelper.NAME));
                navigate.setOrderId(Integer.valueOf(maplist.get(i).get(SQLHelper.ORDERID)));
                navigate.setSelected(Integer.valueOf(maplist.get(i).get(SQLHelper.SELECTED)));
                list.add(navigate);
            }
            return list;
        }
        return defaultUserChannels;
    }

    /**
     * 获取其他的频道
     * 
     * @return 数据库存在用户配置 ? 数据库内的其它频道 : 默认其它频道 ;
     */
    public List<ChannelItem> getOtherChannel() {
        Object cacheList = channelDao.listCache(SQLHelper.SELECTED + "= ?", new String[] {
                "0"
        });
        List<ChannelItem> list = new ArrayList<ChannelItem>();
        if (cacheList != null && !((List) cacheList).isEmpty()) {
            List<Map<String, String>> maplist = (List) cacheList;
            int count = maplist.size();
            for (int i = 0; i < count; i++) {
                ChannelItem navigate = new ChannelItem();
                navigate.setId(Integer.valueOf(maplist.get(i).get(SQLHelper.ID)));
                navigate.setName(maplist.get(i).get(SQLHelper.NAME));
                navigate.setOrderId(Integer.valueOf(maplist.get(i).get(SQLHelper.ORDERID)));
                navigate.setSelected(Integer.valueOf(maplist.get(i).get(SQLHelper.SELECTED)));
                list.add(navigate);
            }
            return list;
        }
        if (userExist) {
            return list;
        }
        cacheList = defaultOtherChannels;
        return (List<ChannelItem>) cacheList;
    }

    /**
     * 保存用户频道到数据库
     * 
     * @param userList
     */
    public void saveUserChannel(List<ChannelItem> userList) {
        for (int i = 0; i < userList.size(); i++) {
            ChannelItem channelItem = userList.get(i);
            channelItem.setOrderId(i);
            channelItem.setSelected(Integer.valueOf(1));
            channelDao.addCache(channelItem);
        }
    }

    public void updateChannel(ChannelItem channelItem, String selected) {
        ContentValues values = new ContentValues();
        values.put("selected", selected);
        values.put("id", channelItem.getId());
        values.put("name", channelItem.getName());
        values.put("orderId", channelItem.getOrderId());
        channelDao.updateCache(values, " name = ?", new String[] {
                channelItem.getName()
        });
    }

    // /**
    // * 保存单个用户频道到数据库
    // *
    // * @param userList
    // */
    //
    // public void saveUserChannel(ChannelItem channelItem) {
    // channelDao.addCache(channelItem);
    // }
    //
    // public void deleteUserChannel(ChannelItem channelItem) {
    // channelDao.deleteCache(" name=?", new String[] {
    // channelItem.getName()
    // });
    // }
    //
    // /**
    // * 保存单个其他频道到数据库
    // *
    // * @param userList
    // */
    // public void saveOtherChannel(ChannelItem channelItem) {
    // channelDao.addCache(channelItem);
    // }
    //
    // public void deleteOtherChannel(ChannelItem channelItem) {
    // channelDao.deleteCache(" name=?", new String[] {
    // channelItem.getName()
    // });
    // }

    /**
     * 保存其他频道到数据库
     * 
     * @param otherList
     */
    public void saveOtherChannel(List<ChannelItem> otherList) {
        for (int i = 0; i < otherList.size(); i++) {
            ChannelItem channelItem = otherList.get(i);
            channelItem.setOrderId(i);
            channelItem.setSelected(Integer.valueOf(0));
            channelDao.addCache(channelItem);
        }
    }

    /**
     * 初始化数据库内的频道数据
     */
    private void initDefaultChannel() {
        Log.d("deleteAll", "deleteAll");
        deleteAllChannel();
        saveUserChannel(defaultUserChannels);
        saveOtherChannel(defaultOtherChannels);
    }
}
