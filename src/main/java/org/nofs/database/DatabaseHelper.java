package org.nofs.database;

import com.mongodb.DBCollection;
import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import org.nofs.game.Account;

/* loaded from: org.nofs.jar:emu/org.nofs/database/DatabaseHelper.class */
public final class DatabaseHelper {
    public static Account createAccount(String username) {
        return createAccountWithUid(username, 0);
    }

    public static Account createAccountWithUid(String username, int reservedUid) {
        if (checkIfAccountExists(username)) {
            return null;
        }
        if (reservedUid > 0 && (reservedUid == 99 || checkIfAccountExists(reservedUid))) {
            return null;
        }
        Account account = new Account();
        account.setUsername(username);
        account.setId(Integer.toString(DatabaseManager.getNextId(account)));
        if (reservedUid > 0) {
            account.setReservedPlayerUid(reservedUid);
        }
        saveAccount(account);
        return account;
    }

    @Deprecated
    public static Account createAccountWithPassword(String username, String password) {
        Account exists = getAccountByName(username);
        if (exists != null) {
            return null;
        }
        Account account = new Account();
        account.setId(Integer.toString(DatabaseManager.getNextId(account)));
        account.setUsername(username);
        account.setPassword(password);
        saveAccount(account);
        return account;
    }

    public static void saveAccount(Account account) {
        DatabaseManager.getAccountDatastore().save((Datastore) account);
    }

    public static Account getAccountByName(String username) {
        return (Account) DatabaseManager.getAccountDatastore().find(Account.class).filter(Filters.eq("username", username)).first();
    }

    public static Account getAccountByToken(String token) {
        if (token == null) {
            return null;
        }
        return (Account) DatabaseManager.getAccountDatastore().find(Account.class).filter(Filters.eq("token", token)).first();
    }

    public static Account getAccountBySessionKey(String sessionKey) {
        if (sessionKey == null) {
            return null;
        }
        return (Account) DatabaseManager.getAccountDatastore().find(Account.class).filter(Filters.eq("sessionKey", sessionKey)).first();
    }

    public static Account getAccountById(String uid) {
        return (Account) DatabaseManager.getAccountDatastore().find(Account.class).filter(Filters.eq(DBCollection.ID_FIELD_NAME, uid)).first();
    }

    public static Account getAccountByPlayerId(int playerId) {
        return (Account) DatabaseManager.getAccountDatastore().find(Account.class).filter(Filters.eq("reservedPlayerId", Integer.valueOf(playerId))).first();
    }

    public static boolean checkIfAccountExists(String name) {
        return DatabaseManager.getAccountDatastore().find(Account.class).filter(Filters.eq("username", name)).count() > 0;
    }

    public static boolean checkIfAccountExists(int reservedUid) {
        return DatabaseManager.getAccountDatastore().find(Account.class).filter(Filters.eq("reservedPlayerId", Integer.valueOf(reservedUid))).count() > 0;
    }
}
