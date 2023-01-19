package com.var.pdfutil.controller;

import com.var.pdfutil.service.PdfFunctions;
import com.var.pdfutil.util.FileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/pdf/files/api")
@CrossOrigin
public class PdfFunctionsController {

    public static final String PDF_FILES_API_DOWNLOAD = "/pdf/files/api/download/";
    @Autowired
    @Qualifier("GCPPdfFunctionsImpl")
    private PdfFunctions pdfFunctions;


    @PostMapping("/upload")
    public ResponseEntity<Set<FileResponse>> upload(@RequestParam("files") MultipartFile[]  files){
        Set<String> names = pdfFunctions.storeFiles(files);
        Set<FileResponse> filesResponse = new HashSet<>();
        for(String f: names){
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(PDF_FILES_API_DOWNLOAD)
                    .path(f)
                    .toUriString();
            FileResponse fr = new FileResponse(f, fileUrl, null);
            filesResponse.add(fr);
        }
        return ResponseEntity.ok(filesResponse);
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> download(@PathVariable String fileName){
        Resource resource = pdfFunctions.getFile(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+fileName+"\"")
                .header(HttpHeaders.CONTENT_TYPE,"application/pdf")
                .body(resource);
    }

    @GetMapping("/merge")
    public ResponseEntity<FileResponse> merge(@RequestParam("files") List<String> files){

        String mergedFileName = pdfFunctions.mergeFiles(files.toArray(new String[0]));

        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(PDF_FILES_API_DOWNLOAD)
                .path(mergedFileName)
                .toUriString();
        return ResponseEntity.ok(new FileResponse(mergedFileName, fileUrl, ""));
    }

}
