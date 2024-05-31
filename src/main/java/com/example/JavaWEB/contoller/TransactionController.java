package com.example.JavaWEB.contoller;

import com.example.JavaWEB.model.Card;
import com.example.JavaWEB.model.Transaction;
import com.example.JavaWEB.model.TransactionStatus;
import com.example.JavaWEB.optimization.Optimization;
import com.example.JavaWEB.serviceImpl.CardServiceImpl;
import com.example.JavaWEB.serviceImpl.PaymentCellServiceImpl;
import com.example.JavaWEB.serviceImpl.TransactionServiceImpl;
import com.example.JavaWEB.serviceImpl.UserServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SessionAttributes("transaction")
@Controller
public class TransactionController {
    private static final Logger log = LogManager.getLogger(TransactionController.class);
    private final TransactionServiceImpl transactionServiceImpl;
    private final PaymentCellServiceImpl paymentCellServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final CardServiceImpl cardServiceImpl;
    private final TransactionServiceImpl transactionService;

    @Autowired
    public TransactionController(TransactionServiceImpl transactionServiceImpl, PaymentCellServiceImpl paymentCellServiceImpl,
                                 UserServiceImpl userServiceImpl, CardServiceImpl cardServiceImpl, TransactionServiceImpl transactionService) {
        this.transactionServiceImpl = transactionServiceImpl;
        this.paymentCellServiceImpl = paymentCellServiceImpl;
        this.userServiceImpl = userServiceImpl;
        this.cardServiceImpl = cardServiceImpl;
        this.transactionService = transactionService;
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
    public String transactionInfo(@PathVariable(value = "id") Long id,
                                  @RequestParam(value = "sortField", required = false, defaultValue = "name") String sortField,
                                  @RequestParam(value = "sortDir", required = false, defaultValue = "asc") String sortDir,
                                  Model model) {

        List<Card> userCards = StreamSupport.stream(userServiceImpl.findAllUserCards().spliterator(), false)
                .collect(Collectors.toList());

        // Sort cards based on the provided sortField and sortDir
        userCards.sort((card1, card2) -> {
            int comparison = 0;
            if ("name".equals(sortField)) {
                comparison = card1.getName().compareToIgnoreCase(card2.getName());
            } else if ("balance".equals(sortField)) {
                comparison = Double.compare(card1.getBalance(), card2.getBalance());
            }
            return "asc".equals(sortDir) ? comparison : -comparison;
        });

        model.addAttribute("paymentCell", paymentCellServiceImpl.findById(id).get());
        Card optimalCard = new Optimization().calculate(userCards);
        userCards.remove(optimalCard);
        userCards.add(0, optimalCard);
        model.addAttribute("cards", userCards);
        model.addAttribute("optimalCard", optimalCard);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", "asc".equals(sortDir) ? "desc" : "asc");
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
        // Ensure the transaction has a card associated with it
        Card card = transaction.getCard();
        if (card == null) {
            // Handle case where card is not set
            model.addAttribute("message", "No card selected for the transaction.");
            return "errorPage"; // Return to an appropriate error page or handle accordingly
        }

        double amount = transaction.getAmount();
        double commissionPercentage = card.getCardType().getCommission();
        double cashbackPercentage = card.getCardType().getCashback();

        // Calculate commission and cashback amounts
        double commissionAmount = amount * commissionPercentage / 100;
        double cashbackAmount = amount * cashbackPercentage / 100;

        // Calculate final amount to be deducted
        double finalAmount = amount + commissionAmount - cashbackAmount;

        model.addAttribute("paymentCell", paymentCellServiceImpl.findById(id).get());
        model.addAttribute("amount", amount);
        model.addAttribute("commissionAmount", commissionAmount);
        model.addAttribute("cashbackAmount", cashbackAmount);
        model.addAttribute("finalAmount", finalAmount);
        model.addAttribute("transaction", transaction);
        model.addAttribute("card", card);
        model.addAttribute("message", model.getAttribute("message"));

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
            Card card = transaction.getCard();
            double commissionPercentage = card.getCardType().getCommission();
            double cashbackPercentage = card.getCardType().getCashback();

            // Calculate commission and cashback amounts
            double commissionAmount = amount * commissionPercentage / 100;
            double cashbackAmount = amount * cashbackPercentage / 100;

            // Calculate final amount to be deducted
            double finalAmount = amount + commissionAmount - cashbackAmount;

            // Update card balance
            card.setBalance(card.getBalance() - finalAmount);

            // Save the updated card information
            cardServiceImpl.save(card);

            // Set additional transaction fields
            transaction.setAmount(finalAmount);
            transaction.setDate(LocalDate.now().toString());
            transaction.setCard(card);// Set the current date
            transaction.setStatus(TransactionStatus.SUCCESS); // Set the status to COMPLETED
            transaction.setName(paymentCellServiceImpl.findById(id).get().getName());

            // Save the transaction
            transactionServiceImpl.save(transaction);

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

//    @PostMapping("/userId{userId}/transactions/clear/{transactionId}")
//    public String clearTransaction(@PathVariable("userId") Long userId,
//                                   @PathVariable("transactionId") Long transactionId) {
//        // Вызов метода сервиса для удаления транзакции
//       transactionService.deleteById(transactionId);
//
//        log.info("transactionId: " + transactionId + " deleted");
//        // Перенаправление на список транзакций
//        return "redirect:/userId{userId}/transactions";
//    }

}

