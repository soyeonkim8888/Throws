package com.example.Throws.delete;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DialectOverride;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass                               // 공통 속성만 제공
@Where(clause = "deleted = false")              // 전역 필터
@SQLDelete(sql =
        "UPDATE #{#entityName} SET deleted = true, deleted_at = now() WHERE id = ?")
public abstract class AbstractSoftDeleteEntity implements SoftDeletable {

    @Column(nullable = false)
    protected boolean deleted = false;

    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void markDeleted() {
        this.deleted      = true;
        this.deletedAt    = LocalDateTime.now();
    }
}
