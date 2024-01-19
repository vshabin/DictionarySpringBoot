package com.example.demo.domain.job.progress;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExportProgress {
    private int lastPage;
    private int pageSize;
    private int allCount;
}
