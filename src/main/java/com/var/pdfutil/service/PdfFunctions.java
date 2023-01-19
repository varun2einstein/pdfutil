package com.var.pdfutil.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface PdfFunctions {
    void init();

    Set<String> storeFiles(MultipartFile[] file);

    Resource getFile(String name);

    String mergeFiles(String[] files);
}
