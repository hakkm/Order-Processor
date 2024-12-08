package com.khabir.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileUtils {

  public static void moveFileToErrorDirectory(File file) {
    Path sourcePath = file.toPath();
    Path targetPath = new File(file.getParentFile().getParentFile(), "err/" + file.getName()).toPath();
    try {
      Files.createDirectories(targetPath.getParent());
      Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
      System.out.println("Moved file to error directory: " + targetPath);
    } catch (IOException e) {
      System.err.println("Failed to move file to error directory: " + file.getName());
      e.printStackTrace();
    }
  }

  public static void moveFileToOutputDirectory(File file) {
    Path sourcePath = file.toPath();
    Path targetPath = new File(file.getParentFile().getParentFile(), "output/" + file.getName()).toPath();
    try {
      Files.createDirectories(targetPath.getParent());
      Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
      System.out.println("Moved file to output directory: " + targetPath);
    } catch (IOException e) {
      System.err.println("Failed to move file to output directory: " + file.getName());
      e.printStackTrace();
    }
  }
}