package clearancegood.services;

import clearancegood.entities.Good;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Component
public class GoodSearch {


    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Scheduled(initialDelay = 60 * 1000, fixedRate = 30 * 60 * 1000)
    public void reIndex() {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        try {
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            System.out.println("Error occured trying to build Hibernate Search indexes "
                    + e.toString());
        }
    }

    @Transactional
    public List<Good> search(String query, Integer start, Integer count) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Good.class).get();

        org.apache.lucene.search.Query query1 = qb
                .keyword()
                .onField("name").boostedTo(20.0f)
                .andField("brand")
                .andField("category").boostedTo(5.0f)
                .matching(query)
                .createQuery();

        // wrap Lucene query in a org.hibernate.Query
        javax.persistence.Query hibQuery =
                fullTextEntityManager.createFullTextQuery(query1, Good.class);
        hibQuery.setFirstResult(start);
        hibQuery.setMaxResults(count);

        //new branch
        //test

        // execute search
        List result = hibQuery.getResultList();

        return result;
    }
}
