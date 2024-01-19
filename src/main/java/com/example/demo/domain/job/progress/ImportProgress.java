package com.example.demo.domain.job.progress;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImportProgress {
    private int lastRow;
    private int attAll;
}
