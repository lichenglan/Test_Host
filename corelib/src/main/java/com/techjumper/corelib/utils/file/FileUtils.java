package com.techjumper.corelib.utils.file;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Base64;

import com.techjumper.corelib.utils.Utils;
import com.techjumper.corelib.utils.common.JLog;
import com.techjumper.corelib.utils.system.AppUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

public class FileUtils {

    // 判断SD卡是否被挂载
    public static boolean isSDCardMounted() {
        // return Environment.getExternalStorageState().equals("mounted");
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    // 获取SD卡的根目录
    public static String getSDCardBaseDir() {
        if (isSDCardMounted()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    public static void deleteDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            JLog.d(file + " 不存在");
            return;
        }
        if (file.isFile()) {
            JLog.d("删除文件: " + file + " " + (file.delete() ? "成功" : "失败"));
            return;
        }

        String[] files = file.list();
        if (files == null || files.length == 0) {
            JLog.d("文件夹 " + file + " 为空, 直接删除: " + (file.delete() ? "成功" : "失败"));
            return;
        }
        JLog.d("文件夹 " + file + " 有文件 " + files.length + " 个");
        for (String fileName : files) {
            deleteDir(file.getAbsolutePath() + File.separator + fileName);
        }
        try {
            JLog.d("删除 文件夹 " + file + " " + (file.delete() ? "成功" : "失败"));
        } catch (Exception ignored) {
        }

    }

    // 分情况得到缓存目录
    public static String getCacheDir() {
        String cachePath;
        if (isSDCardMounted()) {
            File outCacheFile = Utils.appContext.getExternalCacheDir();
            if (outCacheFile != null) {
                cachePath = outCacheFile.getAbsolutePath();
            } else {
                String packageName = AppUtils.getPackageName();
                cachePath = FileUtils.getSDCardBaseDir()
                        + File.separator + "appCache"
                        + File.separator + packageName;
            }
        } else {
            cachePath = Utils.appContext.getCacheDir().getAbsolutePath();
        }
        File file = new File(cachePath);
        if (!file.exists()) file.mkdirs();
        return cachePath;
    }

    // 获取全部完整空间大小，返回MB
    public static long getPhoneSelfSize() {
        File rootFile = Environment.getRootDirectory();
        long rootSize = getBlockSize(rootFile);

        //缓存目录没有包含在root目录里面，所以要单独计算
        File cacheFile = Environment.getDownloadCacheDirectory();
        long cacheSize = getBlockSize(cacheFile);

        return (long) ((rootSize + cacheSize) / 1024.F / 1024);

    }

    //得到手机内置存储大小，返回MB
    public static long getPhoneInternalSDSize() {
        String esState = Environment.getExternalStorageState();
        //判断是否挂载，如果未挂载则直接返回0
        if (!esState.equals(Environment.MEDIA_MOUNTED)) {
            return 0;
        }
        File file = Environment.getExternalStorageDirectory();
        return (long) (getBlockSize(file) / 1024.F / 1024);
    }

    //得到外置SD卡的大小，返回MB
    public static long getPhoneOutSDSize() {
        String sdCardPath = getPhoneOutSDPath();
        long size;
        try {
            File file = new File(sdCardPath);
            size = (long) (getBlockSize(file) / 1024.F / 1024);
        } catch (Exception e) {
            size = 0;
        }
        return size;
    }

    //设备自身的空闲大小，返回MB
    public static long getPhoneSelfFreeSize() {
        File file = Environment.getRootDirectory();
        long rootFreeSize = getFreeBlockSize(file);

        file = Environment.getDownloadCacheDirectory();
        long cacheFreeSize = getFreeBlockSize(file);

        return (long) ((rootFreeSize + cacheFreeSize) / 1024.F / 1024);
    }

    //手机内置存储的空闲大小
    public static long getPhoneInternalSDFreeSize() {
        File file = Environment.getExternalStorageDirectory();
        return (long) (getFreeBlockSize(file) / 1024.F / 1024);
    }


    // 保存bitmap图片到SDCard的私有Cache目录
    public static boolean saveBitmapToSDCardPrivateCacheDir(Bitmap bitmap,
                                                            String fileName) {
        if (isSDCardMounted()) {
            BufferedOutputStream bos = null;
            // 获取私有的Cache缓存目录
            File file = Utils.appContext.getExternalCacheDir();

            try {
                bos = new BufferedOutputStream(new FileOutputStream(new File(
                        file, fileName)));
                if (!TextUtils.isEmpty(fileName)
                        && (fileName.contains(".png") || fileName
                        .contains(".PNG"))) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                }
                bos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeStream(bos);
            }
            return true;
        } else {
            return false;
        }
    }

    public static String fetchFilePostfix(File file) {
        String name = file.getName();
        return fetchFilePostfix(name);
    }

    public static String fetchFilePostfix(String name) {
        String postfix = "";
        int dotIndex = name.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex + 1 >= name.length()) return postfix;
        return name.substring(dotIndex + 1, name.length());
    }

    // 从SD卡获取文件
    public static byte[] loadFileFromSDCard(String fileDir) {
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            bis = new BufferedInputStream(
                    new FileInputStream(new File(fileDir)));
            byte[] buffer = new byte[8 * 1024];
            int c;
            while ((c = bis.read(buffer)) != -1) {
                baos.write(buffer, 0, c);
                baos.flush();
            }
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(baos);
            closeStream(bis);
        }
        return null;
    }

    // 从SDCard中寻找指定目录下的文件，返回Bitmap
    public Bitmap loadBitmapFromSDCard(String filePath) {
        byte[] data = loadFileFromSDCard(filePath);
        if (data != null) {
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (bm != null) {
                return bm;
            }
        }
        return null;
    }

    // 获取SD卡公有目录的路径
    public static String getSDCardPublicDir(String type) {
        return Environment.getExternalStoragePublicDirectory(type).toString();
    }

    // 获取SD卡私有Cache目录的路径
    public static String getSDCardPrivateCacheDir() {
        String path = null;
        try {
            path = Utils.appContext.getExternalCacheDir().getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    // 获取SD卡私有Files目录的路径
    public static String getSDCardPrivateFilesDir(String type) {
        String path = null;
        try {
            path = Utils.appContext.getExternalFilesDir(type).getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public static boolean isFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    public static boolean saveStringToPath(String str, String sdPath, String name) {
        if (str == null) return false;
        ByteArrayInputStream bis = new ByteArrayInputStream(str.getBytes());
        return saveInputstreamToPath(bis, sdPath, name);
    }

    public static boolean saveAssetsFileToPath(String assetsPath, String assetsName, String sdPath) {
        boolean result;
        try {
            InputStream is = Utils.appContext.getAssets().open(assetsPath + File.separator + assetsName);
            result = saveInputstreamToPath(is, sdPath, assetsName);
        } catch (Exception ignored) {
            result = false;
        }
        return result;
    }

    public static boolean copyFileToOtherPath(String oldPath, String name, String targetPath) throws FileNotFoundException {
        File file = new File(oldPath + File.separator + name);
        if (!file.exists()) return false;
        file = new File(targetPath + File.separator + name);
        if (file.exists()) file.delete();
        return saveInputstreamToPath(new FileInputStream(oldPath + File.separator + name), targetPath, name);
    }

    public static boolean saveInputstreamToPath(InputStream is, String sdPath, String name) {
        boolean result = true;
        try {
            BufferedInputStream bis = new BufferedInputStream(is);
            File file = new File(sdPath);
            if (!file.exists() && !file.mkdirs()) {
                throw new IOException("存储路径创建失败");
            }
            deleteFileIfExist(sdPath, name);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(sdPath + File.separator + name));

            byte[] buffer = new byte[8000];
            for (int count; (count = bis.read(buffer)) != -1; ) {
                bos.write(buffer, 0, count);
            }
            bis.close();
            bos.flush();
            bos.close();

        } catch (Exception ignored) {
            result = false;
        }
        return result;
    }

    // 从sdcard中删除文件
    public static boolean deleteFileFromSDCard(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                file.delete();
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    //关闭流
    public static void closeStream(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static long getBlockSize(File file) {
        StatFs stat = new StatFs(file.getPath());
        long blockSize;
        long blockCount;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            blockCount = stat.getBlockCountLong(); //所有的block数量
        } else {
            blockSize = stat.getBlockSize();
            blockCount = stat.getBlockCount();
        }
        return blockSize * blockCount;
    }

    private static long getFreeBlockSize(File file) {
        StatFs stat = new StatFs(file.getPath());
        long blockSize;
        long blockCount;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            blockCount = stat.getAvailableBlocksLong(); //得到未使用的block数量
        } else {
            blockSize = stat.getBlockSize();
            blockCount = stat.getAvailableBlocks();
        }
        return blockSize * blockCount;
    }

    /**
     * 得到SD卡上apk文件的图标
     */
    public static Drawable getApkFileIcon(String path) throws Exception {
        Resources resources = getResources(Utils.appContext, path);
        PackageManager pm = Utils.appContext.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, 0);
        Drawable icon = null;
        try {
            if (resources != null) {
                icon = resources.getDrawable(info.applicationInfo.icon);
            }
        } catch (Exception e) {
            icon = pm.getDefaultActivityIcon();
        }
        return icon;
    }

    public static Resources getResources(Context context, String apkPath) throws Exception {
        String PATH_AssetManager = "android.content.res.AssetManager";
        Class assetMagCls = Class.forName(PATH_AssetManager);
        Constructor assetMagCt = assetMagCls.getConstructor((Class[]) null);
        Object assetMag = assetMagCt.newInstance((Object[]) null);
        Class[] typeArgs = new Class[1];
        typeArgs[0] = String.class;
        Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod("addAssetPath",
                typeArgs);
        Object[] valueArgs = new Object[1];
        valueArgs[0] = apkPath;
        assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
        Resources res = context.getResources();
        typeArgs = new Class[3];
        typeArgs[0] = assetMag.getClass();
        typeArgs[1] = res.getDisplayMetrics().getClass();
        typeArgs[2] = res.getConfiguration().getClass();
        Constructor resCt = Resources.class.getConstructor(typeArgs);
        valueArgs = new Object[3];
        valueArgs[0] = assetMag;
        valueArgs[1] = res.getDisplayMetrics();
        valueArgs[2] = res.getConfiguration();
        res = (Resources) resCt.newInstance(valueArgs);
        return res;
    }

    //得到外置SD卡的路径
    public static String getPhoneOutSDPath() {
        Map<String, String> map = System.getenv();
        if (map.containsKey("SECONDARY_STORAGE")) {
            String paths = map.get("SECONDARY_STORAGE");
            String[] pathArray = paths.split(":");
            if (pathArray.length <= 0) {
                return null;
            }
            return pathArray[0];
        }
        return null;
    }

    public static boolean deleteFileIfExist(String path, String name) {
        File file = new File(path, name);
        return file.exists() && file.delete();
    }

    public static String loadTextFile(String path, String name) {
        String result = "";
        File file = new File(path, name);
        if (!file.exists() || !file.isFile())
            return result;
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[8000];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            result = new String(out.toByteArray(), "UTF-8");
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void saveInputToOutput(InputStream in, OutputStream out) throws IOException {
        BufferedInputStream bin = new BufferedInputStream(in);
        byte[] buffer = new byte[8000];
        for (int count; (count = bin.read(buffer)) != -1; ) {
            out.write(buffer, 0, count);
        }
        bin.close();
        out.close();
    }

    public static String base64Encode(String content) {
        String result = "";
        try {
            result = Base64.encodeToString(content.getBytes(), Base64.DEFAULT);
        } catch (Exception e) {
            JLog.e(e);
        }
        return result;
    }

    public static String getMacAddress() {
        try {
            return loadFileAsString("/sys/class/net/eth0/address")
                    .toUpperCase().substring(0, 17);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String loadFileAsString(String filePath) throws IOException {
        StringBuilder fileData = new StringBuilder(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }
}
