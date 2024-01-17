package com.example.demo.domain.job.params;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestParams {
    private String name;
    private int workCount;
//    public TestParams(//@JsonProperty(value = "name", required = true)
//                       String name){
//        this.name = name;
//    }
}
