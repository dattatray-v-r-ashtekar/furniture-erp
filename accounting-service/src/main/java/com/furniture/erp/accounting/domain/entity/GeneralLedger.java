package com.furniture.erp.accounting.domain.entity;

import com.furniture.erp.domain.entity.AggregateRoot;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "general_ledgers")
public class GeneralLedger extends AggregateRoot<UUID> {

    @Id
    private UUID id;
    private String referenceCode;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "general_ledger_id")
    private List<JournalEntry> items = new ArrayList<>();

    protected GeneralLedger() {
    }

    public GeneralLedger(String referenceCode) {
        this.id = UUID.randomUUID();
        this.referenceCode = referenceCode;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getReferenceCode() { return referenceCode; }
    public List<JournalEntry> getItems() { return items; }

    public void addItem(JournalEntry item) {
        this.items.add(item);
    }
}
