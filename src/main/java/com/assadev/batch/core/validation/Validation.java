package com.assadev.batch.core.validation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

import com.assadev.batch.core.exception.FileException;
import com.assadev.batch.core.exception.ValidationException;
import com.assadev.batch.core.utils.Lock;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Validation {

    public static final String DONE_CHECK_MESSAGE = "[DoneValidation] Normally crawling collected folder exist, [Indexer] with out doing [Crawler]";

    public static final String PID_CHECK_MESSAGE = "[PidValidation] The Crawler process is running with the PID";
    public static final String PID_NULL_MESSAGE = "[PidValidation] PidValidation get pid is null";
    public static final String PID_READ_MESSAGE = "[PidValidation] PidValidation read lock file failure";

    /** Dynamic Only **/
    public static final String HISTORY_CHECK_MESSAGE = "[HistoryValidation] Before 'Dynamic' work, do the 'Static' first";

    public static final String FOLDER_CHECK_MESSAGE = "[FolderValidation] Crawler & Index empty | Crawler is empty Index is not empty, Indexer cannot proceed";

    /**
     * Lock Check Validation
     * @param lock
     */
    public static boolean lockValidation(Lock lock) {
        if(lock.isLock()){
            log.warn("lock file exist next validation check");
            return true;
        }else {
            return false;
        }
    }

    /**
     * Pid Check Validation
     * @param path
     * @return
     * @throws FileException
     */
    public static boolean pidValidation(String path) throws FileException {
        Long pid;
        try{
            pid = getLockPid(path);
        }catch (IOException e){
            throw new FileException(FileException.FileErrCode.READ_LINE_FAIL, PID_CHECK_MESSAGE, e);
        }
        if(Objects.isNull(pid)){
            throw new ValidationException(ValidationException.ValidationErrCode.PID_CHECK, PID_NULL_MESSAGE);
        }else{
            log.info("[Crawler] The [{}] value of the existing lock file PID is {}", path, pid);
            return getProcessStatus(pid);
        }
    }

    public static void lockPidKill(String path) throws FileException {
        Long pid;
        try{
            pid = getLockPid(path);
        }catch (IOException e){
            throw new FileException(FileException.FileErrCode.READ_LINE_FAIL, PID_CHECK_MESSAGE, e);
        }
        if(Objects.isNull(pid)){
            throw new ValidationException(ValidationException.ValidationErrCode.PID_CHECK, PID_NULL_MESSAGE);
        }else{
            log.info("[Crawler] The [{}] value of the existing lock file PID is {}", path, pid);

            Optional<ProcessHandle> optionalProcessHandle = ProcessHandle.of(pid);
            optionalProcessHandle.ifPresent(processHandle -> processHandle.destroy());
        }
    }

    /**
     * Get Pid Number
     * @param lockPath
     * @return
     * @throws IOException
     */
    protected static Long getLockPid(String lockPath)
        throws IOException {

        String pid = Files.readString(Paths.get(lockPath), StandardCharsets.UTF_8);
        if(pid.matches("^[0-9]+$")){
            return Long.parseLong(pid);
        }else{
            return null;
        }
    }

    /**
     * Get lock file PID process status
     * @param pid
     * @return
     */
    protected static boolean getProcessStatus(long pid) {
        Optional<ProcessHandle> optionalProcessHandle = ProcessHandle.of(pid);
        return !optionalProcessHandle.isEmpty() && optionalProcessHandle.get().isAlive();
    }
}
