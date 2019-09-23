package com.space.utils;

import javax.persistence.AttributeConverter;
import java.sql.Date;


public class DateAttributeConverter implements AttributeConverter<Long, Date> {
    @Override
    public Date convertToDatabaseColumn(Long attribute) {
        return new Date(attribute);
    }

    @Override
    public Long convertToEntityAttribute(Date dbData) {
        return dbData.getTime();
    }
}
