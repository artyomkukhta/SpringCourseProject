package com.example.JavaWEB.contoller;

import com.example.JavaWEB.model.CardType;
import com.example.JavaWEB.serviceImpl.CardTypeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@PreAuthorize("hasAuthority('developers:write')")
public class CardTypeController {

    private final CardTypeServiceImpl cardTypeService;

    @Autowired
    public CardTypeController(CardTypeServiceImpl cardTypeService) {
        this.cardTypeService = cardTypeService;
    }


    @GetMapping("/userId{userId}/cards/cardTypes/addCardType")
    public String showAddCardTypeForm(Model model) {
        model.addAttribute("cardType", new CardType());
        return "add-card-type";
    }

    @PostMapping("/userId{userId}/cards/cardTypes/addCardType")
    public String addCardType(@Valid @ModelAttribute CardType cardType, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-card-type";
        }
        cardTypeService.save(cardType);
        return "redirect:/userId{userId}/cards/";
    }

    @GetMapping("/userId{userId}/cards/cardTypes")
    public String listCardTypes(Model model) {
        model.addAttribute("cardTypes", cardTypeService.findAll());
        return "list-card-types";
    }

    @GetMapping("/userId{userId}/cards/cardTypes/editCardType/{id}")
    public String showEditCardTypeForm(@PathVariable("id") Long id, Model model) {
        Optional<CardType> cardTypeOptional = cardTypeService.findById(id);
        if (cardTypeOptional.isPresent()) {
            model.addAttribute("cardType", cardTypeOptional.get());
            return "edit-card-type";
        } else {
            return "redirect:/userId{userId}/cards/";
        }
    }

    @PostMapping("/userId{userId}/cards/cardTypes/editCardType/{id}")
    public String editCardType(@PathVariable("id") Long id, @Valid @ModelAttribute CardType cardType, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "edit-card-type";
        }
        cardType.setId(id);
        cardTypeService.save(cardType);
        return "redirect:/userId{userId}/cards/";
    }

    @GetMapping("/userId{userId}/cards/cardTypes/deleteCardType/{id}")
    public String deleteCardType(@PathVariable("id") Long id) {
        cardTypeService.deleteById(id);
        return "redirect:/userId{userId}/cards/";
    }
}


