package kansaidbstudy;

import java.time.LocalDate;

import org.sql2o.converters.Converter;
import org.sql2o.converters.ConverterException;

public class LocalDateConverter implements Converter<LocalDate> {

    @Override
    public LocalDate convert(Object val) throws ConverterException {
        if (val instanceof java.sql.Date) {
            return ((java.sql.Date)val).toLocalDate();
        }
        return null;
    }

    @Override
    public Object toDatabaseParam(LocalDate val) {
        if (val == null) {
            return null;
        } 
        return java.sql.Date.valueOf(val);
    }    
}