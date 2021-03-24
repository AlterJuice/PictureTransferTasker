package com.juicy.picturetransfer;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ObserveService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        observe();
        return super.onStartCommand(intent, flags, startId);
    }

    public File getInternalStoragePath() {
        File parent = Environment.getExternalStorageDirectory().getParentFile();
        File external = Environment.getExternalStorageDirectory();
        File[] files = parent.listFiles();
        File internal = null;
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().toLowerCase().startsWith("sdcard") && !files[i].equals(external)) {
                    internal = files[i];
                }
            }
        }

        return internal;
    }

    public File getExternalStoragePath() {

        return Environment.getExternalStorageDirectory();
    }

    public void observe() {
        Thread t = new Thread(() -> {


            //File[]   listOfFiles = new File(path).listFiles();
            String internalPath;
            String externalPath;
            File str = getInternalStoragePath();
            if (str != null) {
                internalPath = str.getAbsolutePath();

                new Observer(internalPath).startWatching();
            }
            str = getExternalStoragePath();
            if (str != null) {

                externalPath = str.getAbsolutePath();
                new Observer(externalPath).startWatching();
            }


        });
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();


    }

    static class Observer extends FileObserver {

        List<SingleFileObserver> mObservers;
        String mPath;
        int mMask;

        public void addSingleObserver(SingleFileObserver singleFileObserver){
            mObservers.add(singleFileObserver);
        }

        public Observer(String path) {
            this(path, ALL_EVENTS);
        }

        public Observer(String path, int mask) {
            super(path, mask);
            mPath = path;
            mMask = mask;
            // TODO Auto-generated constructor stub

        }

        @Override
        public void startWatching() {
            // TODO Auto-generated method stub
            if (mObservers != null)
                return;
            mObservers = new ArrayList<>();
            Stack<String> stack = new Stack<String>();
            stack.push(mPath);
            while (!stack.empty()) {
                String parent = stack.pop();
                mObservers.add(new SingleFileObserver(parent, mMask));
                File path = new File(parent);
                File[] files = path.listFiles();
                if (files == null) continue;
                for (int i = 0; i < files.length; ++i) {
                    if (files[i].isDirectory() && !files[i].getName().equals(".") && !files[i].getName().equals("..")) {
                        stack.push(files[i].getPath());
                    }
                }
            }
            for (int i = 0; i < mObservers.size(); i++) {
                mObservers.get(i).startWatching();
            }
        }

        @Override
        public void stopWatching() {
            // TODO Auto-generated method stub
            if (mObservers == null)
                return;
            for (int i = 0; i < mObservers.size(); ++i) {
                mObservers.get(i).stopWatching();
            }
            mObservers.clear();
            mObservers = null;
        }

        @Override
        public void onEvent(int event, final String path) {
            if (event == FileObserver.OPEN) {
                //do whatever you want
            } else if (event == FileObserver.CREATE) {
                //do whatever you want
            } else if (event == FileObserver.DELETE_SELF || event == FileObserver.DELETE) {

                //do whatever you want
            } else if (event == FileObserver.MOVE_SELF || event == FileObserver.MOVED_FROM || event == FileObserver.MOVED_TO) {
                //do whatever you want

            }
        }



    }
}