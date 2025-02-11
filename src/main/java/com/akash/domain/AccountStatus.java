package com.akash.domain;

public enum AccountStatus {

    PENDING_VERIFICATION, //Account is created but not yet verified
    ACTIVE, //Account is ACTIVE AND IN GOOD STANDING
    SUSPENDED, // Account is TEMPORARY SUSPENDED POSSIBLY DUE TO VIOLATIONS
    BANNED,  //Account is  permanently banned due to server violations it
    CLOSED   // Account is permanently closed, possibly at user request
}
