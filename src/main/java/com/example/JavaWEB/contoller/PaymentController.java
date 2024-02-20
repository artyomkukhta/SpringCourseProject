package com.example.JavaWEB.contoller;

import com.example.JavaWEB.model.PaymentCell;
import com.example.JavaWEB.serviceImpl.PaymentCellServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Slf4j
@RequestMapping(value = "/cells")
@Controller
public class PaymentController {
    private final PaymentCellServiceImpl paymentCellServiceImpl;

    @Autowired
    public PaymentController(PaymentCellServiceImpl paymentCellServiceImpl) {

        this.paymentCellServiceImpl = paymentCellServiceImpl;
    }


    @GetMapping("/addCell")
    public String addCell(Model model) {
        model.addAttribute("paymentCell", new PaymentCell());
        return "addCell";
    }

    @PostMapping("/addCell")
    public String addCellButton(@Valid PaymentCell paymentCell, Errors errors) {
        if (errors.hasErrors()) {
            log.info("error adding cell");
            return "addCell";
        } else log.info("cell added");


        paymentCellServiceImpl.add(paymentCell);
        return "redirect:/cells";
    }


    @GetMapping("/id{id}")
    public String cellDetails(@PathVariable(value = "id") Long id, Model model) {

        if (!paymentCellServiceImpl.existsById(id)) {
            return "redirect:/cells";
        }
        model.addAttribute("paymentCell", paymentCellServiceImpl.findById(id).get());//передаем в шаблон
        return "paymentInfo";
    }

    @PreAuthorize("hasAuthority('developers:write')")
    @GetMapping("/id{id}/edit")
    public String cellEdit(@PathVariable(value = "id") Long id, Model model) {
        if (paymentCellServiceImpl.existsById(id))
            model.addAttribute("paymentCell", paymentCellServiceImpl.findById(id).get());//передаем в шаблон
        return "paymentInfoEdit";
    }

    @PostMapping("/id{id}/edit")
    @PreAuthorize("hasAuthority('developers:write')")
    public String cellEditButton(@PathVariable(value = "id") Long id,
                                 @Valid PaymentCell paymentCell,
                                 Errors errors
    ) {
        if (errors.hasErrors()) {
            log.info("error editing cell");
            return "paymentInfoEdit";
        } else log.info("cell added");
        paymentCellServiceImpl.editById(id, paymentCell.getName(), paymentCell.getType(), paymentCell.getDesc());
        return "redirect:/cells/id{id}";//переадресация
    }

    @PostMapping("/id{id}/remove")
    @PreAuthorize("hasAuthority('developers:write')")
    public String cellRemove(@PathVariable(value = "id") Long id, Model model) {
        paymentCellServiceImpl.deleteById(id);
        return "redirect:/cells";
    }

}