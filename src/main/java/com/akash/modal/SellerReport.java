package com.akash.modal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SellerReport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private Seller seller;

    private Long totalEarning=0L;

    private Long totalSells=0L;

    private Long totalRefunds=0L;

    private Long totalTax=0L;

    private Long netEarnings=0L;

    private Long totalOrders=0L;

    private Long cancelOrders=0L;

    private Long totalTransactions=0L;

}
