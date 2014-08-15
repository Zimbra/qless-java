/*
 * ***** BEGIN LICENSE BLOCK *****
 * Copyright (C) 2014 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

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