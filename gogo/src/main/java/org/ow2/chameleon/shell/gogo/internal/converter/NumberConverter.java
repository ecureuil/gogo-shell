package org.ow2.chameleon.shell.gogo.internal.converter;

import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.service.command.Converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Loris Bouzonnet
 */
public class NumberConverter implements Converter {

    @ServiceProperty(name = Converter.CONVERTER_CLASSES, value = "java.lang.Number")
    private String supportedClasses;

    public Object convert(Class<?> toType, Object in) throws Exception {
        if (in instanceof Number && Number.class.isAssignableFrom(toType)) {
            return convertToNumber((Number) in, toType);
        }
        return null;
    }

    public Object convertToNumber(Number value, Class toType) throws Exception {
        if (AtomicInteger.class == toType) {
            return new AtomicInteger((Integer) convertToNumber(value, Integer.class));
        } else if (AtomicLong.class == toType) {
            return new AtomicLong((Long) convertToNumber(value, Long.class));
        } else if (Integer.class == toType) {
            return value.intValue();
        } else if (Short.class == toType) {
            return value.shortValue();
        } else if (Long.class == toType) {
            return value.longValue();
        } else if (Float.class == toType) {
            return value.floatValue();
        } else if (Double.class == toType) {
            return value.doubleValue();
        } else if (Byte.class == toType) {
            return value.byteValue();
        } else if (BigInteger.class == toType) {
            return new BigInteger(value.toString());
        } else if (BigDecimal.class == toType) {
            return new BigDecimal(value.toString());
        } else {
            return null;
        }
    }

    public CharSequence format(Object o, int i, Converter converter) throws Exception {
        return null;
    }

}
