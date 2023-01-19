package com.var.pdfutil.util;

import com.google.auth.Credentials;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GCPStorageOpsTest {

    private  static GCPStorageOps gcpStorageOps;
    @BeforeAll
    public static void init(){
        gcpStorageOps = new GCPStorageOps();
    }

    @Test
    void getCredentials() {
        Credentials credentials = gcpStorageOps.getCredentials();
        assertTrue(credentials!=null);

        String s = "aasdadw0342-.pdf,asdfsdfrsf.pdf";

        String[] as = s.split(",");

        System.out.println("");

    }
}