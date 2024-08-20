package com.example;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class CsvGenerator {

	public static void main(String[] args) {
        String csvFile = "games_data.csv";
        String delimiter = ",";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Random random = new Random();

        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.append("id,game_no,game_name,game_code,type,cost_price,tax,sale_price,date_of_sale\n");

            for (int i = 1; i <= 1000000; i++) {
                int gameNo = random.nextInt(100) + 1;
                String gameName = "Game" + gameNo;
                String gameCode = "G" + gameNo;
                int type = random.nextInt(2) + 1;
                BigDecimal costPrice = BigDecimal.valueOf(random.nextDouble() * 100).setScale(2, RoundingMode.HALF_UP);
                BigDecimal tax = costPrice.multiply(BigDecimal.valueOf(0.09)).setScale(2, RoundingMode.HALF_UP);
                BigDecimal salePrice = costPrice.add(tax);
                LocalDateTime dateOfSale = LocalDateTime.of(2024, 4, random.nextInt(30) + 1, random.nextInt(24), random.nextInt(60), random.nextInt(60));

                writer.append(String.format(
                		"%d" //i
                		+ "%s" //delimiter
                		+ "%d" //gameNo
                		+ "%s" //delimiter
                		+ "%s" //gameName
                		+ "%s" //delimiter
                		+ "%s" //gameCode
                		+ "%s" //delimiter
                		+ "%s" //type
                		+ "%s" //delimiter
                		+ "%s" //costPrice
                		+ "%s" //delimiter
                		+ "%s" //tax
                		+ "%s" //delimiter
                		+ "%s" //salePrice
                		+ "%s" //delimiter
                		+ "%s " //dateOfSale
                		+ "\n",
                		i,
                		delimiter,
                		gameNo,
                		delimiter,
                		gameName,
                		delimiter,
                		gameCode,
                		delimiter,
                		type,
                		delimiter,
                		costPrice,
                		delimiter,
                		tax,
                		delimiter,
                		salePrice,
                		delimiter,
                		dateOfSale.format(formatter)));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
