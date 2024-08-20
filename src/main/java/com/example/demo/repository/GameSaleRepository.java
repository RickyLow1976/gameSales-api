package com.example.demo.repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.demo.model.GameSale;

import jakarta.transaction.Transactional;

public interface GameSaleRepository extends JpaRepository<GameSale, Long> {
	

	Page<GameSale> findAllByDateOfSaleBetweenAndSalePriceGreaterThanEqual(LocalDateTime fromDate, LocalDateTime toDate,
			BigDecimal salePrice, Pageable pageable);

	    @Query("SELECT COUNT(g) FROM GameSale g WHERE g.dateOfSale BETWEEN :startDate AND :endDate")
		static
	    long countByDateOfSaleBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate) {
			// TODO Auto-generated method stub
			return 0;
		}

	    @Query("SELECT SUM(g.salePrice) FROM GameSale g WHERE g.dateOfSale BETWEEN :startDate AND :endDate")
		static
	    BigDecimal sumSalePriceByDateOfSaleBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate) {
			// TODO Auto-generated method stub
			return null;
		}

	    @Query("SELECT SUM(g.salePrice) FROM GameSale g WHERE g.dateOfSale BETWEEN :startDate AND :endDate AND g.gameNo = :gameNo")
		static
	    BigDecimal sumSalePriceByDateOfSaleBetweenAndGameNo(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("gameNo") int gameNo) {
			// TODO Auto-generated method stub
			return null;
		}


}


