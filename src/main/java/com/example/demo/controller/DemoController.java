package com.example.demo.controller;

import com.example.demo.util.SystemUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@PropertySource(value = {"file:${config.path}"}, encoding="utf-8")
public class DemoController {

    @Value("${hostname}")
    private String hostname;
    @Value("${cpu}")
    private String cpu;
    @Value("${mem}")
    private String mem;
    @Value("${disk}")
    private String disk;
    private static Map<String, String> usage = new HashMap<String, String>();

    @GetMapping("/info")
    public Map<String, String> info(){
        Map<String, String> map = new HashMap<>();
        map.put("cpu", SystemUtil.getCPUUtilization());
        map.put("mem", SystemUtil.getMemUtilization());
        map.put("dist", SystemUtil.getDiskUtilization());
        map.put("ip", SystemUtil.getIpAddress());
        return map;
    }

    @GetMapping("/getInfo")
    public Map<String, String> getInfo(){
        return usage;
    }

//    @PostMapping("/setSysInfo")
//    public void setSysInfo(@RequestBody Map<String, String> map){
//        String hostname = map.get("hostname");
//        String cpu = map.get("cpu");
//        String mem = map.get("mem");
//        String disk = map.get("disk");
//        usage.put("hostname", hostname);
//        usage.put("cpu", cpu);
//        usage.put("mem", mem);
//        usage.put("disk", disk);
//    }

    @GetMapping("/getSysInfo")
    public Map<String, String> getSysInfo(){
        usage.put("hostname", hostname);
        usage.put("cpu", cpu);
        usage.put("mem", mem);
        usage.put("disk", disk);
        return usage;
    }
}
