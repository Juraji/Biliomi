package nl.juraji.biliomi.model.core.security.tokens;

import nl.juraji.biliomi.utility.jpa.JpaDao;
import org.hibernate.criterion.Restrictions;

import javax.enterprise.inject.Default;
import java.util.List;

/**
 * Created by Juraji on 6-9-2017.
 * Biliomi v3
 */
@Default
public class AuthTokenDao extends JpaDao<AuthToken> {
    public AuthTokenDao() {
        super(AuthToken.class);
    }

    /**
     * Get a token
     *
     * @param tokenGroup The group of the token
     * @param name       The name of the token
     * @return The appropriate AuthToken entity or a new AuthToken entity with group and name set, but token empty
     */
    public AuthToken get(TokenGroup tokenGroup, String name) {
        AuthToken tokenEnt = criteria()
                .add(Restrictions.and(
                        Restrictions.eq("tokenGroup", tokenGroup),
                        Restrictions.eq("name", name)
                ))
                .getResult();

        if (tokenEnt == null) {
            tokenEnt = new AuthToken();
            tokenEnt.setTokenGroup(tokenGroup);
            tokenEnt.setName(name);
        }

        return tokenEnt;
    }

    public List<AuthToken> getAllInGroup(TokenGroup tokenGroup) {
        return criteria()
                .add(Restrictions.eq("tokenGroup", tokenGroup))
                .getList();
    }

    public boolean isTokenPresent(TokenGroup tokenGroup, String name) {
        return criteria()
                .add(Restrictions.and(
                        Restrictions.eq("tokenGroup", tokenGroup),
                        Restrictions.eq("name", name)
                ))
                .getCount() > 0;
    }
}
