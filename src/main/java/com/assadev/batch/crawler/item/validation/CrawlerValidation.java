package com.assadev.batch.crawler.item.validation;

import com.assadev.batch.core.contant.DefineConstant;
import com.assadev.batch.core.exception.ValidationException;
import com.assadev.batch.core.utils.DirectoryUtils;
import com.assadev.batch.core.validation.Validation;

import java.io.File;
import java.util.List;

public class CrawlerValidation extends Validation {

    public static void failDirectoryValidation(String path){
        try{
            List<String> deleteDirectoryList = DirectoryUtils.getList(path + File.separator + DefineConstant.FOLDER_CRAWLER);
            for(String dir : deleteDirectoryList){
                DirectoryUtils.delete(dir);
            }
        }catch (Exception e){
            throw new ValidationException(ValidationException.ValidationErrCode.CRAWLER_FOLDER_CHECK, "[CRAWLER] Not exist folder delete failure!!");
        }
    }
}
