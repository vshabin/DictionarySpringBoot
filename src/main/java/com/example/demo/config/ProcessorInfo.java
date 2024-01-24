package com.example.demo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ProcessorInfo {
    @Value("${HOSTNAME:#{null}}")
    private String hostName;

    @Value("${COMPUTERNAME:#{null}}")
    private String computerName;
}
