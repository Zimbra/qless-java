package com.zimbra.qless;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



class OptsHelper {

    public static String get(Map<String, Object> opts, String opt) {
        return get(opts, opt, null);
    }
    
    public static String get(Map<String, Object> opts, String opt, String defaultValue) {
        if (opts == null) {
            return defaultValue;
        }
        Object value = opts.get(opt);
        if (value == null) {
            return defaultValue;
        }
        return value.toString();
    }
    
    public static List<String> getList(Map<String, Object> opts, String opt) {
        return getList(opts, opt, new ArrayList<String>());
    }
    
    @SuppressWarnings("unchecked")
    public static List<String> getList(Map<String, Object> opts, String opt, List<String> defaultValue) {
        if (opts == null) {
            return defaultValue;
        }
        Object value = opts.get(opt);
        if (value == null) {
            return defaultValue;
        }
        return (List<String>)value;
    }
}
