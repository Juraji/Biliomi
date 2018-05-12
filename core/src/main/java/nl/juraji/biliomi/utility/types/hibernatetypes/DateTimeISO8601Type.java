package nl.juraji.biliomi.utility.types.hibernatetypes;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import static org.hibernate.type.StandardBasicTypes.STRING;

/**
 * Created by Juraji on 11-4-2017.
 * biliomi
 * <p>
 * Persistence usertype for persisting Joda DateTime in ISO8601 format
 */
public final class DateTimeISO8601Type implements UserType, Serializable {
    public static final String TYPE = "nl.juraji.biliomi.utility.types.hibernatetypes.DateTimeISO8601Type";
    private static final int[] SQL_TYPES = new int[]{Types.VARCHAR};

    @Override
    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    @Override
    public Class returnedClass() {
        return DateTime.class;
    }

    @Override
    public boolean equals(Object o, Object o1) {
        if (o == o1) {
            return true;
        } else if (o != null && o1 != null) {
            DateTime dto = (DateTime) o;
            DateTime dto1 = (DateTime) o1;
            return dto.equals(dto1);
        }

        return false;
    }

    @Override
    public int hashCode(Object o) {
        return o.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] strings, SessionImplementor sessionImplementor, Object o) throws SQLException {
        Object safeGet = STRING.nullSafeGet(resultSet, strings, sessionImplementor, o);
        return (safeGet == null) ? null : new DateTime(safeGet.toString());
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object o, int i, SessionImplementor sessionImplementor) throws SQLException {
        if (o == null) {
            STRING.nullSafeSet(preparedStatement, null, i, sessionImplementor);
        } else {
            DateTime dt = (DateTime) o;
            STRING.nullSafeSet(preparedStatement, dt.toString(), i, sessionImplementor);
        }
    }

    @Override
    public Object deepCopy(Object o) {
        return o;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object o) {
        return (Serializable) o;
    }

    @Override
    public Object assemble(Serializable serializable, Object o) {
        return o;
    }

    @Override
    public Object replace(Object o, Object o1, Object o2) {
        return o;
    }
}
