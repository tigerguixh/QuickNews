package com.tiger.quicknews.wedget.gesture;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

/**
 * activity运行栈，用于完全�?��程序
 * 
 * @author Xue Wenchao
 *
 */
public class ActivityStack {
	private static List<Activity> activityList = new ArrayList<Activity>();

	public static void remove(Activity activity) {
		activityList.remove(activity);
	}

	public static void add(Activity activity) {
		activityList.add(activity);
	}

	public static void finishProgram() {
		for (Activity activity : activityList) {
			activity.finish();
		}
	}

	public static boolean isActivityRunning(String className) {
		if (className != null) {
			for (Activity activity : activityList) {
				if (activity.getClass().getName().equals(className)) {
					return true;
				}
			}
		}
		return false;
	}
}
