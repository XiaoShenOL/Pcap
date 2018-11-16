package com.grumpycat.pcap.appinfo;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.SparseArray;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cc.he on 2018/11/13
 */
public class AppManager {
    private static SparseArray<AppInfo> apps = new SparseArray<>();

    private static volatile boolean isFinishLoad = true;
    public static boolean isFinishLoad(){
        return isFinishLoad;
    }

    public static void loadAppInfo(Context context) {
        isFinishLoad = false;
        apps.clear();

        PackageManager pm = context.getPackageManager();
        List<PackageInfo> infoList = pm.getInstalledPackages(0);
        for (PackageInfo p:infoList) {
            AppInfo app =new AppInfo();
            app.icon = p.applicationInfo.loadIcon(pm);
            app.name = pm.getApplicationLabel(p.applicationInfo).toString();
            app.pkgName = p.applicationInfo.packageName;
            app.uid = p.applicationInfo.uid;

            app.isSystem = (p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
            apps.put(app.uid, app);
        }
        if (finishListener != null){
            finishListener.onFinish();
        }

        isFinishLoad = true;
    }

    public static List<AppInfo> getApps(){
        if(isFinishLoad){
            int len = apps.size();
            if(len > 0){
                AppInfo[] app = new AppInfo[len];

                for(int i=0;i<len;i++){
                    app[i] = apps.valueAt(i);
                }

                Arrays.sort(app, (o1, o2) -> {
                    if (o1.isSystem && !o2.isSystem){
                        return 1;
                    }else if(!o1.isSystem && o2.isSystem){
                        return -1;
                    }
                    return o1.name.compareTo(o2.name);
                });

                return Arrays.asList(app);
            }
        }
        return null;
    }

    public static AppInfo getApp(int uid){
        if (isFinishLoad) {
            return apps.get(uid);
        }
        return null;
    }


    private static volatile OnLoadFinishListener finishListener;
    public static void setFinishListener(OnLoadFinishListener finishListener) {
        AppManager.finishListener = finishListener;
    }
}
