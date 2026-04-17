package kr.ac.hansung.cse.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
@ToString(exclude = {"description", "category", "productDetail", "tags"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 상품명
     * nullable = false → DB에서 NOT NULL 제약 조건
     * length = 100    → VARCHAR(100)
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 카테고리 (예: 전자제품, 식품, 의류 등) */
    // ── ① @ManyToOne: 카테고리와 연관관계 (Owning Side) ─────────────────
    @ManyToOne(fetch = FetchType.LAZY)           // 성능을 위해 반드시 LAZY
    @JoinColumn(name = "category_id")            // FK 컬럼명: product.category_id
    private Category category;                   // 이 필드가 FK를 직접 관리


    /** 가격 */
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 상품 설명 (긴 텍스트)
     * @Lob: Large Object를 의미하며 TEXT, CLOB 등으로 매핑됩니다.
     */
    @Lob
    @Column(name = "description")
    private String description;

    // ── @OneToOne: 상세 정보 (Owning Side, FK: product.product_detail_id) ─
    @OneToOne(cascade = CascadeType.ALL,           // 상세정보 함께 저장/삭제
            fetch = FetchType.LAZY,              // EAGER 기본값 → LAZY 오버라이드
            orphanRemoval = true)                // Product에서 연결 해제 시 자동 삭제
    @JoinColumn(name = "product_detail_id")        // FK: product 테이블에 위치
    private ProductDetail productDetail;


    // ── @ManyToMany: 태그 (Owning Side) ──────────────────────────────────
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}) // REMOVE 절대 금지!
    @JoinTable(
            name = "product_tag",                         // 조인 테이블명
            joinColumns = @JoinColumn(name = "product_id"),// 이 엔티티 FK
            inverseJoinColumns = @JoinColumn(name = "tag_id") // 상대 엔티티 FK
    )
    private List<Tag> tags = new ArrayList<>();

    // 편의 메서드
    public void addTag(Tag tag) { tags.add(tag); }


    /**
     * 새 Product를 생성할 때 사용하는 생성자
     * id는 DB가 자동 생성하므로 포함하지 않습니다.
     */
    public Product(String name, Category category, BigDecimal price, String description) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
    }
}
