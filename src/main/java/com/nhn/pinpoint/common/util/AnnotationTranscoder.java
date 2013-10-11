package com.nhn.pinpoint.common.util;


import com.nhn.pinpoint.thrift.dto.TAnnotation;
import com.nhn.pinpoint.thrift.dto.TAnnotationValue;

import java.io.*;

public class AnnotationTranscoder {

    private static final String DEFAULT_CHARSET = "UTF-8";

    static final byte CODE_STRING = 0;
    static final byte CODE_NULL = 1;
    static final byte CODE_INT = 2;
    static final byte CODE_LONG = 3;

    static final byte CODE_BOOLEAN_TRUE = 4;
    static final byte CODE_BOOLEAN_FALSE = 5;

    static final byte CODE_BYTEARRAY = 6;
    static final byte CODE_BYTE = 7;

    static final byte CODE_SHORT = 8;
    static final byte CODE_FLOAT = 9;
    static final byte CODE_DOUBLE = 10;
    static final byte CODE_TOSTRING = 11;

    protected final TranscoderUtils tu = new TranscoderUtils(true);


    public Object getMappingValue(TAnnotation annotation) {
        final TAnnotationValue value = annotation.getValue();
        if (value == null) {
            return null;
        }
        return value.getFieldValue();
    }


    public Object decode(byte dataType, byte[] data) {
        switch (dataType) {
            case CODE_STRING:
                return decodeString(data);
            case CODE_BOOLEAN_TRUE:
                return Boolean.TRUE;
            case CODE_BOOLEAN_FALSE:
                return Boolean.FALSE;
            case CODE_INT:
                return tu.decodeInt(data);
            case CODE_LONG:
                return tu.decodeLong(data);
            case CODE_BYTE:
                return tu.decodeByte(data);
            case CODE_SHORT:
                return tu.decodeShort(data);
            case CODE_FLOAT:
                return Float.intBitsToFloat(tu.decodeInt(data));
            case CODE_DOUBLE:
                return Double.longBitsToDouble(tu.decodeLong(data));
            case CODE_BYTEARRAY:
                return data;
            case CODE_NULL:
                return null;
            case CODE_TOSTRING:
                return decodeString(data);
        }
        throw new RuntimeException("unsupported DataType:" + dataType);
    }

    public byte getTypeCode(Object o) {
        if (o == null) {
            return CODE_NULL;
        }
        if (o instanceof String) {
            return CODE_STRING;
        } else if (o instanceof Long) {
            return CODE_LONG;
        } else if (o instanceof Integer) {
            return CODE_INT;
        } else if (o instanceof Boolean) {
            if (Boolean.TRUE.equals(o)) {
                return CODE_BOOLEAN_TRUE;
            }
            return CODE_BOOLEAN_FALSE;
        } else if (o instanceof Byte) {
            return CODE_BYTE;
        } else if (o instanceof Short) {
            return CODE_SHORT;
        } else if (o instanceof Float) {
            // thrift에서 지원안함.
            return CODE_FLOAT;
        } else if (o instanceof Double) {
            return CODE_DOUBLE;
        } else if (o instanceof byte[]) {
            return CODE_BYTEARRAY;
        }
        return CODE_TOSTRING;
    }

    public byte[] encode(Object o, int typeCode) {
        switch (typeCode) {
            case CODE_STRING:
                return encodeString((String) o);
            case CODE_INT:
                return tu.encodeInt((Integer) o);
            case CODE_BOOLEAN_TRUE:
                return new byte[0];
            case CODE_BOOLEAN_FALSE:
                return new byte[0];
            case CODE_LONG:
                return tu.encodeLong((Long) o);
            case CODE_BYTE:
                return tu.encodeByte((Byte) o);
            case CODE_SHORT:
                return tu.encodeShort((Short) o);
            case CODE_FLOAT:
                return tu.encodeInt(Float.floatToRawIntBits((Float) o));
            case CODE_DOUBLE:
                return tu.encodeLong(Double.doubleToRawLongBits((Double) o));
            case CODE_BYTEARRAY:
                return (byte[]) o;
            case CODE_NULL:
                return null;
            case  CODE_TOSTRING:
                String str = o.toString();
                return encodeString(str);
        }
        throw new RuntimeException("unsupported DataType:" + typeCode + " data:" + o);
    }

    /**
     * Decode the string with the current character set.
     */
    protected String decodeString(byte[] data) {
        if (data == null) {
            return "";
        }
        try {
            return new String(data, DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encode a string into the current character set.
     */
    protected byte[] encodeString(String in) {
        byte[] rv = null;
        try {
            rv = in.getBytes(DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return rv;
    }
}
