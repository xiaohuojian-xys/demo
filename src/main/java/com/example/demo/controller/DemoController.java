package com.example.demo.controller;

import com.example.demo.util.SystemUtil;
import com.google.gson.JsonObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DemoController {

    public static Map<String, String> usage = new HashMap<String, String>();

    @GetMapping("/info")
    public Map<String, String> info(){
        Map<String, String> map = new HashMap<>();
        map.put("cpu", SystemUtil.getCPUUtilization());
        map.put("mem", SystemUtil.getMemUtilization());
        map.put("dist", SystemUtil.getDiskUtilization());
        map.put("ip", SystemUtil.getIpAddress());
        return map;
    }

    @GetMapping("/getinfo")
    public Map<String, String> getInfo(){
        return usage;
    }

    @PostMapping("/setSysInfo")
    public void setSysInfo(@RequestBody Map<String, String> map){
        String cpu = map.get("cpu");
        String mem = map.get("mem");
        String disk = map.get("disk");
        usage.put("cpu", cpu);
        usage.put("mem", mem);
        usage.put("disk", disk);
    }
}
