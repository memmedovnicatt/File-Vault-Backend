package com.nicat.filevaultbackend.service;

import com.nicat.filevaultbackend.dao.entity.FileMetaData;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface FileMetaDataService {
    void upload(MultipartFile multipartFile, String password, String bucketName);

    FileMetaData download(Long id, String password);

    void update(Integer downloadLimit, Long fileId);

    byte[] downloadFileFromMinio(String fileName, String password, String bucketName) throws Exception;
}