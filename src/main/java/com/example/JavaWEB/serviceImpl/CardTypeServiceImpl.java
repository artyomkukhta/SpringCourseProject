package com.example.JavaWEB.serviceImpl;

import com.example.JavaWEB.model.CardType;
import com.example.JavaWEB.repository.CardTypeRepository;
import com.example.JavaWEB.service.CardTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("cardTypeService")
public class CardTypeServiceImpl implements CardTypeService {

    CardTypeRepository cardTypeRepository;

    @Autowired
    public CardTypeServiceImpl(CardTypeRepository cardTypeRepository) {
        this.cardTypeRepository = cardTypeRepository;
    }

    @Override
    public Iterable<CardType> findAll() {
        return cardTypeRepository.findAll();
    }

    @Override
    public Optional<CardType> findById(Long id) {
        return cardTypeRepository.findById(id);
    }

    @Override
    public Optional<CardType> findByName(String name) {
        return cardTypeRepository.findByName(name);
    }

    @Override
    public boolean add(Long userId, CardType cardType) {
        cardTypeRepository.save(cardType);
        return true;
    }

    @Override
    public boolean delete(CardType cardType) {
        cardTypeRepository.delete(cardType);
        return true;
    }

    @Override
    public boolean deleteById(Long id) {
        cardTypeRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean editById(Long id, CardType cardType) {
        //cardTypeRepository.save()
        return false;
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public CardType save(CardType cardType) {
        return cardTypeRepository.save(cardType);
    }
}
