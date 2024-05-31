package com.example.JavaWEB.contoller;

import com.example.JavaWEB.model.Card;
import com.example.JavaWEB.repository.CardRepository;
import com.example.JavaWEB.service.CardService;
import com.example.JavaWEB.service.TransactionService;
import com.example.JavaWEB.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/userId{userId}/cards")
public class TransferController {
    @Autowired
    private CardService cardService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/transfer")
    public String showTransferForm(@RequestParam("fromCardId") Long fromCardId, Model model, @ModelAttribute("error") String error) {
        Card fromCard = cardService.findById(fromCardId).get();
        model.addAttribute("fromCardId", fromCardId);
        model.addAttribute("fromCardName", fromCard.getName());
        model.addAttribute("commissionRate", fromCard.getCardType().getCommission());
        model.addAttribute("cashbackRate", fromCard.getCardType().getCashback());

        List<Card> sortedCards = new ArrayList<>();
        userService.findAllUserCards().forEach(sortedCards::add);
        sortedCards = sortedCards.stream()
                .filter(card -> !card.getId().equals(fromCardId))
                .sorted(Comparator.comparing(Card::getName).reversed())
                .collect(Collectors.toList());

        model.addAttribute("cards", sortedCards);
        model.addAttribute("amount", 0.0);
        model.addAttribute("avaliableAmount", fromCard.getBalance());
        model.addAttribute("error", error);

        return "transferForm";
    }

    @PostMapping("/transfer")
    public String transfer(@RequestParam("fromCardId") Long fromCardId,
                           @RequestParam("transferType") String transferType,
                           @RequestParam(value = "toCardId", required = false) Long toCardId,
                           @RequestParam(value = "toCardNumber", required = false) String toCardNumber,
                           @RequestParam("amount") Double amount,
                           @PathVariable("userId") Long userId,
                           RedirectAttributes redirectAttributes) {
        Card fromCard = cardService.findById(fromCardId).orElse(null);
        if (fromCard == null) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: Карта не найдена");
            return "redirect:/userId" + userId + "/cards";
        }

        // Calculate the actual amount to be deducted considering the commission
        double commissionRate = fromCard.getCardType().getCommission() * 0.01;
        double cashbackRate = fromCard.getCardType().getCashback() * 0.01;
        double actualAmountToDeduct = amount + (amount * commissionRate) - (amount * cashbackRate);

        // Проверяем, достаточно ли средств на карте для перевода
        if (fromCard.getBalance() < actualAmountToDeduct) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: Недостаточно средств на карте");
            return "redirect:/userId" + userId + "/cards/transfer?fromCardId=" + fromCardId;
        }

        // Осуществляем перевод
        if ("select".equals(transferType) && toCardId != null) {
            transactionService.transferById(fromCardId, toCardId, amount);
        } else if ("number".equals(transferType) && toCardNumber != null) {
            transactionService.transferByNumber(fromCardId, toCardNumber, amount);
        }

        return "redirect:/userId" + userId + "/cards";
    }
}


