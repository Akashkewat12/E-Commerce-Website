package com.akash.repository;

import com.akash.modal.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.smartcardio.Card;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
