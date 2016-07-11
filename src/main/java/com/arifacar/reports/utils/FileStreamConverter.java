package com.arifacar.reports.utils;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Service
public class FileStreamConverter {

    public FileInputStream getFileInputStream(File file) {
        Assert.notNull(file, "File is empty");
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileInputStream;
    }
}