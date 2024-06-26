package com.example.JavaWEB.contoller;

import com.example.JavaWEB.model.Card;
import com.example.JavaWEB.model.CardType;
import com.example.JavaWEB.serviceImpl.CardServiceImpl;
import com.example.JavaWEB.serviceImpl.CardTypeServiceImpl;
import com.example.JavaWEB.serviceImpl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@Slf4j
public class CardController {
    private final CardServiceImpl cardServiceImpl;
    private final CardTypeServiceImpl cardTypeServiceImpl;
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public CardController(CardServiceImpl cardServiceImpl, UserServiceImpl userServiceImpl, CardTypeServiceImpl cardTypeServiceImpl) {
        this.cardServiceImpl = cardServiceImpl;
        this.userServiceImpl = userServiceImpl;
        this.cardTypeServiceImpl = cardTypeServiceImpl;
    }

    @GetMapping("/userId{userId}/cards")
    public String cardsList(@PathVariable(value = "userId") Long userId, Model model) {
        userServiceImpl.findById(userId);
        return findPaginated(userId, 1, "name", "asc", model);
    }

    @GetMapping("/userId{userId}/cards/add")
    public String cardsAdd(@PathVariable(value = "userId") Long userId, Model model) {
        model.addAttribute("card", new Card());

        List<CardType> cardTypes = StreamSupport.stream(cardTypeServiceImpl.findAll().spliterator(), false)
                .collect(Collectors.toList());

        model.addAttribute("cardTypes", cardTypes);
        return "cardsAdd";
    }

    @PostMapping("/userId{userId}/cards/add")
    public String cardsAddPost(@PathVariable(value = "userId") Long userId, @Valid Card card, Errors errors, Model model) {
        if (errors.hasErrors()) {
            log.error(errors.getAllErrors().toString());
            List<CardType> cardTypes = StreamSupport.stream(cardTypeServiceImpl.findAll().spliterator(), false)
                    .collect(Collectors.toList());

            model.addAttribute("cardTypes", cardTypes);
            return "cardsAdd";
        } else log.info("card validated");

        Long cardTypeId = card.getCardType().getId();
        Optional<CardType> cardTypeFromDB = cardTypeServiceImpl.findById(cardTypeId);
        if (!cardTypeFromDB.isPresent()) {
            return "cardsAdd";
        }

        card.setCardType(cardTypeFromDB.get());
        log.info("NORM");
        cardServiceImpl.add(userId, card);
        return "redirect:/userId{userId}/cards";
    }



    @GetMapping("/userId{userId}/cards/id{id}/deposit")
    public String cardsDeposit(@PathVariable(value = "userId") Long userId,
                               @PathVariable(value = "id") Long id,

                               Model model) {
        model.addAttribute("card", cardServiceImpl.findById(id).get());
        return "cardDeposit";
    }

    @PostMapping("/userId{userId}/cards/id{id}/deposit")
    public String cardsDeposit(Card card, @RequestParam double amount) {
        cardServiceImpl.addBalanceById(card.getId(), amount);
        return "redirect:/userId{userId}/cards/id{id}";
    }


    @GetMapping("/userId{userId}/cards/id{id}")
    public String cardDetails(@PathVariable(value = "userId") Long userId, @PathVariable(value = "id") Long id, Model model) {
        if (!cardServiceImpl.existsById(id)) {
            return "redirect:/userId{userId}/cards";
        }
        model.addAttribute("userId", userId);
        model.addAttribute("card", cardServiceImpl.findById(id).get());
        return "cardInfo";
    }

    @PostMapping("/userId{userId}/cards/id{id}")
    public String banCard(@PathVariable(value = "userId") Long userId, @PathVariable(value = "id") Long id, Card card) {
        Optional<CardType> cardTypeFromDB = cardTypeServiceImpl.findById(card.getCardType().getId());
        if (!cardTypeFromDB.isPresent()) {
            // Handle the error appropriately
            return "redirect:/userId" + userId + "/cards/id" + id;
        }

        card.setCardType(cardTypeFromDB.get());
        cardServiceImpl.editById(id, card.getName(), card.getNumber(), card.getBalance(), card.getIsBlocked());
        return "cardInfo";
    }


    @GetMapping("/userId{userId}/cards/id{id}/edit")
    public String cardEdit(@PathVariable(value = "userId") Long userId, @PathVariable(value = "id") Long id, Model model) {

        model.addAttribute("card", cardServiceImpl.findById(id).get());
        return "cardInfoEdit";
    }

    //получаем значения из формы и записываем в бд
    @PostMapping("/userId{userId}/cards/id{id}/edit")
    public String cardEditPost(@PathVariable(value = "userId") Long userId,
                               @PathVariable(value = "id") Long id,
                               @Valid Card card,
                               Errors errors) {
        if (errors.hasErrors()) {
            return "cardInfoEdit";
        }
        cardServiceImpl.editById(id, card.getName(), card.getNumber(), card.getBalance(), card.getIsBlocked());

        return "redirect:/userId{userId}/cards/id{id}";
    }

    @PostMapping("/userId{userId}/cards/id{id}/remove")

    public String cardRemove(@PathVariable(value = "userId") Long userId,
                             @PathVariable(value = "id") Long id,
                             Model model) {
        cardServiceImpl.deleteById(id);
        log.info("deleted(mb)");
        return "redirect:/userId{userId}/cards";//переадресация
    }

    @GetMapping("/userId{userId}/cards/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "userId") Long userId,
                                @PathVariable(value = "pageNo") int pageNo,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDir") String sortDir,
                                Model model) {
        int pageSize = 6; // Set the page size to 6
        Page<Card> page = cardServiceImpl.findPaginated(userId, pageNo, pageSize, sortField, sortDir);
        List<Card> cards = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("cards", cards);
        return "cards";
    }

}
