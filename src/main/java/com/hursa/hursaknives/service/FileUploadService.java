package com.hursa.hursaknives.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileUploadService {
  private final Cloudinary cloudinary;

  public Map<String, String> uploadFile(MultipartFile multipartFile) throws IOException {
    String uuid = UUID.randomUUID().toString();
    String url =
        cloudinary
            .uploader()
            .upload(multipartFile.getBytes(), Map.of("public_id", uuid))
            .get("url")
            .toString();
    return Map.of("public_id", uuid, "url", url);
  }

  public ApiResponse deleteAllFiles(List<String> public_ids) throws Exception {
    return cloudinary.api().deleteResources(public_ids, Map.of("resource_type", "image"));
  }
}
