package com.furniture.erp.payroll.domain.entity;

import com.furniture.erp.domain.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "tax_deductions")
public class TaxDeduction extends BaseEntity<UUID> {

    @Id
    private UUID id;
    private String description;

    protected TaxDeduction() {
    }

    public TaxDeduction(String description) {
        this.id = UUID.randomUUID();
        this.description = description;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getDescription() { return description; }
}
