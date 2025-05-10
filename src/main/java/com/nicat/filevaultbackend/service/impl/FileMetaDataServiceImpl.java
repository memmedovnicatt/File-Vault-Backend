package com.nicat.filevaultbackend.service.impl;

import com.nicat.filevaultbackend.dao.entity.FileMetaData;
import com.nicat.filevaultbackend.dao.repository.FileMetaDataRepository;
import com.nicat.filevaultbackend.model.enums.exception.child.DownloadLimitExceededException;
import com.nicat.filevaultbackend.model.enums.exception.child.NotFoundException;
import com.nicat.filevaultbackend.model.enums.exception.child.PasswordMismatchException;
import com.nicat.filevaultbackend.service.FileMetaDataService;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;


@Service
@Slf4j
@RequiredArgsConstructor
public class FileMetaDataServiceImpl implements FileMetaDataService {
    private final FileMetaDataRepository fileMetaDataRepository;
    private final MinioClient minioClient;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void upload(MultipartFile multipartFile, String password, String bucketName) {
        log.info("Upload method was started");
        if (multipartFile.isEmpty()) {
            log.info("File is empty,can not uploaded");
            throw new NotFoundException("File is empty,can not uploaded");
        }
        log.info("File was found ");
        String fileName = multipartFile.getOriginalFilename();
        try {
            boolean isExist = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket '{}' created : ", bucketName);
            }
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                            .contentType(multipartFile.getContentType())
                            .build()
            );

            log.info("File successfully uploaded to MinIo: {}", fileName);
            FileMetaData fileMetaData = new FileMetaData();
            fileMetaData.setPassword(passwordEncoder.encode(password));
            fileMetaData.setFileName(fileName);
            fileMetaData.setMimeType(multipartFile.getContentType());
            fileMetaData.setSize(multipartFile.getSize());
            fileMetaData.setFileDataBytea(multipartFile.getBytes());
            fileMetaData.setStoragePath(bucketName);

            fileMetaDataRepository.save(fileMetaData);
            log.info("Successfully saved in database");

        } catch (Exception e) {
            log.error("Occur error when file uploaded: {}", e.getMessage(), e);
            throw new RuntimeException("Occur error when file uploaded", e);
        }
    }


    @Override
    public FileMetaData download(Long id, String password) {
        log.info("Download method was started");
        FileMetaData fileMetaData = fileMetaDataRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("File not found"));
        log.info("File was found");
        if (fileMetaData.getDownloadLimit() == 0) {
            throw new DownloadLimitExceededException("Download limit is equal to zero");
        }
        if (!passwordEncoder.matches(password, fileMetaData.getPassword())) {
            throw new PasswordMismatchException("Password not correct");
        }
        return fileMetaData;
    }

    @Override
    public void update(Integer downloadLimit, Long fileId) {
        log.info("update method was started");
        FileMetaData fileMetaData = fileMetaDataRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("File not found"));
        fileMetaData.setDownloadLimit(downloadLimit);
        fileMetaData.setUploadedAt(LocalDateTime.now());
        fileMetaDataRepository.save(fileMetaData);
    }

    @Override
    public byte[] downloadFileFromMinio(String fileName, String password, String bucketName) throws Exception {
        FileMetaData fileMetaData = fileMetaDataRepository.findByFileName(fileName)
                .orElseThrow(() -> new NotFoundException("File not found"));
        log.info("File found");
        if (!passwordEncoder.matches(password, fileMetaData.getPassword())) {
            throw new PasswordMismatchException("Password not correct");
        }
        log.info("Password is correct");
        boolean isExistBucket = fileMetaDataRepository.existsByStoragePath(bucketName);
        log.info("Is the bucket available? : {}", isExistBucket);
        if (!isExistBucket) {
            throw new NotFoundException("Bucket not exist");
        }
        log.info("Bucket was found");

        Integer newDownloadLimit = fileMetaData.getDownloadLimit();
        if (newDownloadLimit <= 0) {
            throw new DownloadLimitExceededException("Download limit is zero,please update download limit for file");
        }
        newDownloadLimit--;
        Integer newCurrentDownload = fileMetaData.getCurrentDownload();
        newCurrentDownload = (newCurrentDownload == null) ? 1 : newCurrentDownload + 1;
        fileMetaData.setCurrentDownload(newCurrentDownload);
        fileMetaData.setDownloadLimit(newDownloadLimit);
        fileMetaDataRepository.save(fileMetaData);

        try (GetObjectResponse response = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        )) {
            return response.readAllBytes();
        }
    }
}