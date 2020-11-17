package org.greenearth.administrator.sample.mapper;

import org.greenearth.administrator.config.database.KwsvcConnection;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@KwsvcConnection
public interface KwsvcMapper {

    String getAccount(String email);
}
