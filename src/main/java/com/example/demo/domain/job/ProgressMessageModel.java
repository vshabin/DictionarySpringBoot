package com.example.demo.domain.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressMessageModel {
    private int successCount;
    private int errorCount;
    private int allCount;
}
