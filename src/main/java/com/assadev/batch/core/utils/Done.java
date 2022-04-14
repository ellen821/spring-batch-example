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

public class Done {
    private Path filePath;
    private String targetPath;

    public Done(String targetPath, String indexMode) {
        this.targetPath = targetPath;
        this.filePath = Paths.get(targetPath, indexMode + DefineConstant.DONE_FILE_EXTENSION);
    }

    public boolean isDone() {
        return Files.exists(this.filePath);
    }

    public String getFilePath() {
        return filePath.toString();
    }

    public boolean done(String folderPatternDateTime) throws DirectoryException, IOException {

        DirectoryUtils.create(this.targetPath);

        Set<StandardOpenOption> options = new HashSet<>();
        options.add(StandardOpenOption.CREATE_NEW);
        options.add(StandardOpenOption.WRITE);

        FileChannel channel = FileChannel.open(this.filePath, options);
        channel.write(ByteBuffer.wrap(folderPatternDateTime.getBytes(StandardCharsets.UTF_8)));
        channel.close();
        return true;
    }

    public String getFolderPatternDateTime(){
        try{
            return Files.readString(this.filePath, StandardCharsets.UTF_8);
        }catch (IOException e){
            return "";
        }
    }
}
