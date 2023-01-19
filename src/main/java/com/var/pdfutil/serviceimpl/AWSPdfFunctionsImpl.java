package com.var.pdfutil.serviceimpl;

import com.var.pdfutil.service.IAWSPdfFunctions;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component("AWSPdfFunctionsImpl")
public class AWSPdfFunctionsImpl implements IAWSPdfFunctions {
    @Override
    public void init() {

    }

    @Override
    public Set<String> storeFiles(MultipartFile[] file) {
        return null;
    }

    @Override
    public Resource getFile(String name) {
        return null;
    }

    @Override
    public String mergeFiles(String[] files) {
        return null;
    }
}
