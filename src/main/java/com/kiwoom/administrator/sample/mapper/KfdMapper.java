package com.kiwoom.administrator.sample.mapper;

import com.kiwoom.administrator.config.database.KfdConnection;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@KfdConnection
public interface KfdMapper {

    String getZone(String city);
}
