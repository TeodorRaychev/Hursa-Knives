package com.hursa.hursaknives.service.scheduler;

import com.cloudinary.api.ApiResponse;
import com.hursa.hursaknives.model.entity.CloudinaryIds;
import com.hursa.hursaknives.service.CloudinaryIdsService;
import com.hursa.hursaknives.service.FileUploadService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CloudinaryDeleteImagesScheduler {
  private final CloudinaryIdsService cloudinaryIdsService;
  private final FileUploadService fileUploadService;
  private final Logger logger = Logger.getLogger(CloudinaryDeleteImagesScheduler.class.getName());

  public CloudinaryDeleteImagesScheduler(
      CloudinaryIdsService cloudinaryIdsService, FileUploadService fileUploadService) {
    this.cloudinaryIdsService = cloudinaryIdsService;
    this.fileUploadService = fileUploadService;
  }

  @Scheduled(cron = "0 0 0 * * ?")
  public void deleteImages() {
    List<CloudinaryIds> all = cloudinaryIdsService.getAll();
    if (!all.isEmpty()) {
      try {
        ApiResponse apiResponse =
            fileUploadService.deleteAllFiles(all.stream().map(CloudinaryIds::getPublicId).toList());
        logger.info(
            "Deleted "
                + apiResponse.get("deleted")
                + " images at "
                + LocalDateTime.now(ZoneOffset.UTC));

        logger.info(
            String.format(
                "Deleting %d images at %s", all.size(), LocalDateTime.now(ZoneOffset.UTC)));
        cloudinaryIdsService.deleteAll(all);
      } catch (Exception e) {
        logger.info("Error deleting images at " + LocalDateTime.now(ZoneOffset.UTC));
      }
    } else {
      logger.info("No images to delete at " + LocalDateTime.now(ZoneOffset.UTC));
    }
  }
}
