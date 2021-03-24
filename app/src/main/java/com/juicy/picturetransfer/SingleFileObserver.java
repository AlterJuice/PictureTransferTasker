package com.juicy.picturetransfer;

import android.os.FileObserver;

public class SingleFileObserver extends FileObserver {
    private String mPath;

    public SingleFileObserver(String path, int mask) {
        super(path, mask);
        mPath = path;
    }

    @Override
    public void onEvent(int event, String path) {
        // TODO Auto-generated method stub
        String newPath = mPath + "/" + path;
        this.onEvent(event, newPath);
    }

}