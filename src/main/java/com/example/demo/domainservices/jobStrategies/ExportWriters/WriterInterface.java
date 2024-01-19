package com.example.demo.domainservices.jobStrategies.ExportWriters;

import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

public interface WriterInterface {
    void addData(List<?> modelList);
    void write(OutputStream outputStream) throws Exception;
}
