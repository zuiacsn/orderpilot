package com.orderpilot.system.controller;

import com.orderpilot.system.model.SystemInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
public class SystemInfoController {

    private final SystemInfo systemInfo;

    public SystemInfoController(@Value("${info.app.name}") String name,
                                @Value("${info.app.version}") String version,
                                @Value("${info.app.stage}") String stage) {
        this.systemInfo = new SystemInfo(name, version, stage);
    }

    @GetMapping("/info")
    public SystemInfo info() {
        return systemInfo;
    }
}
