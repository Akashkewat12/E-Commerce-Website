package com.akash.repository;

import com.akash.modal.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Seller findByEmail(String email);

}
