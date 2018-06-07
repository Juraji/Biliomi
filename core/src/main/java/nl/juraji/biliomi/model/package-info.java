/**
 * Created by Juraji on 6-6-2018.
 * Biliomi
 *
 * This package info file also applies to model packages of all modules
 */
@TypeDefs({
        @TypeDef(
                name = "JodaDateTime",
                defaultForType = DateTime.class,
                typeClass = DateTimeISO8601Type.class
        )
})
package nl.juraji.biliomi.model;

import nl.juraji.biliomi.utility.types.hibernatetypes.DateTimeISO8601Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.joda.time.DateTime;