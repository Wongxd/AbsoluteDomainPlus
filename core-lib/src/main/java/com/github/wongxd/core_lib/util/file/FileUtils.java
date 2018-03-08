package com.github.wongxd.core_lib.util.file;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.github.wongxd.core_lib.R;
import com.github.wongxd.core_lib.base.utils.subutil.util.SubUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by wongxd on 2017/12/16.
 * Copyright © 2017年 no. All rights reserved.
 */


public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    /**
     * 自定义文件类型
     */
    public static final int TYPE_APK = 1;
    public static final int TYPE_JPEG = 2;
    public static final int TYPE_MP3 = 3;
    public static final int TYPE_MP4 = 4;
    public static final int TYPE_JDLY = 5;


    /**
     * 小数的格式化
     */
    public static final DecimalFormat FORMAT = new DecimalFormat("####.##");
    public static final DecimalFormat FORMAT_ONE = new DecimalFormat("####.#");


    /**
     * 存储卡获取 指定文件
     *
     * @param context
     * @param extension
     * @return
     */
    public static List<FileInfo> getSpecificTypeFiles(Context context, String[] extension) {
        List<FileInfo> fileInfoList = new ArrayList<FileInfo>();

        //内存卡文件的Uri
        Uri fileUri = MediaStore.Files.getContentUri("external");
        //筛选列，这里只筛选了：文件路径和含后缀的文件名
        String[] projection = new String[]{
                MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE
        };

        //构造筛选条件语句
        String selection = "";
        for (int i = 0; i < extension.length; i++) {
            if (i != 0) {
                selection = selection + " OR ";
            }
            selection = selection + MediaStore.Files.FileColumns.DATA + " LIKE '%" + extension[i] + "'";
        }
        //按时间降序条件
        String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED;

        Cursor cursor = context.getContentResolver().query(fileUri, projection, selection, null, sortOrder);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    String data = cursor.getString(0);
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setFilePath(data);

                    long size = 0;
                    try {
                        File file = new File(data);
                        size = file.length();
                        fileInfo.setSize(size);
                    } catch (Exception e) {

                    }
                    fileInfoList.add(fileInfo);
                } catch (Exception e) {
                    Log.i("FileUtils", "------>>>" + e.getMessage());
                }

            }
        }
        Log.i(TAG, "getSize ===>>> " + fileInfoList.size());
        return fileInfoList;
    }


    /**
     * 转化完整信息的FileInfo
     *
     * @param context
     * @param fileInfoList
     * @param type
     * @return
     */
    public static List<FileInfo> getDetailFileInfos(Context context, List<FileInfo> fileInfoList, int type) {

        if (fileInfoList == null || fileInfoList.size() <= 0) {
            return fileInfoList;
        }

        for (FileInfo fileInfo : fileInfoList) {
            if (fileInfo != null) {
                fileInfo.setName(getFileName(fileInfo.getFilePath()));
                fileInfo.setSizeDesc(getFileSize(fileInfo.getSize()));
                fileInfo.setFileType(type);
            }
        }
        return fileInfoList;
    }


    /**
     * 根据文件路径获取文件的名称
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (filePath == null || filePath.equals("")) return "";
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }


    /**
     * 获取文件的根目录
     *
     * @return
     */
    public static String getRootDirPath() {
        String path = "/mnt/";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory()+"/";
        }
        return path;
    }

    /**
     * 获取本应用的根目录
     *
     * @return
     */
    public static String getAppRootDirPath(Context ctx) {
        String path = "/mnt/" + ctx.getString(R.string.app_name) + "w/";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory() + "/" + ctx.getString(R.string.app_name) + "w/";
        }
        File f = new File(path);
        if (!f.exists()){
            f.mkdirs();
        }
        return path;
    }

    /**
     * 获取本应用的根目录
     *
     * @return
     */
    public static String getAppRootDirPath() {
        return getAppRootDirPath(SubUtils.getApp());
    }

    /**
     * 根据传入的byte数量转换为对应的byte, Kbyte, Mbyte, Gbyte单位的字符串
     *
     * @param size byte数量
     * @return
     */
    public static String getFileSize(long size) {
        if (size < 0) { //小于0字节则返回0
            return "0B";
        }

        double value = 0f;
        if ((size / 1024) < 1) { //0 ` 1024 byte
            return size + "B";
        } else if ((size / (1024 * 1024)) < 1) {//0 ` 1024 kbyte

            value = size / 1024f;
            return FORMAT.format(value) + "KB";
        } else if (size / (1024 * 1024 * 1024) < 1) {                  //0 ` 1024 mbyte
            value = (size * 100 / (1024 * 1024)) / 100f;
            return FORMAT.format(value) + "MB";
        } else {                  //0 ` 1024 mbyte
            value = (size * 100l / (1024l * 1024l * 1024l)) / 100f;
            return FORMAT.format(value) + "GB";
        }
    }


    /**
     * 转换为时间数组
     * String[0] 为数值
     * String[1] 为单位
     * 61 ===》》》 1.05秒
     *
     * @param second
     * @return
     */
    public static String[] getTimeByArrayStr(long second) {
        String[] result = new String[2];
        if (second < 0) { //小于0字节则返回0
            result[0] = "0";
            result[1] = "秒";
            return result;
        }

        double value = 0.0f;
        if (second / (60f * 1000f) < 1) { //秒
            result[0] = String.valueOf(second / 1000);
            result[1] = "秒";
//            return  size + "B";
        } else if ((second / (60f * 60f * 1000f)) < 1) {//分
            value = second / (60f * 1000f);
            result[0] = FORMAT_ONE.format(value);
            result[1] = "分";
//            return  FORMAT.format(value) + "KB";
        } else {                              //时
            value = second / (60f * 60f * 1000f);
            result[0] = FORMAT_ONE.format(value);
            result[1] = "时";
        }

        return result;
    }

}