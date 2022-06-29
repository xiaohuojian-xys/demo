package com.example.demo.controller;

import com.example.demo.util.SystemUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class DemoController {
    @GetMapping("/info")
    public Map<String, String> getInfo(){
        Map<String, String> map = new HashMap<>();
        map.put("cpu", SystemUtil.getCPUUtilization());
        map.put("mem", SystemUtil.getMemUtilization());
        map.put("dist", SystemUtil.getDiskUtilization());
        map.put("ip", SystemUtil.getIpAddress());
        return map;
    }
}
