package com.example.JavaWEB.contoller;

import com.example.JavaWEB.serviceImpl.PaymentCellServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Slf4j
@Controller
public class HomeController {
    private final PaymentCellServiceImpl paymentCellServiceImpl;

    @Autowired
    public HomeController(PaymentCellServiceImpl paymentCellServiceImpl) {

        this.paymentCellServiceImpl = paymentCellServiceImpl;
    }
    @GetMapping("/cells")
    public String home(Model model) {
        model.addAttribute("paymentCells", paymentCellServiceImpl.findAll());
        return "home";
    }


    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About page");
        return "about";
    }
}