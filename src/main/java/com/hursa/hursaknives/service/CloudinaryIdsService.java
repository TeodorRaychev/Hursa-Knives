package com.hursa.hursaknives.service;

import com.hursa.hursaknives.model.entity.CloudinaryIds;
import com.hursa.hursaknives.repo.CloudinaryIdsRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CloudinaryIdsService {

  private final CloudinaryIdsRepository cloudinaryIdsRepository;

  public CloudinaryIdsService(CloudinaryIdsRepository cloudinaryIdsRepository) {
    this.cloudinaryIdsRepository = cloudinaryIdsRepository;
  }

  public void save(String publicId) {
    if (cloudinaryIdsRepository.findByPublicId(publicId).isEmpty()) {
      cloudinaryIdsRepository.save(new CloudinaryIds().setPublicId(publicId));
    }
  }

  public List<CloudinaryIds> getAll() {
    return cloudinaryIdsRepository.findAll();
  }

  public void deleteAll(List<CloudinaryIds> cloudinaryIds) {
    cloudinaryIdsRepository.deleteAll(cloudinaryIds);
  }
}
