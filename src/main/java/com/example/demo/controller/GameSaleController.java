package com.example.demo.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.GameSale;
import com.example.demo.repository.GameSaleRepository;
import com.example.demo.service.GameSaleQueryService;
import com.example.demo.service.GameSaleService;

import jakarta.transaction.Transactional;

@RestController
public class GameSaleController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/import")
    public String importGames(@RequestParam("file") MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<GameSale> gameSalesList = new ArrayList<>();
            String line;
            int count = 0;

            // Skip the header row
            reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                GameSale gameSales = new GameSale();
                gameSales.setGameNo(Integer.parseInt(fields[1]));
                gameSales.setGameName(fields[2]);
                gameSales.setGameCode(fields[3]);
                gameSales.setType(Integer.parseInt(fields[4]));
                gameSales.setCostPrice(new BigDecimal(fields[5]));
                gameSales.setSalePrice(new BigDecimal(fields[7]));
                gameSales.setDateOfSale(LocalDateTime.parse(fields[8].trim(), DATE_TIME_FORMATTER));

                gameSalesList.add(gameSales);
//                redisTemplate.opsForList().leftPush("game_sales_list", gameSales);

                // Perform batch insert in chunks of 1000 records
                if (count % 10000 == 0 && count > 0) {
                	redisTemplate.opsForList().leftPush("game_sales_list", gameSalesList);
                	processBatch();
//                	gameSaleService.bulkInsert(gameSalesList);
//                    gameSalesList.clear();
                }
                count++;
            }

            // Insert the remaining records
            if (!gameSalesList.isEmpty()) {
//            	gameSaleService.bulkInsert(gameSalesList);
            	processBatch();
            }

            return "Successfully imported " + count + " records.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to import games: " + e.getMessage();
        }
    }
//    @PostMapping("/import")
//    public ResponseEntity<String> importGameSales(@RequestParam("file") MultipartFile file) {
//        try {
//            gameSaleService.importGameSales(file);
//            return ResponseEntity.ok("Import successful");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to import games: " + e.getMessage());
//        }
//    }
    @Transactional
    private void processBatch() {
        List<Object> gameSalesList = redisTemplate.opsForList().range("game_sales_list", 0, -1);
        // Convert gameSalesList from Redis data to your entity list and perform bulk insert
        redisTemplate.opsForList().trim("game_sales_list", 1, 0); // Clear the list after processing
    }
    
    @GetMapping("/getGameSales")
    public ResponseEntity<Page<GameSale>> getGameSales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(required = false) BigDecimal salePrice,
            Pageable pageable) {
        Page<GameSale> gameSales = GameSaleQueryService.getGameSales(fromDate, toDate, salePrice, pageable);
        return ResponseEntity.ok(gameSales);
    }
    
    @GetMapping("/getTotalSales")
    public ResponseEntity<Map<String, Object>> getTotalSales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(required = false) Integer gameNo) {
        Map<String, Object> response = new HashMap<>();
        if (gameNo == null) {
            response.put("totalSales", GameSaleRepository.sumSalePriceByDateOfSaleBetween(fromDate, toDate));
            response.put("totalCount", GameSaleRepository.countByDateOfSaleBetween(fromDate, toDate));
        } else {
            response.put("totalSales", GameSaleRepository.sumSalePriceByDateOfSaleBetweenAndGameNo(fromDate, toDate, gameNo));
        }
        return ResponseEntity.ok(response);
    }


}

