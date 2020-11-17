package org.greenearth.administrator.sample.mapper;

import org.greenearth.administrator.config.database.KfdConnection;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@KfdConnection
public interface KfdMapper {

    String getZone(String city);
}
