package com.heima.freemarker.test;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;

import java.io.FileInputStream;

public class MinioText {

    public static void main(String[] args) {

        try {
            FileInputStream fileInputStream = new FileInputStream("D:\\list.html");
            MinioClient minioClient = MinioClient.builder().credentials("minioadmin", "minioadmin").endpoint("http://192.168.50.128:9000").build();


            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object("list.html")
                    .contentType("text/html")
                    .bucket("leadnews")
                    .stream(fileInputStream, fileInputStream.available(), -1).build();
            minioClient.putObject(putObjectArgs);

            System.out.println("http://192.168.50.128:9000/leadnews/list.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
