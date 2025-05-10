package com.nicat.filevaultbackend.service.impl;

import com.nicat.filevaultbackend.dao.entity.FileMetaData;
import com.nicat.filevaultbackend.dao.repository.FileMetaDataRepository;
import com.nicat.filevaultbackend.service.SchedulerService;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerServiceImpl implements SchedulerService {
    private final FileMetaDataRepository fileMetaDataRepository;
    private final MinioClient minioClient;

    @Override
    @Scheduled(fixedRate = 3600000)//every 1 hour
    public void expiredFileCleanUp() {
        String bucketName = "my-bucket";
        List<FileMetaData> expiredFiles =
                fileMetaDataRepository.findByExpirationDateBefore(LocalDateTime.now());
        for (FileMetaData file : expiredFiles) {
            try {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(file.getFileName())
                        .build());
                fileMetaDataRepository.deleteAll(expiredFiles);
                log.info("File deleted from MinIo");
                log.info("File deleted from database");
            } catch (Exception e) {
                System.out.println("MinIo file can not deleted :" + file.getFileName());
            }
        }
    }
}