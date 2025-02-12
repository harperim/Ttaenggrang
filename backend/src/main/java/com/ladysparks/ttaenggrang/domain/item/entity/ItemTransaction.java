package com.ladysparks.ttaenggrang.domain.item.entity;
import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "item_transaction")
public class ItemTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private Student buyer;

    @Column(nullable = false)
    private int quantity;

    @CreationTimestamp
    private Timestamp createdAt;

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }
}
