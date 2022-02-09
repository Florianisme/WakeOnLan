package de.florianisme.wakeonlan.ui.home.backup;

public enum RequestCode {
    CREATE_EXPORT_FILE(1), READ_IMPORT_FILE(2);

    private final int requestCode;

    RequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getRequestCode() {
        return requestCode;
    }
}
