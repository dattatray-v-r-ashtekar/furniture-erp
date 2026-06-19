package com.furniture.erp.bi.domain.entity;

import com.furniture.erp.domain.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "kpi_metrics")
public class KpiMetric extends BaseEntity<UUID> {

    @Id
    private UUID id;
    private String description;

    protected KpiMetric() {
    }

    public KpiMetric(String description) {
        this.id = UUID.randomUUID();
        this.description = description;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getDescription() { return description; }
}
