package com.scoopshack.model;

/** Anything that can be billed must be able to report these four amounts. */
public interface Billable {
    double getSubtotal();
    double getDiscountAmount();
    double getGstAmount();
    double getTotal();
}
