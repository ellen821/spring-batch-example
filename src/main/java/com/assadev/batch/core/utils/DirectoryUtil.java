package com.assadev.batch.core.utils;

import com.assadev.batch.core.exception.DirectoryException;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryUtil {

    /**
     * 디렉토리 생성 - 상위 디렉토리가 존재하지 않으면 상위 디렉토리도 생성
     *
     * @param path
     * @throws IOException
     */
    public static void create(String path) throws DirectoryException {
        try {
            if(!isExist(path)) {
                String[] pathSplit = path.split("/");

                StringBuilder tmpPath = new StringBuilder();
                tmpPath.append("/");
                for (String tmpPathSplit : pathSplit) {
                    if (!tmpPathSplit.isEmpty()) {
                        tmpPath.append(tmpPathSplit);
                        if (!Files.exists(Paths.get(tmpPath.toString()))) {
                            Files.createDirectory(Paths.get(tmpPath.toString()));
                        }
                        tmpPath.append("/");
                    }
                }
            }
        }catch (Exception e){
            throw new DirectoryException(DirectoryException.DirectoryErrCode.MAKE_FAIL, "Directory Make failure!!", e);
        }
    }

    /**
     * 대상 디렉토리가 존재하는지 않하는지 체크
     *
     * @param path
     * @throws IOException
     */
    public static boolean isExist(String path) {
        if (new File(path).exists()) {
            return true;
        }
        return false;
    }

    /**
     * 디렉토리 삭제 - 하위 디렉토리가 있으면 하위 디렉토리까지 삭제
     *
     * @param path
     * @throws IOException
     */
    public static void delete(String path) throws IOException {
        if( !isExist(path) ){
            return;
        }

        File file = new File(path);
        Files.walkFileTree(Paths.get(file.getPath()), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 디렉토리 삭제 - 파라미터로 전달받은 이름의 파일명이 없으면 삭제
     * @throws IOException
     */
    public static List<String> checkAfterDelete(String folderPath, String checkName) throws IOException {
        Path path = Paths.get(folderPath);

        List<String> deleteList = Files.list(path).map(Objects::toString).collect(Collectors.toList());
        List<String> doneList = new ArrayList<>();
        if(Files.isDirectory(path)){
            for(String directory : deleteList){
                if(new File(directory).isDirectory()){
                    for(String fileName : Files.list(Paths.get(directory)).map(Object::toString).collect(Collectors.toList())){
                        if(fileName.endsWith(checkName)){
                            doneList.add(directory);
                            break;
                        }
                    }
                }
            }
        }

        /* crawler.done이 있는 폴더 제외 */
        deleteList.removeAll(doneList);
        /* 대상 폴더 삭제 */
        for(String s : deleteList){
            delete(s);
        }

        return deleteList;
    }

    /**
     * 백업디렉토리 삭제 - remainSize 수만큼 제외하고 삭제
     *
     * @param path
     * @param remainSize
     * @throws IOException
     */
    public static void deleteBackup(String path, int remainSize) throws IOException {
        if (Files.isDirectory(Paths.get(path))) {
            Stream<Path> list = Files.list(Paths.get(path));
            List<File> fileList = list.sorted(Comparator.reverseOrder()).map(Path::toFile)
                .collect(Collectors.toList());
            for (File file : fileList) {
                if (remainSize == 0) {
                    delete(file.getPath());
                } else {
                    remainSize--;
                }
            }
        }
    }


    /**
     * 디렉토리 이동 - 수집완료 파일 있을 경우만 이동, 이동경로에 디렉토리 없으면 생성
     *
     * @param beforePath
     * @param afterPath
     * @return
     * @throws IOException
     */
    public static boolean move(String beforePath, String afterPath) throws IOException, DirectoryException {
        // done file check
        Stream<Path> list = Files.list(Paths.get(beforePath));
//        List<Path> doneFile = list.filter(s -> s.toString().endsWith(DefineValue.CRAWLER_DONE_FILE))
        List<Path> doneFile = list.filter(s -> s.toString().endsWith("crawler.done"))
            .collect(Collectors.toList());

        if (!doneFile.isEmpty()) {
            create(afterPath);
            Files.move(Paths.get(beforePath), Paths.get(afterPath),
                StandardCopyOption.REPLACE_EXISTING);
            return true;
        }

        return false;
    }

    /**
     * 디렉토리 목록 구하기 - 하위 디렉토리 리스트로 반환
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static List<String> getList(String path) throws IOException {
        List<String> resultList = new ArrayList<>();

        if( isExist(path) ) {
            List<Path> list = Files.list(Paths.get(path)).collect(Collectors.toList());
            for (Path tmp : list) {
                if (Files.isDirectory(tmp)) {
                    resultList.add(tmp.toString());
                }
            }
        }
        return resultList;
    }

    /**
     * 디렉토리 목록 구하기 - 하위 디렉토리 리스트로 반환
     *                      - getIndexList - crawler.done 파일이 없으면 제외
     *
     * @param dPath
     * @return
     * @throws IOException
     */
    public static List<String> getIndexList(String dPath) throws IOException {
        List<String> resultList = new ArrayList<>();
        for(String path : Files.list(Paths.get(dPath)).map(Objects::toString).collect(Collectors.toList())){
            if(new File(path).isDirectory()){
                for(String fileName : Files.list(Paths.get(path)).map(Object::toString).collect(Collectors.toList())){
                    if(fileName.endsWith("crawler.done")){
                        resultList.add(path);
                        break;
                    }
                }
            }
        }
        return resultList;
    }
}
