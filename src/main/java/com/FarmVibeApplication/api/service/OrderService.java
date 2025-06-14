package com.FarmVibeApplication.api.service;

import com.FarmVibeApplication.api.Repository.OrderRepository;
import com.FarmVibeApplication.api.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepo;
    public Order save(Order o) {
        return orderRepo.save(o); }
}

