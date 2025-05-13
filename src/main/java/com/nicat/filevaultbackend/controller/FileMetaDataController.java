package com.nicat.filevaultbackend.controller;

import com.nicat.filevaultbackend.dao.entity.FileMetaData;
import com.nicat.filevaultbackend.service.FileMetaDataService;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileMetaDataController {

    private final FileMetaDataService fileMetaDataService;

    @PostMapping(path = "/uploads", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile multipartFile,
                                    @RequestParam String password,
                                    @RequestParam String bucketName) {
        fileMetaDataService.upload(multipartFile, password, bucketName);
        return ResponseEntity.ok("File uploaded successfully");
    }


    @Operation(summary = "Download from only database")
    @GetMapping("/downloads/{fileId}")
    public ResponseEntity<byte[]> download(@PathVariable Long fileId,
                                           @RequestParam String password) {
        FileMetaData fileData = fileMetaDataService.download(fileId, password);
        byte[] file = fileData.getFileDataBytea();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileData.getMimeType()))
                .body(file);
    }

    @Operation(summary = "Download from only MinIo")
    @GetMapping("/downloads")
    public ResponseEntity<byte[]> download(@RequestParam String fileName,
                                           @RequestParam String password,
                                           @RequestParam String bucketName) throws Exception {
        byte[] fileBytes = fileMetaDataService.downloadFileFromMinio(fileName, password, bucketName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileBytes);

    }

    @Operation(summary = "Update download limit for users")
    @PatchMapping("downloads/{downloadLimit}")
    public ResponseEntity<Void> update(@PathVariable Integer downloadLimit,
                                       @RequestParam Long fileId) {
        fileMetaDataService.update(downloadLimit, fileId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Generate url for minio bucket and file name")
    @ApiResponse(responseCode = "200", description = "Url successfully generated ")
    @PostMapping("/generate-url")
    public ResponseEntity<String> generatePresignedUrl(@RequestParam String bucketName,
                                                       @RequestParam String fileName) throws Exception {
        String url = fileMetaDataService.presignedUrl(bucketName, fileName);
        return ResponseEntity.ok(url);
    }
}