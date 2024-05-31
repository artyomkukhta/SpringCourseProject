package com.example.JavaWEB.serviceImpl;

import com.example.JavaWEB.model.PaymentCell;
import com.example.JavaWEB.repository.PaymentCellRepository;
import com.example.JavaWEB.service.PaymentCellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("paymentCellService")
public class PaymentCellServiceImpl implements PaymentCellService {

    private final PaymentCellRepository paymentCellRepository;

    @Autowired
    public PaymentCellServiceImpl(PaymentCellRepository paymentCellRepository) {
        this.paymentCellRepository = paymentCellRepository;
    }

    public boolean existsById(Long id){
        return paymentCellRepository.existsById(id);
    }

    public List<PaymentCell> findAll(){
        return paymentCellRepository.findAll()
                .stream()
                .sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
                .collect(Collectors.toList());
    }

    public Optional<PaymentCell> findById(Long id){
        return paymentCellRepository.findById(id);
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
        if (paymentCell.isPresent()) {
            PaymentCell cell = paymentCell.get();
            cell.setName(name);
            cell.setType(type);
            cell.setDesc(desc);
            paymentCellRepository.save(cell);
            return true;
        }
        return false;
    }

    public List<PaymentCell> searchByName(String name) {
        return paymentCellRepository.findAll().stream()
                .filter(cell -> cell.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
}
