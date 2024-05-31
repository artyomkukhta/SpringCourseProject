package com.example.JavaWEB.serviceImpl;

import com.example.JavaWEB.model.Card;
import com.example.JavaWEB.model.CardType;
import com.example.JavaWEB.model.User;
import com.example.JavaWEB.repository.CardRepository;
import com.example.JavaWEB.repository.TransactionRepository;
import com.example.JavaWEB.repository.UserRepository;
import com.example.JavaWEB.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("cardService")
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    private final UserServiceImpl userServiceImpl;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository, UserRepository userRepository, TransactionRepository transactionRepository, UserServiceImpl userServiceImpl) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.userServiceImpl = userServiceImpl;
    }

    public Iterable<Card> findAll() {
        return cardRepository.findAll();
    }

    public Optional<Card> findById(Long id) {
        Optional<Card> card = cardRepository.findById(id);
        if (card.isPresent()) return card;
        return null;
    }

    public boolean add(Long userId, Card card) {
        Optional<User> user = userRepository.findById(userId);
        user.get().getCards().add(card);
        card.setUser(user.get());
        cardRepository.save(card);
        userRepository.save(user.get());
        return true;
    }

    public boolean add(Long userId, String name, CardType cardType, String number, double balance, boolean isBlocked) {
        Card card = new Card(name, cardType, number, balance, false);
        Optional<User> user = userRepository.findById(userId);
        card.setUser(user.get());
        user.get().getCards().add(card);
        userRepository.save(user.get());
        cardRepository.save(card);
        return true;
    }

    public boolean delete(Card card) {
        cardRepository.delete(card);
        return true;
    }

    public boolean deleteById(Long id) {
        cardRepository.deleteById(id);
        return true;
    }

    public boolean editById(Long id, String name, String number, double balance, boolean isBlocked) {
        Optional<Card> card = cardRepository.findById(id);
        card.get().setName(name);
        card.get().setNumber(number);
        card.get().setBalance(balance);
        card.get().setIsBlocked(isBlocked);
        cardRepository.save(card.get());
        return true;
    }

    public boolean existsById(Long id) {
        return cardRepository.existsById(id);
    }

    public Page<Card> findPaginated(Long userId, int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

        Optional<User> user = userRepository.findById(userId);
        List<Card> cards = user.get().getCards();

        return cardRepository.findAllByUser(user.get(), pageable);
    }

    public boolean addBalanceById(Long cardId, double amount) {
        Card card = cardRepository.findById(cardId).get();
        card.setBalance(card.getBalance() + amount);
        cardRepository.save(card);
        return true;
    }

    public void save(Card card) {
        cardRepository.save(card);
    }

}
