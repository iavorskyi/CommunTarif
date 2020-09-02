package com.iavorskyi.repos;

import com.iavorskyi.domain.ComService;
import com.iavorskyi.domain.Months;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComServiceRepo extends JpaRepository<ComService, Long> {
    public List<ComService> findAllByYear(int year);
    public List<ComService> findAllByMonth(Months month);
    public List<ComService> findAllByYearAndMonth(int year, Months month);
    public ComService findByYearAndMonthAndName(int year, Months months, String name);
    ComService findByName(String name);

    }
