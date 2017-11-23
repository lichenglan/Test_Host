package com.techjumper.polyhome.polyhomebhost.entity.sql;

import com.google.auto.value.AutoValue;
import com.squareup.sqldelight.RowMapper;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 2016/10/29
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
@AutoValue
public abstract class PolyLog implements PolyLogModel {
    public static final Factory<PolyLog> FACTORY = new Factory<>((Creator<PolyLog>) AutoValue_PolyLog::new);

    public static final RowMapper<PolyLog> MAPPER = FACTORY.select_allMapper();
}
