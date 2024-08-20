package com.example.demo.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import java.io.Reader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.GameSale;
import com.example.demo.repository.GameSaleRepository;

import jakarta.transaction.Transactional;

@Service
public class GameSaleService {

    @Transactional
    public void importGameSales(MultipartFile file) throws Exception {
    	final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    	List<GameSale> games = new ArrayList<>();
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(reader);
            for (CSVRecord record : records) {
                GameSale game = new GameSale();
                game.setGameNo(Integer.parseInt(record.get("game_no")));
                game.setGameName(record.get("game_name").trim());
                game.setGameCode(record.get("game_code").trim());
                game.setType(Integer.parseInt(record.get("type")));
                game.setCostPrice(new BigDecimal(record.get("cost_price")));
                game.setSalePrice(new BigDecimal(record.get("sale_price")));
                game.setDateOfSale(LocalDateTime.parse(record.get("date_of_sale").trim(),formatter));
                games.add(game);
                
                if (games.size() % 100000 == 0) {
                	bulkInsert(games);
                    games.clear();
                }
            }
            if (!games.isEmpty()) {
            	bulkInsert(games);
            }
        }
    }
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void bulkInsert(List<GameSale> gameSalesList) {
        String sql = "INSERT INTO game_sales (game_no, game_name, game_code, type, cost_price, sale_price, date_of_sale) VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                GameSale gameSales = gameSalesList.get(i);
                ps.setInt(1, gameSales.getGameNo());
                ps.setString(2, gameSales.getGameName());
                ps.setString(3, gameSales.getGameCode());
                ps.setInt(4, gameSales.getType());
                ps.setBigDecimal(5, gameSales.getCostPrice());
                ps.setBigDecimal(6, gameSales.getSalePrice());
                ps.setTimestamp(7, Timestamp.valueOf(gameSales.getDateOfSale()));
            }

            @Override
            public int getBatchSize() {
                return gameSalesList.size();
            }
        });
    }
}

