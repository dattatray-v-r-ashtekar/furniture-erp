package com.furniture.erp.hrms.domain.entity;

import com.furniture.erp.domain.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "attendance_logs")
public class AttendanceLog extends BaseEntity<UUID> {

    @Id
    private UUID id;
    private String description;

    protected AttendanceLog() {
    }

    public AttendanceLog(String description) {
        this.id = UUID.randomUUID();
        this.description = description;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getDescription() { return description; }
}
