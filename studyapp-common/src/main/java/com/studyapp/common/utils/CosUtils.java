package com.studyapp.common.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 腾讯云COS工具类
 */
@Slf4j
public class CosUtils {

    private String secretId;
    private String secretKey;
    private String region;
    private String bucketName;
    private String baseUrl;
    private COSClient cosClient;

    public CosUtils(String secretId, String secretKey, String region, String bucketName) {
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.region = region;
        this.bucketName = bucketName;
        this.baseUrl = "https://" + bucketName + ".cos." + region + ".myqcloud.com";
        initClient();
    }

    /**
     * 初始化COS客户端
     */
    private void initClient() {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        clientConfig.setHttpProtocol(HttpProtocol.https);
        this.cosClient = new COSClient(cred, clientConfig);
    }

    /**
     * 上传文件
     *
     * @param file   文件
     * @param folder 文件夹（如：images、videos）
     * @return 文件URL
     */
    public String uploadFile(MultipartFile file, String folder) {
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String key = folder + "/" + UUID.randomUUID().toString().replace("-", "") + suffix;

        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, metadata);
            PutObjectResult result = cosClient.putObject(putObjectRequest);
            log.info("文件上传成功: {}", key);
            return baseUrl + "/" + key;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传图片
     */
    public String uploadImage(MultipartFile file) {
        return uploadFile(file, "images");
    }

    /**
     * 上传视频
     */
    public String uploadVideo(MultipartFile file) {
        return uploadFile(file, "videos");
    }

    /**
     * 上传头像
     */
    public String uploadAvatar(MultipartFile file) {
        return uploadFile(file, "avatars");
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(baseUrl)) {
            return;
        }
        String key = fileUrl.substring(baseUrl.length() + 1);
        try {
            cosClient.deleteObject(bucketName, key);
            log.info("文件删除成功: {}", key);
        } catch (Exception e) {
            log.error("文件删除失败: {}", key, e);
        }
    }

    /**
     * 生成视频临时访问URL（带签名）
     *
     * @param videoKey 视频key
     * @param expireMinutes 过期时间（分钟）
     * @return 临时访问URL
     */
    public String generatePresignedUrl(String videoKey, int expireMinutes) {
        try {
            java.util.Date expiration = new java.util.Date(System.currentTimeMillis() + expireMinutes * 60 * 1000L);
            java.net.URL url = cosClient.generatePresignedUrl(bucketName, videoKey, expiration);
            return url.toString();
        } catch (Exception e) {
            log.error("生成预签名URL失败", e);
            return null;
        }
    }

    /**
     * 关闭客户端
     */
    public void shutdown() {
        if (cosClient != null) {
            cosClient.shutdown();
        }
    }
}
