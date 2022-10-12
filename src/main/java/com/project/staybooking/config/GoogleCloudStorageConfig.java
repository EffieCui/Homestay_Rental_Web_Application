package com.project.staybooking.config;

import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

// 根据credentials的信息建立和gcs的链接，可以往bucket上传文件
// 目的：返回storage对象。需要的参数credentials

@Configuration
public class GoogleCloudStorageConfig {

    @Bean
    public Storage storage() throws IOException {
        Credentials credentials = ServiceAccountCredentials.fromStream(
                getClass().getClassLoader().getResourceAsStream("credentials.json"));//读取ide的resource里文本文件的内容
        return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
    }
}
