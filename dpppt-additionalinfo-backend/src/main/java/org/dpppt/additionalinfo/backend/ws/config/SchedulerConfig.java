package org.dpppt.additionalinfo.backend.ws.config;

import java.util.TimeZone;

import org.dpppt.additionalinfo.backend.ws.controller.DppptAdditionalInfoController;
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

	@Autowired
	DppptAdditionalInfoController dppptAdditionalInfoController;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		// refresh the statistics every full hour.
		taskRegistrar.addCronTask(new CronTask(new Runnable() {

			@Override
			public void run() {
				dppptAdditionalInfoController.reloadStats();
			}

		}, new CronTrigger("0 0 * * * ?", TimeZone.getTimeZone("Europe/Zurich"))));
	}

}
