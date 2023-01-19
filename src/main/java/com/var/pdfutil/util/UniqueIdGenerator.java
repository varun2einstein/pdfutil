package com.var.pdfutil.util;

import java.util.UUID;

public class UniqueIdGenerator {

    public static String getUUID(){
        return UUID.randomUUID().toString();
    }
}
