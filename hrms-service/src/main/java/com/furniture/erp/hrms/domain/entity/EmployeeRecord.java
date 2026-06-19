package com.furniture.erp.hrms.domain.entity;

import com.furniture.erp.domain.entity.AggregateRoot;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "employee_records")
public class EmployeeRecord extends AggregateRoot<UUID> {

    @Id
    private UUID id;
    private String referenceCode;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "employee_record_id")
    private List<AttendanceLog> items = new ArrayList<>();

    protected EmployeeRecord() {
    }

    public EmployeeRecord(String referenceCode) {
        this.id = UUID.randomUUID();
        this.referenceCode = referenceCode;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getReferenceCode() { return referenceCode; }
    public List<AttendanceLog> getItems() { return items; }

    public void addItem(AttendanceLog item) {
        this.items.add(item);
    }
}
