package com.example.demo.domainservices.jobStrategies.ExportWriters;

import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

public interface WriterInterface<T> {
    void addData(List<T> modelList);
    void write(OutputStream outputStream) throws Exception;

    void preWrite();
    void postWrite();
}
