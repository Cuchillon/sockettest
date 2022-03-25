package com.ferick.config;

import java.util.regex.Pattern;

public class ConfigUtils {

    public static String parseConfigFileName(String template) {
        var regex = ".*/?.+\\.(properties|yaml|yml|json)-config$";
        var result = "";
        var p = Pattern.compile(regex);
        var m = p.matcher(template);
        while (m.find()) {
            var matchedString = m.group();
            result = matchedString
                    .replaceFirst(".*/", "")
                    .replaceFirst("\\.(properties|yaml|yml|json)-config", "");
        }
        return result;
    }
}
