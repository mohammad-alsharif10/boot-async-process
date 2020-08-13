package com.async.future.service;

import com.async.future.database.ImageRepository;
import com.async.future.model.Image;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public Subject<List<String>> listSubject = PublishSubject.create();


    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public List<String> processImagesListSync(MultipartFile[] imagesList) throws IOException {
        List<String> imagesPathListSync = new ArrayList<>();


        long startSync = System.currentTimeMillis();
        for (MultipartFile file : imagesList) {
            try {
                uploadImages(file, imagesPathListSync);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        long endSync = (System.currentTimeMillis() - startSync);
        System.out.println("time consumed sync " + endSync);
        System.out.println(imagesPathListSync.size());
        return imagesPathListSync;
    }

    @SneakyThrows
    public List<String> processImagesListAsync(MultipartFile[] imagesList) throws Exception {

        MultipartFile[] partOne = Arrays.copyOfRange(imagesList, 0, 21);
        MultipartFile[] partTwo = Arrays.copyOfRange(imagesList, 21, 42);
        MultipartFile[] partThree = Arrays.copyOfRange(imagesList, 42, 63);
        MultipartFile[] partFour = Arrays.copyOfRange(imagesList, 63, 84);

        long startAsync = System.currentTimeMillis();

        CompletableFuture<List<String>> completableFuturePartOne = CompletableFuture.supplyAsync(() -> {
            ArrayList<String> paths1 = new ArrayList<>();
            for (MultipartFile multipartFile : partOne) {
                try {
                    uploadImages(multipartFile, paths1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return paths1;
        });

        CompletableFuture<List<String>> completableFuturePartTwo = CompletableFuture.supplyAsync(() -> {
            ArrayList<String> paths2 = new ArrayList<>();
            for (MultipartFile multipartFile : partTwo) {
                try {
                    uploadImages(multipartFile, paths2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return paths2;
        });

        CompletableFuture<List<String>> completableFuturePartThree = CompletableFuture.supplyAsync(() -> {
            ArrayList<String> paths3 = new ArrayList<>();
            for (MultipartFile multipartFile : partThree) {
                try {
                    uploadImages(multipartFile, paths3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return paths3;
        });

        CompletableFuture<List<String>> completableFuturePartFour = CompletableFuture.supplyAsync(() -> {
            ArrayList<String> paths4 = new ArrayList<>();
            for (MultipartFile multipartFile : partFour) {
                try {
                    uploadImages(multipartFile, paths4);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return paths4;
        });

        List<CompletableFuture<List<String>>> completableFutureList = new ArrayList<>();
        completableFutureList.add(completableFuturePartOne);
        completableFutureList.add(completableFuturePartTwo);
        completableFutureList.add(completableFuturePartThree);
        completableFutureList.add(completableFuturePartFour);
        List<String> collect = completableFutureList.parallelStream().map(CompletableFuture::join).flatMap(Collection::stream).collect(Collectors.toList());
        long endAsync = (System.currentTimeMillis() - startAsync);
        System.out.println("time consumed async " + endAsync);
        System.out.println("Images processed " + collect.size());
        return collect;
    }


    public List<Image> processImagesListAsync2(MultipartFile[] imagesList) {
        long startAsync = System.currentTimeMillis();
        List<MultipartFile[]> multipartFilesList = new ArrayList<>();
        multipartFilesList.add(Arrays.copyOfRange(imagesList, 0, 21));
        multipartFilesList.add(Arrays.copyOfRange(imagesList, 21, 42));
        multipartFilesList.add(Arrays.copyOfRange(imagesList, 42, 63));
        multipartFilesList.add(Arrays.copyOfRange(imagesList, 63, 84));

        List<Image> imageList = imageRepository.saveAll(multipartFilesList
                .parallelStream()
                .map(multipartFiles -> CompletableFuture.supplyAsync(() -> {
                    ArrayList<String> paths = new ArrayList<>();
                    Arrays.stream(multipartFiles).forEach(multipartFile -> {
                        try {
                            uploadImages(multipartFile, paths);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    return paths;
                }))
                .map(CompletableFuture::join)
                .flatMap(Collection::stream)
                .map(Image::new).collect(Collectors.toList()));
        long endAsync = (System.currentTimeMillis() - startAsync);
        System.out.println("time consumed async " + endAsync);
        System.out.println("Images processed " + imageList.size());
        return imageList;
    }

    private void uploadImages(MultipartFile file, List<String> paths) throws IOException {
        String imagesPath = "F:\\personal-projects\\images\\" + file.getOriginalFilename();
        paths.add(imagesPath);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File convertFile = new File(imagesPath);
        FileOutputStream fileOutputStream = new FileOutputStream(convertFile);
        fileOutputStream.write(file.getBytes());
        fileOutputStream.close();
    }


}
