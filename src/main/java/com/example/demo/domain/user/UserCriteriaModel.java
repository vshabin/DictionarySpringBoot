package com.example.demo.domain.user;

import com.example.demo.domain.common.PageModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserCriteriaModel extends PageModel {
    private String roleFilter;
    private String loginFilter;
    private String fullNameFilter;
    private String sortFilter;
}
