package com.async.future.controller;

import com.async.future.model.Image;
import com.async.future.service.ImageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("image")
public class ImageController {

    private final ImageService imageService;
//    private List<String> stringList;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
//        Disposable subscribe = this.imageService.listSubject.map(strings -> strings).subscribe(strings -> this.stringList = strings);


    }


    @RequestMapping(value = "/upload-images", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<Image> uploadProfileImage
            (@RequestParam(value = "images") MultipartFile[] images) {
//        return imageService.processImagesListSync(images);
//        return imageService.processImagesListAsync(images);
        return imageService.processImagesListAsync2(images);
    }
}
