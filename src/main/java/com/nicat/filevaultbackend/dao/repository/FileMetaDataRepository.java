package com.nicat.filevaultbackend.dao.repository;

import com.nicat.filevaultbackend.dao.entity.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileMetaDataRepository extends JpaRepository<FileMetaData, Long> {
    Optional<FileMetaData> findByFileName(String fileName);

    List<FileMetaData> findByExpirationDateBefore(LocalDateTime time);

    boolean existsByStoragePath(String bucketName);
}