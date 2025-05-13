package com.nicat.filevaultbackend.service;

import com.nicat.filevaultbackend.dao.entity.FileMetaData;
import io.minio.errors.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public interface FileMetaDataService {
    void upload(MultipartFile multipartFile, String password, String bucketName);

    FileMetaData download(Long id, String password);

    void update(Integer downloadLimit, Long fileId);

    byte[] downloadFileFromMinio(String fileName, String password, String bucketName) throws Exception;

    String presignedUrl(String bucketName, String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

}