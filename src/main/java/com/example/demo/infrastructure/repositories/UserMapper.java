package com.example.demo.infrastructure.repositories;

import com.example.demo.domain.user.UserModelPost;
import com.example.demo.domain.user.UserModelReturn;
import com.example.demo.infrastructure.repositories.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface UserMapper {
    UserEntity toUserEntity(UserModelPost userModel);

    @Mapping(source = "userEntity.id", target = "id")
    @Mapping(source = "userEntity.login", target = "login")
    @Mapping(source = "userEntity.role", target = "role")
    @Mapping(source = "userEntity.fullName", target = "fullName")
    @Mapping(source = "userEntity.createdAt", target = "createdAt")
    @Mapping(source = "userEntity.archived", target = "archiveDate")
    UserModelReturn toUserModelReturn(UserEntity userEntity);

    List<UserModelReturn> toUserModelReturnList (List<UserEntity> userEntities);
}
