package com.assadev.batch.core.utils;

import com.assadev.batch.core.contant.DefineConstant;
import com.assadev.batch.core.exception.DirectoryException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

public class Lock {

    private Path filePath;
    private String targetPath;

    public Lock(String targetPath, String indexMode) {
        this.targetPath = targetPath;
        this.filePath = Paths.get(targetPath, indexMode + DefineConstant.LOCK_FILE_EXTENSION);
    }

    public boolean isLock() {
        return Files.exists(this.filePath);
    }

    public String getFilePath() {
        return filePath.toString();
    }

    public boolean lock() throws DirectoryException, IOException {

        DirectoryUtil.create(this.targetPath);

        Set<StandardOpenOption> options = new HashSet<>();
        options.add(StandardOpenOption.CREATE_NEW);
        options.add(StandardOpenOption.WRITE);

        FileChannel channel = FileChannel.open(this.filePath, options);
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        String pid = processName.split("@")[0];

        channel.write(ByteBuffer.wrap(pid.getBytes(StandardCharsets.UTF_8)));
        channel.close();
        return true;
    }

    public boolean unLock() throws IOException {
        return Files.deleteIfExists(this.filePath);
    }

}
