package com.example.demo.domain.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuidResultModel extends GeneralResultModel{
    private UUID id;
}
