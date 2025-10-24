package com.example.food_delivery.controller;


import com.example.food_delivery.payload.ResponseData;
import com.example.food_delivery.service.imp.FileServiceImp;
import com.example.food_delivery.service.imp.MenuServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    MenuServiceImp menuServiceImp;
    @Autowired
    FileServiceImp fileServiceImp;

    @PostMapping()
    public ResponseEntity<?> createMenu(
            @RequestParam MultipartFile file,
            @RequestParam String  title,
            @RequestParam String time_ship ,
            @RequestParam String is_freeship,
            @RequestParam double price ,
            @RequestParam int cate_id){

        ResponseData responseData = new ResponseData();
        boolean isSucces= menuServiceImp.createMenu(file,title,time_ship,is_freeship,price,cate_id);
        responseData.setData(isSucces);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/file/{filename:.+}")
    public ResponseEntity<?> getFileRestaurant(@PathVariable String filename){
        Resource resource= fileServiceImp.loadFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+resource.getFilename()+"\"").body(resource);
    }
}
