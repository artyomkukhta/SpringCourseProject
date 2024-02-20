package com.example.JavaWEB.service;

import com.example.JavaWEB.model.PaymentCell;

import java.util.Optional;

public interface PaymentCellService {
    public boolean existsById(Long id);

    public Iterable<PaymentCell> findAll();

    public Optional<PaymentCell> findById(Long id);

    public boolean add(PaymentCell paymentCell);

    public boolean add(String name, String type, String desc);

    public boolean delete(PaymentCell paymentCell);

    public boolean deleteById(Long id);

    public boolean edit(PaymentCell paymentCell, String name, String type, String desc);

    public boolean editById(Long id, String name, String type, String desc);

}

