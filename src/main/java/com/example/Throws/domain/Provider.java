package com.example.Throws.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
public class Provider {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String providerName;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private List<Member> members;

}
