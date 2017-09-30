package com.example.tools.byteman.beans;

import org.apache.commons.io.IOUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.boot.CommandLineRunner;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class BytemanInstaller implements CommandLineRunner {

    private final BytemanProperties props;
    private final ScriptWatcher watcher;


    public BytemanInstaller(BytemanProperties props) {
        this.props = props;
        this.watcher = new ScriptWatcher(props);
    }

    public void run(String... strings) throws Exception {
       /* SecurityManager sm = new BytemanSecurityManager( System.getSecurityManager() );
        System.setSecurityManager(sm);*/
        installByteman();
    }

    public void installByteman() throws FileNotFoundException {
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        org.jboss.byteman.agent.install.Install.main(new String[]{
                "-b",
                "-p",
                String.valueOf(props.getPort()),
                //"-Dorg.jboss.byteman.compile.to.bytecode",
                "-Dorg.jboss.byteman.transform.all",
                "-Dorg.jboss.byteman.verbose",
                "-Dorg.jboss.byteman.debug",
                pid});
        scan();
        List<String> scriptFolders = props.getScriptFolders();
        if (scriptFolders != null && !scriptFolders.isEmpty()) {
            for (String folderName : scriptFolders) {
                watcher.watch(folderName);
            }
        }
    }

    public void scan() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("com.example.tools.btm"))
                .setScanners(new ResourcesScanner()));
        Set<String> scripts =
                reflections.getResources(Pattern.compile(".*\\.btm"));
        for (String script : scripts) {
            installBuildIn(script);
        }

    }

    public void installBuildIn(String buildInScript) {
        try {
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(buildInScript);
            File tempFile = File.createTempFile(buildInScript, ".btm");
            tempFile.deleteOnExit();
            OutputStream fileOutputStream = new FileOutputStream(tempFile);
            IOUtils.copy(resourceAsStream, fileOutputStream);
            watcher.watch(tempFile.getCanonicalPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
