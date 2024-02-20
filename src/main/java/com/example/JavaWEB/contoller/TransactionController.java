package com.example.JavaWEB.contoller;

import com.example.JavaWEB.model.Card;
import com.example.JavaWEB.model.Transaction;
import com.example.JavaWEB.optimization.Optimization;
import com.example.JavaWEB.serviceImpl.CardServiceImpl;
import com.example.JavaWEB.serviceImpl.PaymentCellServiceImpl;
import com.example.JavaWEB.serviceImpl.TransactionServiceImpl;
import com.example.JavaWEB.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SessionAttributes("transaction")
@Controller
public class TransactionController {
    private final TransactionServiceImpl transactionServiceImpl;
    private final PaymentCellServiceImpl paymentCellServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final CardServiceImpl cardServiceImpl;

    @Autowired
    public TransactionController(TransactionServiceImpl transactionServiceImpl, PaymentCellServiceImpl paymentCellServiceImpl,
                                 UserServiceImpl userServiceImpl, CardServiceImpl cardServiceImpl) {
        this.transactionServiceImpl = transactionServiceImpl;
        this.paymentCellServiceImpl = paymentCellServiceImpl;
        this.userServiceImpl = userServiceImpl;
        this.cardServiceImpl = cardServiceImpl;
    }

    @ModelAttribute(name = "transaction")
    public Transaction transaction() {
        return new Transaction();
    }

    @ModelAttribute(name = "amount")
    public double amount() {
        double amount = 0;
        return amount;
    }

    @GetMapping("/userId{userId}/transactions")
    public String transactionsList(@PathVariable(value = "userId") Long userId, Model model) {
        //  model.addAttribute("transactions", transactionService.findTransactionsByUserId(userId));
        return findPaginated(userId, 1, "name", "asc", model);
    }

    @GetMapping("/transaction/id{id}/pay")
    public String transactionInfo(@PathVariable(value = "id") Long id, Model model) {

        //Optimal card calculation


        List<Card> userCards = StreamSupport.stream(userServiceImpl.findAllUserCards().spliterator(), false)
                .collect(Collectors.toList());

        List<Card> userCardsByOptimality = new ArrayList<>();

        int userCardsOriginalSize = userCards.size();



       /* Card optimalCard = new Card();
        for (int i=0; i < userCardsOriginalSize-1; i++) {

            Optimization optimization = new Optimization();

            optimalCard = optimization.calculate(userCards);

            // userCards.remove(optimalCard);
            userCards.remove(optimalCard);
            userCardsByOptimality.add(optimalCard);
        }

        userCardsByOptimality.addAll(userCards);

        optimalCard = userCardsByOptimality.get(0);

        model.addAttribute("paymentCell", paymentCellServiceImpl.findById(id).get());
        model.addAttribute("cards", userCardsByOptimality);
        model.addAttribute("optimalCard", optimalCard);*/

        model.addAttribute("paymentCell", paymentCellServiceImpl.findById(id).get());
        Card optimalCard = new Optimization().calculate(userCards);
        userCards.remove(optimalCard);
        userCards.add(0,optimalCard);
        model.addAttribute("cards", userCards);
        model.addAttribute("optimalCard", optimalCard);
        return "transactionInfo";
    }

    @PostMapping("/transaction/id{id}/pay")
    public String selectCardForTransaction(@PathVariable(value = "id") Long id,
                                           @ModelAttribute(name = "transaction") @Valid Transaction transaction,
                                           Errors errors,
                                           Model model) {
       /* if (errors.hasErrors()) {
            model.addAttribute("paymentCell", paymentCellServiceImpl.findById(id).get());//передаем в шаблон
            model.addAttribute("cards", userServiceImpl.findAllUserCards());
            return "transactionInfo";
        }*/
        return "redirect:/transaction/id{id}/confirm";
    }

    @GetMapping("/transaction/id{id}/confirm")
    public String transactionConfirmView(@PathVariable(value = "id") Long id, @ModelAttribute(name = "transaction") Transaction transaction, Model model) {
        /*if (!paymentCellRepository.existsById(id)) {
            return "redirect:/cells";
        }*/

        model.addAttribute("paymentCell", paymentCellServiceImpl.findById(id).get());//передаем в шаблон
        model.addAttribute("amount", transaction.getAmount());
        model.addAttribute("message");

        return "transactionConfirm";
    }

    @PostMapping("/transaction/id{id}/confirm")
    public String handleTransactionConfirm(@PathVariable(value = "id") Long id,
                                           @ModelAttribute(name = "transaction") Transaction transaction,
                                           @RequestParam double amount,
                                           Model model,
                                           RedirectAttributes redirectAttributes,
                                           SessionStatus sessionStatus) {
        try {

            transactionServiceImpl.handleTransaction(id, transaction.getCard(), amount);
            //   if(transactionService.handleTransaction(id, selectedCardId, amount) != null) return "redirect:/cells";
        } catch (Exception exception) {

            redirectAttributes.addFlashAttribute("amount", amount);
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/transaction/id{id}/confirm";
        }

        redirectAttributes.addFlashAttribute("selectedCard", transaction.getCard());
        redirectAttributes.addFlashAttribute("amount", transaction.getAmount());
        redirectAttributes.addFlashAttribute("paymentCell", paymentCellServiceImpl.findById(id).get());
        return "redirect:/transaction/id{id}/success";

    }

    @GetMapping("transaction/id{id}/success")
    public String transactionSuccess(@PathVariable(value = "id") Long id,
                                     Model model,
                                     SessionStatus sessionStatus) {
        if (!paymentCellServiceImpl.existsById(id)) {
            return "redirect:/cells";
        }
        model.addAttribute("paymentCell");
        model.addAttribute("selectedCard");
        model.addAttribute("amount");
        ////////////////////////////////////////////////
        sessionStatus.setComplete();
        return "transactionSuccess";
    }

    @GetMapping("/userId{userId}/transactions/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "userId") Long userId,
                                @PathVariable(value = "pageNo") int pageNo,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDir") String sortDir,
                                Model model) {
        int pageSize = 5;
        Page<Transaction> page = transactionServiceImpl.findPaginated(userId, pageNo, pageSize, sortField, sortDir);
        List<Transaction> transactions = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());


        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("transactions", transactions);
        return "transactionsList";

    }
}

