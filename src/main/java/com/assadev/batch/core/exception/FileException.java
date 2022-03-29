package com.assadev.batch.core.exception;

import java.io.IOException;

public class FileException extends IOException {

    public enum FileErrCode {
        NOT_FOUND( 500 ),
        MAKE_FAIL( 501 ),
        WRITE_FAIL( 502 ),
        READ_FAIL( 503 ),
        READ_LINE_FAIL( 504 ),
        MOVE_FAIL( 505 ),
        CRAWLER_EMPTY( 506 ),
        UNKNOWN(0);
        public final int code;

        FileErrCode( int c )
        {
            code = c;
        }
    }

    public FileException(FileErrCode code, String msg) {
        super(msg);
        this.code = code.code;
    }

    public FileException(FileErrCode code, String msg, Throwable e) {
        super(msg, e);
        this.code = code.code;
    }

    int code=0;

    public int getExitCode() {
        return this.code;
    }
}
