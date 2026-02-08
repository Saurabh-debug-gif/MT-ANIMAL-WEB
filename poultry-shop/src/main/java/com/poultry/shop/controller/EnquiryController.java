package com.poultry.shop.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class EnquiryController {

    @PostMapping("/enquiry")
    public String submitEnquiry(
            @RequestParam String animalType,
            @RequestParam String requirement,
            @RequestParam Integer animalCount,
            @RequestParam String monthlyRequirement,
            @RequestParam String name,
            @RequestParam String phone
    ) {
        System.out.println("NEW ENQUIRY:");
        System.out.println("Animal: " + animalType);
        System.out.println("Animals Count: " + animalCount);
        System.out.println("Monthly Requirement: " + monthlyRequirement);
        System.out.println("Requirement: " + requirement);
        System.out.println("Name: " + name);
        System.out.println("Phone: " + phone);

        return "OK";
    }

}
