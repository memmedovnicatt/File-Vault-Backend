package com.nicat.filevaultbackend.dao.entity;

import com.nicat.filevaultbackend.model.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "files")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileMetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "file_data")
    byte[] fileDataBytea;

    @Column(name = "file_name")
    String fileName;

    @Column(name = "size")
    Long size;

    @Column(name = "mime_type")
    String mimeType;

    @Column(name = "uploaded_at")
    LocalDateTime uploadedAt;

    @Builder.Default
    @Column(name = "expiration_date")
    LocalDateTime expirationDate = LocalDateTime.now().plusDays(1);

    @Column(name = "download_limit")
    @Builder.Default
    Integer downloadLimit = 5;

    @Column(name = "current_download")
    Integer currentDownload;

    @Column(name = "password")
    String password;

    @Column(name = "storage_path")
    String storagePath;

    @PrePersist
    protected void onCreate() {
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
    }
}