package com.var.pdfutil.serviceimpl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.var.pdfutil.config.AppConfig;
import com.var.pdfutil.service.IFileSystemPdfFunctions;
import com.var.pdfutil.util.UniqueIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component("FileSystemPdfFunctionsImpl")
public class FileSystemPdfFunctionsImpl implements IFileSystemPdfFunctions {

    public static final String PDF = ".pdf";
    public static final String FILE_EXTENSION_SYMBOL = "\\.";
    public static final String MERGED_POSTFIX = "_merged";
    @Autowired
    private AppConfig config;

    @Override
    public void init() {
      //load config
          String location = config.getLocation();
        try {
            Files.createDirectories(Path.of(location));
        } catch (IOException e) {
            log.error("Error : {}",e.getCause());
            throw new RuntimeException("Error creating directories in file system");
        }
    }

    @Override
    public Set<String> storeFiles(MultipartFile[] files) {
        Set<String> paths = new HashSet<>();
        for(MultipartFile f:files){
            paths.add(storeFile(f));
        }
        return paths;
    }

    private String storeFile(MultipartFile file){
        String name = file.getOriginalFilename().split(FILE_EXTENSION_SYMBOL)[0];
        Path path = Path.of(config.getLocation()).resolve(name+ UniqueIdGenerator.getUUID()+ PDF);
        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Error {}", e.getCause());
            throw new RuntimeException("error saving file to file system");
        }
        return path.getFileName().normalize().toString();
    }

    @Override
    public Resource getFile(String name) {
            Path p = Path.of(config.getLocation() + name).normalize();
            Resource res = null;
            try {
                 res = new UrlResource(p.toUri());
                 if(!res.exists()){
                     log.error("resource does not exist on file system ");
                     throw new RuntimeException("resource does not exist");
                 }
            } catch (MalformedURLException e) {
                log.error("Error {}", e.getCause());
                throw new RuntimeException("Error while retrieving file "+name+"from file system");
            }
        return res;
    }

    @Override
    public String mergeFiles(String[] files) {
        Document mergedDocument = new Document();
        PdfCopy copy = null;
        String mergedFileName= files[0].split(FILE_EXTENSION_SYMBOL)[0]+ MERGED_POSTFIX +PDF;
        try {
             copy = new PdfCopy(mergedDocument, new FileOutputStream(config.getLocation()+mergedFileName));
             mergedDocument.open();
             for (String file : files){
                 Resource resource = getFile(file);
                 PdfReader pdfReader = new PdfReader(resource.getURL());
                 copy.addDocument(pdfReader);
                 pdfReader.close();
             }
        } catch (DocumentException e) {
            throw new RuntimeException(e.getCause());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getCause());
        } catch (IOException e) {
            throw new RuntimeException(e.getCause());
        }
        copy.close();
        mergedDocument.close();
        return mergedFileName;
    }
}
