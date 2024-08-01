package com.heima.freemarker.test;

import com.heima.file.service.FileStorageService;
import com.heima.freemarker.FreeMarkerDemoApplication;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest(classes = FreeMarkerDemoApplication.class)
@RunWith(SpringRunner.class)
public class MinioText {

    @Autowired
    private FileStorageService fileStorageService;


    @Test
    public void text() throws FileNotFoundException {

        FileInputStream fileInputStream = new FileInputStream("D:\\list.html");
        String s = fileStorageService.uploadHtmlFile("", "list.html", fileInputStream);
        System.out.println(s);

    }


    /*public static void main(String[] args) {

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
    }*/
}
