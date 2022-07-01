package com.example.demo.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@Slf4j
public class DemoController {

    @Value("${config.path}")
    private String filePath;
    @Value("${service.schema}")
    private String serviceSchema;
    @Value("${service.name}")
    private String serviceName;

    @GetMapping("/getSysInfo")
    public Map<String, String> getSysInfo(){
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Yaml yaml = new Yaml();
        Map<String, String> data = yaml.load(inputStream);
        System.out.println(data);
        return data;
    }
//    @Scheduled(fixedRate = 10000)
    @Scheduled(cron = "${updateCorn}")
    public void putSystemInfo() {
        Map<String, String> data = getSysInfo();
        String endpoint = serviceSchema + serviceName;
        URIBuilder builder;
        Gson gson = new Gson();
        CloseableHttpClient client = HttpClients.createDefault();
        if (client == null) {
            log.error("Initialization is failed but the failure is ignored. Please check the initialization of the instance");
        }
        try {
            builder = new URIBuilder(endpoint);
            builder.setCharset(StandardCharsets.UTF_8);
            builder.setPath("/system/setSystemInfo");
            HttpPost post = new HttpPost(builder.build());
            StringEntity entity = new StringEntity(gson.toJson(data), StandardCharsets.UTF_8);
            entity.setContentType("application/json");
            post.setEntity(entity);
            //执行请求
            CloseableHttpResponse response = client.execute(post);
            if (response == null) {
                log.error("Request fail, empty response.");
            }
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                log.info("success, 发送成功");
            } else {
                log.info("failed, 发送失败");
                log.info(String.valueOf(statusCode));
                log.info(response.getStatusLine().toString());
            }
        } catch (URISyntaxException e) {
            log.error("Error URI endpoint. ", e);
        } catch (ClientProtocolException e) {
            log.error("ClientProtocolException. ", e);
        } catch (IOException e) {
            log.error("IOException. ", e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                log.error("Close client error. ", e);
            }
        }
    }
}
