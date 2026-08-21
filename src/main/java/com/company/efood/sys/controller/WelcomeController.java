package com.company.efood.sys.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @version 1.0.0
 * @Author Md. Sabbir Hossain
 * @Email sabbirr883@gmail.com
 * @Since March 1, 2024
 */

@AllArgsConstructor
@RestController
@RequestMapping("/")
public class WelcomeController {

    @GetMapping
    public String getAll() {
        return " API UP & Running.................\n" +
                "@version 0.0.1";
    }

    @GetMapping("/devinfo")
    public String getDevInfo() {
        return "Md Sabbir Hossain \n" +
                "SOFTWARE ENGINEER\n" +
                "\n" +
                "\n" +
                "Address:\n" +
                "   Post: Lokmanpur,\n" +
                "   Upazila: Bagatipara,\n" +
                "   District: Natore,\n" +
                "Contact:\n" +
                " Email: devolopersabbir0@gmail.com";
    }
}
