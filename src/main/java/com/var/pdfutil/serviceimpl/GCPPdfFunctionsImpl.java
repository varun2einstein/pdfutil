package com.var.pdfutil.serviceimpl;

import com.google.auth.Credentials;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.var.pdfutil.service.IGCPPdfFunctions;
import com.var.pdfutil.util.GCPStorageOps;
import com.var.pdfutil.util.UniqueIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Component("GCPPdfFunctionsImpl")
@Slf4j
public class GCPPdfFunctionsImpl implements IGCPPdfFunctions {

    public static final String PDF = ".pdf";
    public static final String FILE_EXTENSION_SYMBOL = "\\.";
    public static final String MERGED_POSTFIX = "_merged";
    private static final String ID = "_ID_";

    @Autowired
    private GCPStorageOps gcpStorageOps;

    @Override
    public void init() {

    }

    @Override
    public Set<String> storeFiles(MultipartFile[] files) {
        Set<String> names = new HashSet();
        for(MultipartFile file: files){
            try {
                String name = file.getOriginalFilename().split(FILE_EXTENSION_SYMBOL)[0];
                String fileNameOnServer = name+ID+ UniqueIdGenerator.getUUID()+ PDF;
                names.add(fileNameOnServer);
                gcpStorageOps.uploadObjectFromMemory(fileNameOnServer, file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Error uploading file, check file contents");
            }
        }
        return names;
    }

    @Override
    public Resource getFile(String name) {
        byte[] downloadedFile = gcpStorageOps.downloadObjectIntoMemory(name);
         return new ByteArrayResource(downloadedFile, name) ;
    }

    @Override
    public String mergeFiles(String[] files) {
        Document mergedDocument = new Document();
        PdfCopy copy = null;
        String mergedFileName= files[0].split(FILE_EXTENSION_SYMBOL)[0]+ MERGED_POSTFIX +PDF;
        ByteArrayOutputStream inMemoryOutStream = new ByteArrayOutputStream();
        try {
            copy = new PdfCopy(mergedDocument, inMemoryOutStream);
            mergedDocument.open();
            for (String file : files){
                PdfReader pdfReader = new PdfReader(gcpStorageOps.downloadObjectIntoMemory(file));
                copy.addDocument(pdfReader);
                pdfReader.close();
            }
        } catch (DocumentException e) {
            throw new RuntimeException(e.getCause());
        } catch (IOException e) {
            throw new RuntimeException(e.getCause());
        }finally {
            copy.close();
            mergedDocument.close();
        }
        try {
            gcpStorageOps.uploadObjectFromMemory(mergedFileName, inMemoryOutStream.toByteArray());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error uploading merged file");
        }
        return mergedFileName;
    }
}
