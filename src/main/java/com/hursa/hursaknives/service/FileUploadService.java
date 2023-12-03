package com.hursa.hursaknives.service;

import com.cloudinary.Cloudinary;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileUploadService {
  private final Cloudinary cloudinary;

  public String uploadFile(MultipartFile multipartFile) throws IOException {
    return cloudinary
        .uploader()
        .upload(multipartFile.getBytes(), Map.of("public_id", UUID.randomUUID().toString()))
        .get("url")
        .toString();
  }
}
