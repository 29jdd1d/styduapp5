package com.studyapp.admin.controller;

import com.studyapp.common.result.Result;
import com.studyapp.common.utils.CosUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 */
@Slf4j
@Tag(name = "文件上传")
@RestController
@RequestMapping("/admin/upload")
@RequiredArgsConstructor
public class AdminUploadController {

    private final CosUtils cosUtils;

    /**
     * 上传图片
     */
    @Operation(summary = "上传图片")
    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }
        
        // 验证文件大小（最大5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.error("图片大小不能超过5MB");
        }
        
        try {
            String url = cosUtils.uploadImage(file);
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            return Result.success(result);
        } catch (Exception e) {
            log.error("图片上传失败", e);
            return Result.error("图片上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传视频
     */
    @Operation(summary = "上传视频")
    @PostMapping("/video")
    public Result<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            return Result.error("只能上传视频文件");
        }
        
        // 验证文件大小（最大500MB）
        if (file.getSize() > 500 * 1024 * 1024) {
            return Result.error("视频大小不能超过500MB");
        }
        
        try {
            String url = cosUtils.uploadVideo(file);
            // 从URL中提取videoKey（用于生成签名URL）
            String videoKey = extractVideoKey(url);
            
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("videoKey", videoKey);
            return Result.success(result);
        } catch (Exception e) {
            log.error("视频上传失败", e);
            return Result.error("视频上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传头像
     */
    @Operation(summary = "上传头像")
    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }
        
        // 验证文件大小（最大2MB）
        if (file.getSize() > 2 * 1024 * 1024) {
            return Result.error("头像大小不能超过2MB");
        }
        
        try {
            String url = cosUtils.uploadAvatar(file);
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            return Result.success(result);
        } catch (Exception e) {
            log.error("头像上传失败", e);
            return Result.error("头像上传失败: " + e.getMessage());
        }
    }

    /**
     * 通用文件上传
     */
    @Operation(summary = "通用文件上传")
    @PostMapping("/file")
    public Result<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "files") String folder
    ) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }
        
        // 验证文件大小（最大100MB）
        if (file.getSize() > 100 * 1024 * 1024) {
            return Result.error("文件大小不能超过100MB");
        }
        
        try {
            String url = cosUtils.uploadFile(file, folder);
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            return Result.success(result);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 从URL中提取视频Key
     */
    private String extractVideoKey(String url) {
        // URL格式: https://bucket.cos.region.myqcloud.com/videos/xxx.mp4
        // 提取: videos/xxx.mp4
        if (url != null && url.contains(".myqcloud.com/")) {
            return url.substring(url.indexOf(".myqcloud.com/") + 14);
        }
        return null;
    }
}
