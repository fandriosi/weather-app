package com.andriosi.weather.storage;

import com.andriosi.weather.config.StorageProperties;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class FileStorageService {

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    private static final Duration DEFAULT_PRESIGN_DURATION = Duration.ofMinutes(10);

    private final StorageProperties properties;
    private volatile S3Client s3Client;
    private volatile S3Presigner s3Presigner;

    public FileStorageService(StorageProperties properties) {
        this.properties = properties;
    }

    public List<StoredFileInfo> storeSensorFiles(UUID sensorId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }

        return switch (properties.getType()) {
            case S3 ->
                storeOnS3(sensorId, files);
            case LOCAL ->
                storeOnLocal(sensorId, files);
        };
    }

    public Resource loadLocalResource(String storageKey) {
        String basePath = properties.getLocal().getBasePath();
        Path baseDir = Paths.get(basePath).toAbsolutePath().normalize();
        Path targetPath = baseDir.resolve(storageKey).normalize();
        if (!targetPath.startsWith(baseDir)) {
            throw new IllegalArgumentException("Invalid storage key");
        }
        return new PathResource(targetPath);
    }

    public URI createPresignedDownloadUrl(String storageKey,
            String contentType,
            String originalName) {
        StorageProperties.S3 s3 = properties.getS3();
        String bucket = requireValue(s3.getBucket(), "S3 bucket is required");
        String region = requireValue(s3.getRegion(), "S3 region is required");
        String disposition = buildContentDisposition(originalName);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(storageKey)
                .responseContentType(normalizeContentType(contentType))
                .responseContentDisposition(disposition)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(DEFAULT_PRESIGN_DURATION)
                .getObjectRequest(getObjectRequest)
                .build();

        return URI.create(getS3Presigner(region).presignGetObject(presignRequest).url().toString());
    }

    private List<StoredFileInfo> storeOnLocal(UUID sensorId, List<MultipartFile> files) {
        String basePath = properties.getLocal().getBasePath();
        String prefix = "sensors/" + sensorId;
        Path baseDir = Paths.get(basePath).resolve(prefix);
        List<StoredFileInfo> results = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }

            String originalName = normalizeFileName(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + "_" + originalName;
            Path targetPath = baseDir.resolve(fileName).normalize();
            String contentType = normalizeContentType(file.getContentType());

            try {
                Files.createDirectories(targetPath.getParent());
                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException ex) {
                throw new IllegalStateException("Failed to store file locally", ex);
            }

            String storageKey = prefix + "/" + fileName;
            results.add(new StoredFileInfo(
                    StorageType.LOCAL,
                    storageKey,
                    null,
                    originalName,
                    contentType,
                    file.getSize()
            ));
        }

        return results;
    }

    public void deleteFile(String storageKey, StorageType storageType) {
        if (storageKey == null || storageKey.isEmpty()) {
            throw new IllegalArgumentException("Storage key is required");
        }

        switch (storageType) {
            case S3 ->
                deleteFromS3(storageKey);
            case LOCAL ->
                deleteFromLocal(storageKey);
        }
    }

    public Resource getFile(String storageKey, StorageType storageType) {
        if (storageKey == null || storageKey.isEmpty()) {
            throw new IllegalArgumentException("Storage key is required");
        }

        return switch (storageType) {
            case S3 -> {
                StoredFileInfo fileInfo = getS3FileInfo(storageKey);
                yield loadS3ObjectAsResource(fileInfo);
            }
            case LOCAL ->
                loadLocalResource(storageKey);
        };
    }

    private Resource loadS3ObjectAsResource(StoredFileInfo fileInfo) {
        StorageProperties.S3 s3 = properties.getS3();
        String bucket = requireValue(s3.getBucket(), "S3 bucket is required");
        String region = requireValue(s3.getRegion(), "S3 region is required");

        try {
            ResponseBytes<GetObjectResponse> bytes = getS3Client(region)
                    .getObjectAsBytes(builder -> builder.bucket(bucket).key(fileInfo.getStorageKey()));
            return new ByteArrayResource(bytes.asByteArray());
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to load file from S3", ex);
        }
    }

    private void deleteFromS3(String storageKey) {
        StorageProperties.S3 s3 = properties.getS3();
        String bucket = requireValue(s3.getBucket(), "S3 bucket is required");
        String region = requireValue(s3.getRegion(), "S3 region is required");

        try {
            getS3Client(region).deleteObject(builder -> builder.bucket(bucket).key(storageKey));
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to delete file from S3", ex);
        }
    }

    private StoredFileInfo getS3FileInfo(String storageKey) {
        StorageProperties.S3 s3 = properties.getS3();
        String bucket = requireValue(s3.getBucket(), "S3 bucket is required");
        String region = requireValue(s3.getRegion(), "S3 region is required");

        try {
            var headObject = getS3Client(region).headObject(builder -> builder.bucket(bucket).key(storageKey));
            String originalName = headObject.metadata().getOrDefault("original-name", Paths.get(storageKey).getFileName().toString());
            return new StoredFileInfo(
                    StorageType.S3,
                    storageKey,
                    "s3://" + bucket + "/" + storageKey,
                    originalName,
                    headObject.contentType(),
                    headObject.contentLength()
            );
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to get file info from S3", ex);
        }
    }

    private void deleteFromLocal(String storageKey) {
        StorageProperties.Local local = properties.getLocal();
        String basePath = requireValue(local.getBasePath(), "Local base path is required");
        Path filePath = Paths.get(basePath).resolve(storageKey).normalize();

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to delete file from local storage", ex);
        }
    }

    private List<StoredFileInfo> storeOnS3(UUID sensorId, List<MultipartFile> files) {
        StorageProperties.S3 s3 = properties.getS3();
        String bucket = requireValue(s3.getBucket(), "S3 bucket is required");
        String region = requireValue(s3.getRegion(), "S3 region is required");
        String prefix = normalizePrefix(s3.getPrefix());
        List<StoredFileInfo> results = new ArrayList<>();
        S3Client client = getS3Client(region);

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }

            String originalName = normalizeFileName(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + "_" + originalName;
            String key = prefix + sensorId + "/" + fileName;
            String contentType = normalizeContentType(file.getContentType());

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .metadata(java.util.Map.of("original-name", originalName))
                    .build();

            try (InputStream inputStream = file.getInputStream()) {
                client.putObject(request, RequestBody.fromInputStream(inputStream, file.getSize()));
            } catch (IOException ex) {
                throw new IllegalStateException("Failed to store file on S3", ex);
            }

            results.add(new StoredFileInfo(
                    StorageType.S3,
                    key,
                    "s3://" + bucket + "/" + key,
                    originalName,
                    contentType,
                    file.getSize()
            ));
        }

        return results;
    }

    private S3Client getS3Client(String region) {
        if (s3Client != null) {
            return s3Client;
        }

        synchronized (this) {
            if (s3Client == null) {
                S3ClientBuilder builder = S3Client.builder()
                        .region(Region.of(region))
                        .credentialsProvider(resolveCredentials());
                s3Client = builder.build();
            }
        }

        return s3Client;
    }

    private S3Presigner getS3Presigner(String region) {
        if (s3Presigner != null) {
            return s3Presigner;
        }

        synchronized (this) {
            if (s3Presigner == null) {
                s3Presigner = S3Presigner.builder()
                        .region(Region.of(region))
                        .credentialsProvider(resolveCredentials())
                        .build();
            }
        }

        return s3Presigner;
    }

    private AwsCredentialsProvider resolveCredentials() {
        StorageProperties.S3 s3 = properties.getS3();
        if (hasText(s3.getAccessKey()) && hasText(s3.getSecretKey())) {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(s3.getAccessKey(), s3.getSecretKey());
            return StaticCredentialsProvider.create(credentials);
        }
        return DefaultCredentialsProvider.create();
    }

    private String normalizeFileName(String originalName) {
        String baseName = Objects.toString(originalName, "file");
        String fileName = Paths.get(baseName).getFileName().toString();
        return fileName.replaceAll("[^A-Za-z0-9._-]", "_");
    }

    private String normalizeContentType(String contentType) {
        return hasText(contentType) ? contentType : DEFAULT_CONTENT_TYPE;
    }

    private String normalizePrefix(String prefix) {
        if (!hasText(prefix)) {
            return "sensors/";
        }
        String trimmed = prefix.trim();
        return trimmed.endsWith("/") ? trimmed : trimmed + "/";
    }

    private String buildContentDisposition(String fileName) {
        if (!hasText(fileName)) {
            return ContentDisposition.attachment().build().toString();
        }
        return ContentDisposition.attachment().filename(fileName).build().toString();
    }

    private String requireValue(String value, String message) {
        if (!hasText(value)) {
            throw new IllegalStateException(message);
        }
        return value;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
