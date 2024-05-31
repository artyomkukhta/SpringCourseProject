package com.example.JavaWEB.serviceImpl;

import com.example.JavaWEB.model.*;
import com.example.JavaWEB.repository.CardRepository;
import com.example.JavaWEB.repository.PaymentCellRepository;
import com.example.JavaWEB.repository.TransactionRepository;
import com.example.JavaWEB.repository.UserRepository;
import com.example.JavaWEB.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("transactionService")
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final CardRepository selectedCardRepository;
    private final UserRepository userRepository;
    private final PaymentCellRepository paymentCellRepository;
    private final CardServiceImpl cardService;
    private final CardRepository cardRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, CardRepository selectedCardRepository, UserRepository userRepository, PaymentCellRepository paymentCellRepository, CardServiceImpl cardService, CardRepository cardRepository) {
        this.transactionRepository = transactionRepository;
        this.selectedCardRepository = selectedCardRepository;
        this.userRepository = userRepository;
        this.paymentCellRepository = paymentCellRepository;
        this.cardService = cardService;
        this.cardRepository = cardRepository;
    }

    public Set<Transaction> findTransactionsByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        List<Card> selectedCards = user.get().getCards();
        Set<Transaction> transactions = new HashSet<>();
        for (Card selectedCard : selectedCards) {
            transactions.addAll(selectedCard.getTransactions());
        }
        return transactions;
    }

    public Optional<Transaction> findById(Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isPresent()) return transaction;
        return null;
    }

    public boolean add(Transaction transaction) {
        transactionRepository.save(transaction);
        return true;
    }

    public boolean add(String name, Card selectedCard, double amount) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        Transaction transaction = new Transaction(name, selectedCard, amount, date.toString(), TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);
        return true;
    }

    public boolean delete(Transaction transaction) {
        transactionRepository.delete(transaction);
        return true;
    }

    public boolean deleteById(Long id) {
        transactionRepository.deleteById(id);
        return true;
    }

    public String handleTransaction(Long cellId, Card selectedCard, double amount) throws Exception {
        Optional<PaymentCell> paymentCell = paymentCellRepository.findById(cellId);
        if (selectedCard.getIsBlocked()) throw new Exception("Card blocked!");
        if (selectedCard == null) throw new Exception("Card is not selected!");

        double commissionRate = selectedCard.getCardType().getCommission() * 0.01;
        double cashbackRate = selectedCard.getCardType().getCashback() * 0.01;
        double commissionAmount = amount * commissionRate;
        double cashbackAmount = amount * cashbackRate;
        double finalAmount = amount + commissionAmount - cashbackAmount;

        if (selectedCard.getBalance() < finalAmount) throw new Exception("Insufficient balance!");

        selectedCard.setBalance(selectedCard.getBalance() - finalAmount);
        selectedCard.setOperationSum(selectedCard.getOperationSum() + amount);
        Date date = new Date();

        Transaction transaction = new Transaction(paymentCell.get().getName(), selectedCard, amount, date.toString(), TransactionStatus.SUCCESS);
        selectedCard.getTransactions().add(transaction);
        transactionRepository.save(transaction);
        selectedCardRepository.save(selectedCard);
        paymentCellRepository.save(paymentCell.get());
        return null;
    }

    public Page<Transaction> findPaginated(Long userId, int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

        Optional<User> user = userRepository.findById(userId);
        List<Card> cards = user.get().getCards();

      /*  for (Card selectedCard : cards) {
            transactions.addAll(selectedCard.getTransactions());
        }*/

      //  Page<Transaction> page = new PageImpl<>(transactions, pageable, transactions.size());
       // return transactionRepository.findAllByCard(cards., pageable);//sort вместо pageable

        return transactionRepository.findByCardIn(cards, pageable);
    }



    @Override
    @Transactional
    public void transferById(Long fromCardId, Long toCardId, Double amount) {
        Card fromCard = cardRepository.findById(fromCardId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid fromCardId"));
        Card toCard = cardRepository.findById(toCardId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid toCardId"));

        // Calculate the actual amount to be deducted considering the commission and cashback
        double commissionRate = fromCard.getCardType().getCommission() * 0.01;
        double cashbackRate = fromCard.getCardType().getCashback() * 0.01;
        double actualAmountToDeduct = amount + (amount * commissionRate) - (amount * cashbackRate);

        // Проверяем, достаточно ли средств на карте отправителя
        if (fromCard.getBalance() < actualAmountToDeduct) {
            throw new IllegalArgumentException("Ошибка: Недостаточно средств на карте отправителя");
        }

        // Выполняем перевод
        fromCard.setBalance(fromCard.getBalance() - actualAmountToDeduct);
        toCard.setBalance(toCard.getBalance() + amount);

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }

    @Override
    @Transactional
    public void transferByNumber(Long fromCardId, String toCardNumber, Double amount) {
        Card fromCard = cardRepository.findById(fromCardId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid fromCardId"));
        Card toCard = cardRepository.findByNumber(toCardNumber)
                .orElseThrow(() -> new IllegalArgumentException("Invalid toCardNumber"));

        // Calculate the actual amount to be deducted considering the commission and cashback
        double commissionRate = fromCard.getCardType().getCommission() * 0.01;
        double cashbackRate = fromCard.getCardType().getCashback() * 0.01;
        double actualAmountToDeduct = amount + (amount * commissionRate) - (amount * cashbackRate);

        // Проверяем, достаточно ли средств на карте отправителя
        if (fromCard.getBalance() < actualAmountToDeduct) {
            throw new IllegalArgumentException("Ошибка: Недостаточно средств на карте отправителя");
        }

        // Выполняем перевод
        fromCard.setBalance(fromCard.getBalance() - actualAmountToDeduct);
        toCard.setBalance(toCard.getBalance() + amount);

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }
    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}