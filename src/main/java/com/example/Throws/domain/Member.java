package com.example.Throws.domain;

import com.example.Throws.delete.AbstractSoftDeleteEntity;
import jakarta.persistence.*;
import lombok.*;

//@Setter
// 모든 필드를 외부에서 바꿀 수 있게 노출함. -> 삭제/
// 값 수정이 "비지니스 규칙"없이 이뤄져도 안전한 필드만 선택적으로 사용
@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE) //Builder 전용
@NoArgsConstructor(access = AccessLevel.PROTECTED) //JPA 기본 생성자
public class Member extends AbstractSoftDeleteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @JsonIgnore :이 어노테이션 사용(해도 ok) 보다 DTO로 분리하면 보안정보 노출 위험에서 벗어남.
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = true)
    @ToString.Exclude
    private Provider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public void changePassword(String hashedPw) {
        this.password = hashedPw;
    }

    /*
    @OneToMany(mappedBy = "member")
    private List<Subscribe> subscribes = new ArrayList<>();

    //Soft-Delete 클래스를 만들어서 다른 곳에 적용 예정
    private boolean deleted = false; // SRP 위반: Soft-Delete상태 보관
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    public boolean isDeleted() {
        return deleted;
    }
*/
}

