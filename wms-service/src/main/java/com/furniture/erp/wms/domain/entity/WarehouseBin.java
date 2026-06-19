package com.furniture.erp.wms.domain.entity;

import com.furniture.erp.domain.entity.AggregateRoot;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "warehouse_bins")
public class WarehouseBin extends AggregateRoot<UUID> {

    @Id
    private UUID id;
    private String referenceCode;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "warehouse_bin_id")
    private List<BinMovement> items = new ArrayList<>();

    protected WarehouseBin() {
    }

    public WarehouseBin(String referenceCode) {
        this.id = UUID.randomUUID();
        this.referenceCode = referenceCode;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getReferenceCode() { return referenceCode; }
    public List<BinMovement> getItems() { return items; }

    public void addItem(BinMovement item) {
        this.items.add(item);
    }
}
