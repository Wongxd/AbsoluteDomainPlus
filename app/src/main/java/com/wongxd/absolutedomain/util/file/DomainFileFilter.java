package com.wongxd.absolutedomain.util.file;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DomainFileFilter implements FileFilter {

    public static String ImgFileExtension = ".domainImg";

    public static String videoFileExtension = ".domainVideo";

    public static String txtFileExtension = ".domainTxt";


    private String fileExtension = ImgFileExtension;


    public DomainFileFilter() {
        this(ImgFileExtension);
    }

    public DomainFileFilter(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory())
            return true;
        else {
            String name = file.getName().toLowerCase();
            return name.endsWith(fileExtension.toLowerCase());
        }

    }


    /**
     * get all the music file in the rootpath.
     *
     * @param rootPath
     */
    public List<File> getAllFilePath(String rootPath) {

        List<File> results = new ArrayList<>();

        File file = new File(rootPath);
        if (!file.exists()) try {
            File dir = file.getParentFile();
            dir.mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File[] files = file.listFiles(this);
        if (files == null) return null;
        for (File f : files) {
            if (f == null) continue;
            if (f.isDirectory()) {
                getAllFilePath(f.getPath());
            } else {
                results.add(f);
            }
        }

        return results;

    }


}  