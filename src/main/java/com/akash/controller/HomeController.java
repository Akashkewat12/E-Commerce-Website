package com.akash.controller;


import com.akash.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping
    public ApiResponse HomeControllerHandler() {
        ApiResponse apiResponse=new ApiResponse();
        apiResponse.setMessage("Welcome to ecommerce");
        return apiResponse;
    }

}
