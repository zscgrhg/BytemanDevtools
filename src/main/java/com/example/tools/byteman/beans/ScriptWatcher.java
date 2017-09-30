package com.example.tools.byteman.beans;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScriptWatcher extends FileAlterationListenerAdaptor {
    final List<File> watchedFolders = new CopyOnWriteArrayList<File>();
    private final BytemanProperties props;

    public ScriptWatcher(BytemanProperties props) {
        this.props = props;
    }

    public void watch(String folderName) {
        File folder = new File(folderName);
        if (folder.exists()) {
            watchedFolders.add(folder);
            installBTM(folder);
            if (folder.isDirectory()) {
                try {
                    FileAlterationObserver observer = new FileAlterationObserver(folder, new FileFilter() {
                        public boolean accept(File pathname) {
                            return pathname.getName().toLowerCase().endsWith(".btm");
                        }
                    });
                    FileAlterationMonitor monitor =
                            new FileAlterationMonitor(props.getPollingInterval());
                    observer.addListener(this);
                    monitor.addObserver(observer);
                    monitor.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onFileCreate(File file) {
        resetAll();
    }

    // Is triggered when a file is deleted from the monitored folder
    @Override
    public void onFileDelete(File file) {
        resetAll();
    }

    @Override
    public void onFileChange(File file) {
        resetAll();
    }

    public void resetAll() {
        clearBTM();
        installAll();
    }

    public void clearBTM() {
        try {
            org.jboss.byteman.agent.submit.Submit.main(new String[]{
                    "-p",String.valueOf(props.getPort()),
                    "-u"});
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void installAll() {
        for (File watchedFolder : watchedFolders) {
            try {
                installBTM(watchedFolder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void installBTM(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                installBTM(f);
            }
        } else {
            try {
                String btm = file.getCanonicalPath();
                if (btm.endsWith(".btm")) {
                    org.jboss.byteman.agent.submit.Submit.main(
                            new String[]{"-l",
                            "-p",
                            String.valueOf(props.getPort()),
                            btm});
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
