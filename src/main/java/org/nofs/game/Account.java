package org.nofs.game;

import dev.morphia.annotations.Collation;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.PreLoad;
import org.nofs.config.Configuration;
import org.nofs.database.DatabaseHelper;
import org.nofs.utils.Crypto;
import org.nofs.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.bson.Document;
import org.eclipse.jetty.util.security.Constraint;

@Entity(value = "accounts", useDiscriminator = false)
/* loaded from: sdkserver.jar:emu/grasscutter/game/Account.class */
public class Account {
    @Id
    private String id;
    @Indexed(options = @IndexOptions(unique = true))
    @Collation(locale = "simple", caseLevel = true)
    private String username;
    private String password;
    private int reservedPlayerId;
    private String email;
    private String token;
    private String sessionKey;
    private final List<String> permissions = new ArrayList();
    private Locale locale = Configuration.LANGUAGE;
    private String banReason;
    private int banEndTime;
    private int banStartTime;
    private boolean isBanned;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getReservedPlayerUid() {
        return this.reservedPlayerId;
    }

    public void setReservedPlayerUid(int playerId) {
        this.reservedPlayerId = playerId;
    }

    public String getEmail() {
        if (this.email != null && !this.email.isEmpty()) {
            return this.email;
        }
        return "";
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSessionKey() {
        return this.sessionKey;
    }

    public String generateSessionKey() {
        this.sessionKey = Utils.bytesToHex(Crypto.createSessionKey(32));
        save();
        return this.sessionKey;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getBanReason() {
        return this.banReason;
    }

    public void setBanReason(String banReason) {
        this.banReason = banReason;
    }

    public int getBanEndTime() {
        return this.banEndTime;
    }

    public void setBanEndTime(int banEndTime) {
        this.banEndTime = banEndTime;
    }

    public int getBanStartTime() {
        return this.banStartTime;
    }

    public void setBanStartTime(int banStartTime) {
        this.banStartTime = banStartTime;
    }

    public boolean isBanned() {
        if (this.banEndTime > 0 && this.banEndTime < System.currentTimeMillis() / 1000) {
            this.isBanned = false;
            this.banEndTime = 0;
            this.banStartTime = 0;
            this.banReason = null;
            save();
        }
        return this.isBanned;
    }

    public void setBanned(boolean isBanned) {
        this.isBanned = isBanned;
    }

    public List<String> getPermissions() {
        return this.permissions;
    }

    public boolean addPermission(String permission) {
        if (this.permissions.contains(permission)) {
            return false;
        }
        this.permissions.add(permission);
        return true;
    }

    public boolean addPermission(String[] permission) {
        for (String s : permission) {
            if (this.permissions.contains(s)) {
                return false;
            }
            this.permissions.add(s);
        }
        return true;
    }

    public static boolean permissionMatchesWildcard(String wildcard, String[] permissionParts) {
        String[] wildcardParts = wildcard.split("\\.");
        if (permissionParts.length < wildcardParts.length) {
            return false;
        }
        for (int i = 0; i < wildcardParts.length; i++) {
            String str = wildcardParts[i];
            boolean z = true;
            switch (str.hashCode()) {
                case 42:
                    if (str.equals("*")) {
                        z = true;
                        break;
                    }
                    break;
                case 1344:
                    if (str.equals(Constraint.ANY_AUTH)) {
                        z = false;
                        break;
                    }
                    break;
            }
            if (!(z)) {
                return true;
            } else if (z) {
                if (i < permissionParts.length - 1) {
                } else {
                    return true;
                }
            } else {
                if (wildcardParts[i].equals(permissionParts[i])) {
                } else {
                    return false;
                }
            }
        }
        return wildcardParts.length == permissionParts.length;
    }

    public boolean hasPermission(String permission) {
        if (permission.isEmpty()) {
            return true;
        }
        if (this.permissions.contains("*") && this.permissions.size() == 1) {
            return true;
        }
        List<String> permissions = Stream.of((Object[]) new List[]{this.permissions, Arrays.asList(Configuration.ACCOUNT.defaultPermissions)}).flatMap((v0) -> {
            return v0.stream();
        }).distinct().toList();
        if (permissions.contains(permission)) {
            return true;
        }
        String[] permissionParts = permission.split("\\.");
        for (String p : permissions) {
            if (p.startsWith("-") && permissionMatchesWildcard(p.substring(1), permissionParts)) {
                return false;
            }
            if (permissionMatchesWildcard(p, permissionParts)) {
                return true;
            }
        }
        return permissions.contains("*");
    }

    public boolean removePermission(String permission) {
        return this.permissions.remove(permission);
    }

    public void clearPermission() {
        this.permissions.clear();
    }

    public String generateLoginToken() {
        this.token = Utils.bytesToHex(Crypto.createSessionKey(32));
        save();
        return this.token;
    }

    public void save() {
        DatabaseHelper.saveAccount(this);
    }

    @PreLoad
    public void onLoad(Document document) {
        if (!document.containsKey("permissions")) {
            addPermission("*");
        }
        if (!document.containsKey("locale")) {
            this.locale = Configuration.LANGUAGE;
        }
    }
}
