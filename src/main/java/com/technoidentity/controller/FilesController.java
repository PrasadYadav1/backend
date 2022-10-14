package com.technoidentity.controller;

import com.technoidentity.dto.FileInfoDto;
import com.technoidentity.service.FilesStorageService;
import com.technoidentity.util.ResponseMessage;
import io.swagger.annotations.Api;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

@RestController
@RequestMapping(value = "/api/files")
@CrossOrigin
@Api(tags = "File")
public class FilesController {

  @Autowired FilesStorageService storageService;

  @PostMapping("/upload")
  public ResponseEntity<?> uploadFile(
      @RequestParam("bucketName") String bucketName, @RequestPart("file") MultipartFile file) {
    String message = "";
    try {
      String datePrefix = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
      String filename = datePrefix + "-" + file.getOriginalFilename();

      storageService.save(bucketName, file, filename);

      String url =
          MvcUriComponentsBuilder.fromMethodName(
                  FilesController.class, "getFile", bucketName, file.getOriginalFilename())
              .build()
              .toString();
      return ResponseEntity.status(HttpStatus.OK).body(new FileInfoDto(filename, url));
    } catch (Exception e) {
      message = "Could not upload the file: " + e + "!";
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
          .body(new ResponseMessage(message));
    }
  }

  @GetMapping("/{filename:.+}")
  @ResponseBody
  public ResponseEntity<Resource> getFile(
      @RequestParam("bucketName") String bucketName, @PathVariable String filename) {
    Resource file = storageService.load(bucketName, filename);
    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
        .body(file);
  }
}
