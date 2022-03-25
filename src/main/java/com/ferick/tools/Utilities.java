package com.ferick.tools;

import java.util.HashMap;
import java.util.Map;

public class Utilities {

    /**
     * Method to transform pair strings to mutable {@link Map}
     *
     * @param entries range of strings which have to set key-value pairs
     * @return map if number of strings is even
     * @throws IllegalArgumentException if number of strings is odd
     */
    public static Map<String, String> asMap(String... entries) {
        if (entries.length % 2 != 0) {
            throw new IllegalArgumentException("Odd number of elements in key-value pairs!");
        }

        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < entries.length; i += 2) {
            map.put(entries[i], entries[i + 1]);
        }

        return map;
    }
}
