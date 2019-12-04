package clearancegood.entities;

import lombok.*;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "kkey"})
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Field(store = Store.YES, index = Index.YES, analyze = Analyze.NO)
    private String name;   //Target.com

    @Field(store = Store.YES, index = Index.NO)
    private String website; //http://www.target.com

    @Field(store = Store.YES, index = Index.NO)
    private String logo;  // jasdfsa.jpg

    @Column(unique = true)
    private String kkey;  //TARGET
}
