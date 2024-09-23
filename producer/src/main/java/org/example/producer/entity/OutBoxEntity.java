package org.example.producer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.OffsetDateTime;

@Entity
@Table(name = "outbox")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutBoxEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "destination", nullable = false)
    private String destination;

    @JdbcTypeCode(Types.BINARY)
    @Column(name = "payload", nullable = false)
    private byte[] payload;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
