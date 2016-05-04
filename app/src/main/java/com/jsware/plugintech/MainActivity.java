package com.jsware.plugintech;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jsware.dynamiclib.IDynamic;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    DexClassLoader dexClassLoader;
    private AssetManager mAssetManager;//资源管理器
    private Resources mResources;//资源
    private Resources.Theme mTheme;//主题

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String dexPath = Environment.getExternalStorageDirectory()+ File.separator+"pluginsdk.apk";
        File releaseFile = getDir("dex", 0);
        dexClassLoader = new DexClassLoader(dexPath, releaseFile.getAbsolutePath(), null, getClassLoader());

        /**
         * 加载资源
         */
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, dexPath);
            mAssetManager = assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Resources superRes = super.getResources();
        superRes.getDisplayMetrics();
        superRes.getConfiguration();
        mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(),superRes.getConfiguration());
        mTheme = mResources.newTheme();
        mTheme.setTo(super.getTheme());

        Class mLoadClassDynamic;
        try {
            mLoadClassDynamic = dexClassLoader.loadClass("com.jsware.pluginsdk.Dynamic");
            Object dynamicObject = mLoadClassDynamic.newInstance();
            //接口形式调用
            IDynamic dynamic = (IDynamic)dynamicObject;
            dynamic.showDialog(MainActivity.this);
        } catch (Exception e) {
            Log.e("DEMO", "msg:"+e.getMessage());
        }
    }

    @Override
    public AssetManager getAssets() {
        return mAssetManager == null ? super.getAssets() : mAssetManager;
    }

    @Override
    public Resources getResources() {
        return mResources == null ? super.getResources() : mResources;
    }

    @Override
    public Resources.Theme getTheme() {
        return mTheme == null ? super.getTheme() : mTheme;
    }
}
