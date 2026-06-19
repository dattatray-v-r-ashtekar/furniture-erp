package com.furniture.erp.qms.domain.entity;

import com.furniture.erp.domain.entity.AggregateRoot;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "quality_inspections")
public class QualityInspection extends AggregateRoot<UUID> {

    @Id
    private UUID id;
    private String referenceCode;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "quality_inspection_id")
    private List<DefectLog> items = new ArrayList<>();

    protected QualityInspection() {
    }

    public QualityInspection(String referenceCode) {
        this.id = UUID.randomUUID();
        this.referenceCode = referenceCode;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getReferenceCode() { return referenceCode; }
    public List<DefectLog> getItems() { return items; }

    public void addItem(DefectLog item) {
        this.items.add(item);
    }
}
