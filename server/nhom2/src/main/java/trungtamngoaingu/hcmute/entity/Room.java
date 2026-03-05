package trungtamngoaingu.hcmute.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Room")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roomId;

    @Column(nullable = false, length = 50)
    private String roomName;

    @Column(nullable = false)
    private Integer capacity;

    @Column(length = 100)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('Available', 'Maintenance', 'Inactive') DEFAULT 'Available'")
    private Status status;

    public enum Status {
        Available, Maintenance, Inactive
    }
}
