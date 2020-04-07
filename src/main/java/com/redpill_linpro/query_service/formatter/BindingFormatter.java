package com.redpill_linpro.query_service.formatter;

import java.util.ArrayList;
import java.util.List;

public class BindingFormatter {
    private static List<String> compressedBindings = new ArrayList<>();

    private BindingFormatter() { }

    public static List<String> compressList(List<String> bindings) {

        for (int i = 0; i < bindings.size(); i+=2){
            compressedBindings.add(bindings.get(i) + "," + bindings.get(i + 1));
        }
        return compressedBindings;
    }
}
