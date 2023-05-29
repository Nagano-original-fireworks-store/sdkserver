package org.nofs.database;

import com.mongodb.DBCollection;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;
import dev.morphia.query.experimental.filters.Filters;
import org.nofs.Grasscutter;
import org.nofs.config.Configuration;
import org.nofs.game.Account;

/* loaded from: sdkserver.jar:emu/grasscutter/database/DatabaseManager.class */
public final class DatabaseManager {
    private static Datastore dispatchDatastore;

    public static Datastore getAccountDatastore() {
        return dispatchDatastore;
    }

    public static void initialize() {
        MapperOptions mapperOptions = MapperOptions.builder().storeEmpties(true).storeNulls(false).build();
        MongoClient dispatchMongoClient = MongoClients.create(Configuration.DATABASE.server.connectionUri);
        dispatchDatastore = Morphia.createDatastore(dispatchMongoClient, Configuration.DATABASE.server.collection, mapperOptions);
        dispatchDatastore.getMapper().map(DatabaseCounter.class, Account.class);
        ensureIndexes(dispatchDatastore);
    }

    private static void ensureIndexes(Datastore datastore) {
        try {
            datastore.ensureIndexes();
        } catch (MongoCommandException e) {
            Grasscutter.getLogger().info("Mongo index error: ", (Throwable) e);
            if (e.getCode() == 85) {
                MongoIterable<String> collections = datastore.getDatabase().listCollectionNames();
                MongoCursor<String> it2 = collections.iterator();
                while (it2.hasNext()) {
                    String name = it2.next();
                    datastore.getDatabase().getCollection(name).dropIndexes();
                }
                datastore.ensureIndexes();
            }
        }
    }

    public static synchronized int getNextId(Class<?> c) {
        DatabaseCounter counter = (DatabaseCounter) getAccountDatastore().find(DatabaseCounter.class).filter(Filters.eq(DBCollection.ID_FIELD_NAME, c.getSimpleName())).first();
        if (counter == null) {
            counter = new DatabaseCounter(c.getSimpleName());
        }
        try {
            int nextId = counter.getNextId();
            getAccountDatastore().save(counter);
            return nextId;
        } catch (Throwable th) {
            getAccountDatastore().save(counter);
            throw th;
        }
    }

    public static synchronized int getNextId(Object o) {
        return getNextId(o.getClass());
    }
}
