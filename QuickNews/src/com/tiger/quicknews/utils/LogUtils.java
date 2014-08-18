
package com.tiger.quicknews.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * 调试日志的统一输出
 * 
 * @author Xue Wenchao
 */
public class LogUtils {
    // 是否输出日志的开关
    public static boolean DEBUG = true;

    public static void i(String TAG, String msg) {
        if (DEBUG) {
            Log.i(TAG, msg);
            FileLogger.getInstance().addLog(TAG, msg);
        }
    }

    public static void i(String TAG, String msg, Throwable e) {
        if (DEBUG) {
            Log.i(TAG, msg, e);
            FileLogger.getInstance().addLog(TAG, msg, e);
        }
    }

    public static void e(String TAG, String msg) {
        if (DEBUG) {
            Log.e(TAG, msg);
            FileLogger.getInstance().addLog(TAG, msg);
        }
    }

    public static void e(String TAG, String msg, Throwable e) {
        if (DEBUG) {
            Log.e(TAG, msg, e);
            FileLogger.getInstance().addLog(TAG, msg, e);
        }
    }

    public static void d(String TAG, String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
            FileLogger.getInstance().addLog(TAG, msg);
        }
    }

    public static void d(String TAG, String msg, Throwable e) {
        if (DEBUG) {
            Log.d(TAG, msg, e);
            FileLogger.getInstance().addLog(TAG, msg, e);
        }
    }

    public static void v(String TAG, String msg) {
        if (DEBUG) {
            Log.v(TAG, msg);
            FileLogger.getInstance().addLog(TAG, msg);
        }
    }

    public static void v(String TAG, String msg, Throwable e) {
        if (DEBUG) {
            Log.v(TAG, msg, e);
            FileLogger.getInstance().addLog(TAG, msg, e);
        }
    }

    public static void w(String TAG, String msg) {
        if (DEBUG) {
            Log.w(TAG, msg);
            FileLogger.getInstance().addLog(TAG, msg);
        }
    }

    public static void w(String TAG, String msg, Throwable e) {
        if (DEBUG) {
            Log.w(TAG, msg, e);
            FileLogger.getInstance().addLog(TAG, msg, e);
        }
    }

    public static void println() {
        if (DEBUG) {
            System.out.println();
            FileLogger.getInstance().addLog("", "");
        }
    }

    public static void println(Object msg) {
        if (DEBUG) {
            System.out.println(msg);
            FileLogger.getInstance().addLog("System.out", msg.toString());
        }
    }

    public static void print(Object msg) {
        if (DEBUG) {
            System.out.print(msg);
            FileLogger.getInstance().addLog("System.out", msg.toString());
        }
    }

    public static void printStackTrace(Throwable e) {
        if (DEBUG) {
            e.printStackTrace();
            FileLogger.getInstance().addLog("System.out", e);
        }
    }

    public static void stopFileLogger() {
        FileLogger.getInstance().stop();
    }

    /**
     * 设置日志的存放路径
     * 
     * @param fileLogPath
     */
    public static void setFileLogPath(String fileLogPath) {
        FileLogger.getInstance().setLogPath(fileLogPath);
    }

    private static class FileLogger implements Runnable {
        private static FileLogger inst = new FileLogger();
        private String logPath;// 日志存放的路径

        private final ArrayList<String> logList = new ArrayList<String>();

        public static FileLogger getInstance() {
            return inst;
        }

        public void setLogPath(String logPath) {
            if (logPath != null && !logPath.endsWith(File.separator)) {
                this.logPath = logPath + File.separator;
            }
            else {
                this.logPath = logPath;
            }
        }

        private boolean isSDCardAvailable() {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        }

        public void addLog(String TAG, String message) {
            if (!isSDCardAvailable() || logPath == null) {
                return;
            }
            synchronized (this) {
                logList.add("[" + getTime() + "][" + TAG + "]  " + message);
                notifyWrite();
            }
        }

        public void addLog(String TAG, Throwable e) {
            if (!isSDCardAvailable() || logPath == null) {
                return;
            }
            synchronized (this) {
                addLog(TAG, e.toString());
                StackTraceElement[] elements = e.getStackTrace();
                for (int i = 0; i < elements.length; i++) {
                    StackTraceElement element = elements[i];
                    logList.add("    " + element.toString());
                }
                Throwable cause = e.getCause();
                if (cause != null) {
                    logList.add("    Caused by: " + cause.toString());
                    elements = cause.getStackTrace();
                    for (int i = 0; i < elements.length; i++) {
                        StackTraceElement element = elements[i];
                        logList.add("    " + element.toString());
                    }
                }
                notifyWrite();
            }
        }

        public void addLog(String TAG, String message, Throwable e) {
            if (!isSDCardAvailable() || logPath == null) {
                return;
            }
            synchronized (this) {
                addLog(TAG, message);
                addLog(TAG, e);
            }
        }

        private String getLog() {
            synchronized (this) {
                if (logList.size() > 0) {
                    return logList.remove(0);
                }
                return null;
            }
        }

        private Thread thread;
        private boolean isRunning;

        private void notifyWrite() {
            if (!isRunning && isSDCardAvailable()) {
                isRunning = true;
                thread = new Thread(this);
                thread.start();
            }
        }

        private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS",
                Locale.getDefault());
        private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());

        public static String getTime() {
            return dateTimeFormat.format(new Date());
        }

        public String getLogFileName() {
            return dateFormat.format(new Date()) + ".txt";
        }

        public void stop() {
            isRunning = false;
        }

        @Override
        public void run() {
            FileOutputStream fos = null;
            BufferedWriter writer = null;
            int count = 0;

            while (true) {
                String log = getLog();
                if (!isRunning) {
                    break;
                }
                if (log == null) {
                    if (count > 5) {
                        break;
                    }
                    else {
                        ++count;
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {

                        }
                        continue;
                    }
                }

                if (!isSDCardAvailable() || logPath == null) {
                    break;
                }
                try {
                    String logDir = logPath;
                    Utils.createDirs(new File(logDir));
                    File file = new File(logDir + getLogFileName());

                    if (!file.exists()) {
                        LogUtils.i("LogUtils",
                                "LogUtils createNewFile log file path:" + file.getPath());
                        file.createNewFile();
                    }
                    fos = new FileOutputStream(file, true);
                    writer = new BufferedWriter(new OutputStreamWriter(fos));
                    // 写文件
                    writer.write(log);
                    writer.newLine();
                } catch (Throwable e) {
                    e.printStackTrace();
                    break;
                } finally {
                    CloseUtils.close(writer);
                    CloseUtils.close(fos);
                }
            }

            isRunning = false;
            thread = null;
        }
    }
}
