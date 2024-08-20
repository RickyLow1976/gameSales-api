package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.model.GameSale;
import com.example.demo.repository.GameSaleRepository;

@Service
public class GameSaleQueryService {

    @Autowired
    private static GameSaleRepository gameSaleRepository;

    public static Page<GameSale> getGameSales(LocalDateTime fromDate, LocalDateTime toDate, BigDecimal salePrice, Pageable pageable) {
        return gameSaleRepository.findAllByDateOfSaleBetweenAndSalePriceGreaterThanEqual(fromDate, toDate, salePrice, pageable);
    }
}

