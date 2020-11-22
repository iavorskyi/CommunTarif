package com.iavorskyi.repos;

import com.iavorskyi.domain.ComService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComServiceRepo extends JpaRepository<ComService, Long> {
    ComService findOneById(Long id);
    
}
