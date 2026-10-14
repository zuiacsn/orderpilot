package com.orderpilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** OrderPilot 的统一启动入口，扫描所有 com.orderpilot 业务模块。 */
@SpringBootApplication
public class OrderPilotApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderPilotApplication.class, args);
    }
}
