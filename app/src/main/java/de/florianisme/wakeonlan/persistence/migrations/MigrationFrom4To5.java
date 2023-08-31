package de.florianisme.wakeonlan.persistence.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class MigrationFrom4To5 extends Migration {

    public MigrationFrom4To5() {
        super(4, 5);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        // Do nothing
    }

}
