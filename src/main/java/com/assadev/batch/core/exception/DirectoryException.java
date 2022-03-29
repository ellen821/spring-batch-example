package com.assadev.batch.core.exception;

public class DirectoryException extends Exception {

    public enum DirectoryErrCode {
        EXIST( 500 ),
        MAKE_FAIL( 501 ),
        MOVE_FAIL( 502 ),
        DELETE_FAIL(503),
        GET_LIST(504),
        UNKNOWN(0);
        public final int code;

        DirectoryErrCode( int c )
        {
            code = c;
        }
    }

    public DirectoryException(DirectoryErrCode code, String msg) {
        super(msg);
        this.code = code.code;
    }

    public DirectoryException(DirectoryErrCode code, String msg, Throwable e) {
        super(msg, e);
        this.code = code.code;
    }

    int code=0;

    public int getExitCode() {
        return this.code;
    }

}
