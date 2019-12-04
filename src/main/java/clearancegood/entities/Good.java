package clearancegood.entities;

import lombok.*;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Indexed
public class Good {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Field(store = Store.YES, analyze = Analyze.YES, index = Index.YES)
    private String name;

    @Field(store = Store.YES, analyze = Analyze.YES, index = Index.YES)
    private String brand;

    @Field(store = Store.YES, index = Index.NO)
    private Double price;

    @Field(store = Store.YES, index = Index.NO)
    private Double regPrice;
    private String providerId;

    @Field(store = Store.YES, analyze = Analyze.YES, index = Index.YES)
    private String category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id")
    @IndexedEmbedded
    private Seller seller;

    @Column(length = 10000)
    @Field(store = Store.NO, index = Index.NO)
    private String description;

    @Column(unique=true)
    @Field(store = Store.YES, index = Index.NO)
    private String picture;

    @Field(store = Store.YES, index = Index.NO)
    private String link;

}
