package com.assadev.batch.core.contant;

public class DefineConstant {

    public static final String LOCK_FILE_EXTENSION = ".lock";
    public static final String DONE_FILE_EXTENSION = ".done";
    public static final String ERROR_FILE_EXTENSION = ".error";
    public static final String HISTORY_FILE = "history";

    public static final String RESTORE_FILE = "restore_dynamic_data";
    public static final String FILE_EXTENSION = ".txt";

    public static final String HISTORY_FORMAT = "yyyy-MM-dd_HH:mm:ss";
    public static final String FOLDER_FORMAT = "yyyyMMdd-HHmmss";

    public static final String FOLDER_CRAWLER = "crawler";
    public static final String FOLDER_BACKUP = "backup";
    public static final String FOLDER_ERROR = "error";

    private DefineConstant() {
        throw new IllegalStateException("DefineConstant class");
    }

}
