package com.assadev.batch.item.chunk;

import com.assadev.batch.core.contant.DefineConstant;
import com.assadev.batch.core.contant.IndexMode;
import com.assadev.batch.core.contant.IndexType;
import com.assadev.batch.core.exception.ValidationException;
import com.assadev.batch.core.utils.DateUtils;
import com.assadev.batch.core.utils.DirectoryUtil;
import com.assadev.batch.core.utils.Done;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
public class ItemDynamicCrawlerListener extends StepExecutionListenerSupport {

    @Value("${crawler.item.base-path}")
    private String basePath;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        if(stepExecution.getStatus() == BatchStatus.STARTED) {
            log.info(" >>>>>>>>> 2. Item Dynamic Crawler Before <<<<<<<<< ");

            //TODO: Main 수집 쿼리 실행 전 작업
            // - 수집폴더 생성
            // - Pre-SQL
            try {
                // 수집 폴더 생성
                String crawlerPath = stepExecution.getJobExecution().getExecutionContext().getString("targetPath") +
                        File.separator + DefineConstant.FOLDER_CRAWLER + File.separator + DateUtils.getNowDateTime(DefineConstant.FOLDER_FORMAT);
                DirectoryUtil.create(crawlerPath);
                stepExecution.getJobExecution().getExecutionContext().putString("historyDateTime", DateUtils.getNowDateTime(DefineConstant.HISTORY_FORMAT));
                stepExecution.getJobExecution().getExecutionContext().putString("crawlerPath", crawlerPath);

                // Pre-SQL 실행

            }catch (Exception e){
                throw new ValidationException(ValidationException.ValidationErrCode.FOLDER_CHECK, "[Dynamic] [Crawler] crawler folder make error.");
            }
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if(stepExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info(" >>>>>>>>> 3. Item Dynamic Crawler After <<<<<<<<< ");

            //TODO: Main 수집 쿼리 종료 후 작업
            // - Post-SQL
            // - done 파일 생성
            try {
                // Post-SQL 실행

                // done 파일 생성
                ExecutionContext jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();
                Done crawlerDone = new Done(jobExecutionContext.getString("targetPath"), jobExecutionContext.getString("targetIndexMode"));
                crawlerDone.done(jobExecutionContext.getString("historyDateTime"));
            }catch (Exception e){
                throw new ValidationException(ValidationException.ValidationErrCode.DONE_CHECK, "[Dynamic] [Crawler] done file error.");
            }
        }
        return new ExitStatus("stepListener exit");
    }
}
