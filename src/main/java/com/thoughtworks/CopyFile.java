package com.thoughtworks;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

@Slf4j
public class CopyFile {

    public static void copyFile(File source, String destination) throws IOException {
        //创建目的地文件夹
        File destinationFile = new File(destination);
        if (!destinationFile.exists()) {
            destinationFile.mkdir();
        }
        //如果source是文件夹，则在目的地址中创建新的文件夹
        if (source.isDirectory()) {
            File file = new File(destination + "\\" + source.getName());//用目的地址加上source的文件夹名称，创建新的文件夹
            file.mkdir();
            //得到source文件夹的所有文件及目录
            File[] files = source.listFiles();
            if (files.length == 0) {
                return;
            } else {
                for (int i = 0; i < files.length; i++) {
                    copyFile(files[i], file.getPath());
                }
            }

        }
        //source是文件，则用字节输入输出流复制文件
        else if (source.isFile()) {
            try (FileInputStream fis = new FileInputStream(source);) {
                //创建新的文件，保存复制内容，文件名称与源文件名称一致
                File newFile = new File(destination + "\\" + source.getName());
                if (!newFile.exists()) {
                    newFile.createNewFile();
                }

                FileOutputStream fos = new FileOutputStream(newFile);
                // 读写数据
                // 定义数组
                byte[] bytes = new byte[1024];
                // 定义长度
                int length;
                // 循环读取
                while ((length = fis.read(bytes)) != -1) {
                    // 写出数据
                    fos.write(bytes, 0, length);
                }
            } catch (IOException e) {
                log.info(e.getMessage());
            }
        }
    }
}
