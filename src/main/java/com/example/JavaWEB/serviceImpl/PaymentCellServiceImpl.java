package com.example.JavaWEB.serviceImpl;

import com.example.JavaWEB.model.PaymentCell;
import com.example.JavaWEB.repository.PaymentCellRepository;
import com.example.JavaWEB.service.PaymentCellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("paymentCellService")
public class PaymentCellServiceImpl implements PaymentCellService {

    private final PaymentCellRepository paymentCellRepository;

    @Autowired
    public PaymentCellServiceImpl(PaymentCellRepository paymentCellRepository) {
        this.paymentCellRepository = paymentCellRepository;
    }
    public boolean existsById(Long id){
        if (paymentCellRepository.existsById(id)) return true;
        else return false;
    }
    public Iterable<PaymentCell> findAll(){
        return paymentCellRepository.findAll();
    }

    public Optional<PaymentCell> findById(Long id){
        Optional<PaymentCell> paymentCell = paymentCellRepository.findById(id);
        if (paymentCell.isPresent()) return paymentCell;
        return null;
    }
    public boolean add(PaymentCell paymentCell){
        paymentCellRepository.save(paymentCell);
        return true;
    }
    public boolean add(String name, String type, String desc){
        PaymentCell paymentCell = new PaymentCell(name, type, desc);
        paymentCellRepository.save(paymentCell);
        return true;
    }
    public boolean delete(PaymentCell paymentCell){
        paymentCellRepository.delete(paymentCell);
        return true;
    }
    public boolean deleteById(Long id){

        paymentCellRepository.deleteById(id);
        return true;
    }

    public boolean edit(PaymentCell paymentCell, String name, String type, String desc){
        paymentCell.setName(name);
        paymentCell.setType(type);
        paymentCell.setDesc(desc);
        paymentCellRepository.save(paymentCell);
        return true;
    }
    public boolean editById(Long id, String name, String type, String desc){
        Optional<PaymentCell> paymentCell = paymentCellRepository.findById(id);
        paymentCell.get().setName(name);
        paymentCell.get().setType(type);
        paymentCell.get().setDesc(desc);
        paymentCellRepository.save(paymentCell.get());
        return true;
    }
}
