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
    private String accountId = "REVENUE-B2C";
    private String entryType = "CREDIT";
    private Double amount = 45000.00;
    private String description = "B2C Online Order Payment";

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "general_ledger_id")
    private List<JournalEntry> items = new ArrayList<>();

    protected GeneralLedger() {
    }

    public GeneralLedger(String referenceCode) {
        this.id = UUID.randomUUID();
        this.referenceCode = referenceCode;
        this.accountId = "REVENUE-B2C";
        this.entryType = "CREDIT";
        this.amount = 45000.00;
        this.description = "B2C Online Order Revenue for " + referenceCode;
        super.setId(this.id);
    }

    public GeneralLedger(UUID id, String referenceCode, String accountId, String entryType, Double amount, String description) {
        this.id = id != null ? id : UUID.randomUUID();
        this.referenceCode = referenceCode;
        this.accountId = accountId != null ? accountId : "REVENUE-B2C";
        this.entryType = entryType != null ? entryType : "CREDIT";
        this.amount = amount != null ? amount : 0.0;
        this.description = description;
        super.setId(this.id);
    }

    public GeneralLedger(String referenceCode, String accountId, String entryType, Double amount, String description) {
        this(UUID.randomUUID(), referenceCode, accountId, entryType, amount, description);
    }

    public UUID getId() { return id; }
    public String getReferenceCode() { return referenceCode; }
    public String getAccountId() { return accountId; }
    public String getEntryType() { return entryType; }
    public Double getAmount() { return amount; }
    public String getDescription() { return description; }
    public List<JournalEntry> getItems() { return items; }

    public void addItem(JournalEntry item) {
        this.items.add(item);
    }
}
