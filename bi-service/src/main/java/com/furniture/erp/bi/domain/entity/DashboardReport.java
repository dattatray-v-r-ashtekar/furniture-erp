package com.furniture.erp.bi.domain.entity;

import com.furniture.erp.domain.entity.AggregateRoot;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dashboard_reports")
public class DashboardReport extends AggregateRoot<UUID> {

    @Id
    private UUID id;
    private String referenceCode;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "dashboard_report_id")
    private List<KpiMetric> items = new ArrayList<>();

    protected DashboardReport() {
    }

    public DashboardReport(String referenceCode) {
        this.id = UUID.randomUUID();
        this.referenceCode = referenceCode;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getReferenceCode() { return referenceCode; }
    public List<KpiMetric> getItems() { return items; }

    public void addItem(KpiMetric item) {
        this.items.add(item);
    }
}
