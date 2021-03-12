package org.dpppt.additionalinfo.backend.ws.config;

import java.time.LocalDate;
import java.util.TimeZone;
import org.dpppt.additionalinfo.backend.ws.controller.DppptAdditionalInfoController;
import org.dpppt.additionalinfo.backend.ws.data.HistoryDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

@Configuration
@EnableScheduling
public class SchedulerConfig implements SchedulingConfigurer {

    @Autowired DppptAdditionalInfoController dppptAdditionalInfoController;
    @Autowired HistoryDataService historyDataService;
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // refresh the statistics every full hour.
        taskRegistrar.addCronTask(
                new CronTask(
                        new Runnable() {

                            @Override
                            public void run() {
                                dppptAdditionalInfoController.reloadStats();
                            }
                        },
                        new CronTrigger("0 0 * * * ?", TimeZone.getTimeZone("Europe/Zurich"))));
        
        // remove old data every night
        taskRegistrar.addCronTask(
                new CronTask(
                        new Runnable() {

                            @Override
                            public void run() {
                            	LocalDate day = LocalDate.now().minusDays(30);
                            	logger.info("Remove history data before: " + day.toString());
                            	historyDataService.removeBefore(day);
                            }
                        },
                        new CronTrigger("0 0 4 * * ?", TimeZone.getTimeZone("Europe/Zurich"))));
    }
}
