package com.kiwoom.administrator.sample.mapper;

import com.kiwoom.administrator.config.database.KwsvcConnection;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@KwsvcConnection
public interface KwsvcMapper {

    String getAccount(String email);
}
