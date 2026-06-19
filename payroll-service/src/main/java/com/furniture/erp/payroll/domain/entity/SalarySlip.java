package com.furniture.erp.payroll.domain.entity;

import com.furniture.erp.domain.entity.AggregateRoot;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "salary_slips")
public class SalarySlip extends AggregateRoot<UUID> {

    @Id
    private UUID id;
    private String referenceCode;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "salary_slip_id")
    private List<TaxDeduction> items = new ArrayList<>();

    protected SalarySlip() {
    }

    public SalarySlip(String referenceCode) {
        this.id = UUID.randomUUID();
        this.referenceCode = referenceCode;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getReferenceCode() { return referenceCode; }
    public List<TaxDeduction> getItems() { return items; }

    public void addItem(TaxDeduction item) {
        this.items.add(item);
    }
}
