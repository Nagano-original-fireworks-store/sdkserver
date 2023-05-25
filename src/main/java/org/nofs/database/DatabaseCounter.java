package org.nofs.database;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity(value = "counters", useDiscriminator = false)
/* loaded from: org.nofs.jar:emu/grasscutter/database/DatabaseCounter.class */
public class DatabaseCounter {
    @Id
    private String id;
    private int count;

    public DatabaseCounter() {
    }

    public DatabaseCounter(String id) {
        this.id = id;
        this.count = 10000;
    }

    public int getNextId() {
        int id = this.count + 1;
        this.count = id;
        return id;
    }
}
