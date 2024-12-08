package com.khabir.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileUtils {

  private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

  public static void moveFileToErrorDirectory(File file) {
    Path sourcePath = file.toPath();
    Path targetPath = new File(file.getParentFile().getParentFile(), "err/" + file.getName()).toPath();
    try {
      Files.createDirectories(targetPath.getParent());
      Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
      logger.info("Moved file to error directory: {}", targetPath);
    } catch (IOException e) {
      logger.error("Failed to move file to error directory: {}", file.getName(), e);
    }
  }

  public static void moveFileToOutputDirectory(File file) {
    Path sourcePath = file.toPath();
    Path targetPath = new File(file.getParentFile().getParentFile(), "output/" + file.getName()).toPath();
    try {
      Files.createDirectories(targetPath.getParent());
      Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
      logger.info("Moved file to output directory: {}", targetPath);
    } catch (IOException e) {
      logger.error("Failed to move file to output directory: {}", file.getName(), e);
    }
  }
}