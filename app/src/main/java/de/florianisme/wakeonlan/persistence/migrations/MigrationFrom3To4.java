package de.florianisme.wakeonlan.persistence.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class MigrationFrom3To4 extends Migration {

    public MigrationFrom3To4() {
        super(3, 4);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE 'Devices' ADD COLUMN 'enable_remote_shutdown' INTEGER DEFAULT 0 NOT NULL"); // Booleans are stored as Integer
        database.execSQL("ALTER TABLE 'Devices' ADD COLUMN 'ssh_address' TEXT");
        database.execSQL("ALTER TABLE 'Devices' ADD COLUMN 'ssh_port' INTEGER");
        database.execSQL("ALTER TABLE 'Devices' ADD COLUMN 'ssh_user' TEXT");
        database.execSQL("ALTER TABLE 'Devices' ADD COLUMN 'ssh_password' TEXT");
        database.execSQL("ALTER TABLE 'Devices' ADD COLUMN 'ssh_command' TEXT");
    }

}
