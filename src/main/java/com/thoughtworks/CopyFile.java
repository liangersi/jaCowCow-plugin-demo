package com.thoughtworks;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
public class CopyFile {

    public static void copyFile(File source, String destination) throws IOException {

        File destinationFile = new File(destination);
        if (!destinationFile.exists()) {
            destinationFile.mkdir();
        }

        if (source.isFile()) {
            try (FileInputStream fis = new FileInputStream(source);) {
                File newFile = new File(destination + "\\" + source.getName());
                if (!newFile.exists()) {
                    newFile.createNewFile();
                }

                FileOutputStream fos = new FileOutputStream(newFile);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) != -1) {
                    fos.write(bytes, 0, length);
                }
            } catch (IOException e) {
                log.info(e.getMessage());
            }
        }
    }
}
