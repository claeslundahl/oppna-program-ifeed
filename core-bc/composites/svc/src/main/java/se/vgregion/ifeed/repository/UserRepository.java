package se.vgregion.ifeed.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import se.vgregion.ifeed.types.CachedUser;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository
@Transactional
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public CachedUser findUser(String username) {
        Query query = entityManager.createQuery("select u from CachedUser u where u.id = :id");
        query.setParameter("id", username);

        try {
            return (CachedUser) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public CachedUser merge(CachedUser cachedUser) {
        return entityManager.merge(cachedUser);
    }
}
