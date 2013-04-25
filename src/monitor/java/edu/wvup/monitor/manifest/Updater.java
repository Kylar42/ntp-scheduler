package edu.wvup.monitor.manifest;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;

/**
 * User: Tom Byrne
 * "If I am unable to see, it is because
 * I am being stood upon by giants."
 */
public class Updater {


    private final File _origin;
    private final File _target;

    public Updater(File originDirectory, File targetDir) {
        _origin = originDirectory;
        _target = targetDir;
    }

    public boolean performUpdate() throws IOException {
        //Ok, loop through each file, and if it's a directory, loop through each of it's files.
        iterate(_origin);
        return true;
    }

    public void iterate(File sourceDir) throws IOException {
        for (File child : sourceDir.listFiles()) {
            Path relativePath = relativePath(child, _origin);
            if(child.getAbsolutePath().startsWith(relativePath.toAbsolutePath().toString())){
                continue;//we don't want ones that are parents or selves.
            }
            if (!child.isDirectory()) {
                Path targetPath = Paths.get(_target.toPath().toString(), relativePath.toString());
                copy(child, targetPath);
            } else {
                iterate(child);
            }

        }
    }


    public Path relativePath(File sourceFile, File sourceFileRootDir) {
        Path dir = sourceFileRootDir.toPath();
        Path file = sourceFile.toPath();
        return dir.relativize(file);
    }

    public void copy(File source, Path targetPath) throws IOException {
        Path sourcePath = source.toPath();
        Files.createDirectories(targetPath.getParent());//make sure parent exists
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }


    public static void main(String args[]) throws IOException{
        File output = new File("/tmp/output");
        File input = new File("/tmp/staged/dist/ntp-server");
        Updater updater = new Updater(input, output);
        updater.performUpdate();

    }

}
