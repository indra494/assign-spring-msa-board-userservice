package com.indra.userservice.utils;

import org.springframework.util.StringUtils;

public class ObjectUtil {

     public static Object nvl(Object srcObj, Object targetObj) {
        if( StringUtils.isEmpty(srcObj) ) {
            return targetObj;
        }
        return srcObj;
    }
}
