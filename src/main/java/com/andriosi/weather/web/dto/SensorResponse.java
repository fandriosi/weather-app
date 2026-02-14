package com.andriosi.weather.web.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.andriosi.weather.domain.SensorType;

public class SensorResponse {

    private UUID id;
    private String code;
    private String description;
    private SensorType type;
    private SensorStatus status;
    private UUID stationId;
    private String stationName;

    // IMPORTANTE: Dados da imagem
    private ImageData image;

    // IMPORTANTE: Lista de arquivos anexados
    private List<FileData> files;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Classe interna para dados da imagem
    public static class ImageData {

        private UUID id;
        private String url;              // URL para download/visualização
        private String originalName;     // Nome original do arquivo
        private String contentType;      // image/jpeg, image/png, etc
        private Long size;               // Tamanho em bytes
        private LocalDateTime uploadedAt;

        public ImageData(UUID id2, String fileUrl, String originalName2, String contentType2, long size2,
                LocalDateTime localDateTime) {
            //TODO Auto-generated constructor stub
        }

        // Getters e Setters
        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getOriginalName() {
            return originalName;
        }

        public void setOriginalName(String originalName) {
            this.originalName = originalName;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        public LocalDateTime getUploadedAt() {
            return uploadedAt;
        }

        public void setUploadedAt(LocalDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
        }
    }

    // Classe interna para dados dos arquivos
    public static class FileData {

        private UUID id;
        private String url;              // URL para download
        private String originalName;     // Nome original do arquivo
        private String contentType;      // application/pdf, etc
        private Long size;               // Tamanho em bytes
        private LocalDateTime uploadedAt;

        public FileData(UUID id2, String fileUrl, String originalName2, String contentType2, long size2,
                LocalDateTime localDateTime) {
            //TODO Auto-generated constructor stub
        }

        // Getters e Setters
        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getOriginalName() {
            return originalName;
        }

        public void setOriginalName(String originalName) {
            this.originalName = originalName;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        public LocalDateTime getUploadedAt() {
            return uploadedAt;
        }

        public void setUploadedAt(LocalDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
        }
    }

    // Getters e Setters principais
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SensorType getType() {
        return type;
    }

    public void setType(SensorType type) {
        this.type = type;
    }

    public SensorStatus getStatus() {
        return status;
    }

    public void setStatus(SensorStatus status) {
        this.status = status;
    }

    public UUID getStationId() {
        return stationId;
    }

    public void setStationId(UUID stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public ImageData getImage() {
        return image;
    }

    public void setImage(ImageData image) {
        this.image = image;
    }

    public List<FileData> getFiles() {
        return files;
    }

    public void setFiles(List<FileData> files) {
        this.files = files;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
