package com.epam.esm.core.repository;


import com.epam.esm.core.entity.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<UserOrder, Long>{
    List<UserOrder> findByUserId(Long userId);
}