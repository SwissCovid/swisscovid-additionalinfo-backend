package org.dpppt.additionalinfo.backend.ws.statistics;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SplunkResult {

	@JsonProperty(value = "_time")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS z")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)  
	private LocalDateTime time;

	@JsonProperty(value = "active Apps")
	private Integer activeApps;

	@JsonProperty(value = "usedAuthorizationCodeCount")
	private Integer usedAuthorizationCodesCount;

	@JsonProperty(value = "positiveTestCount")
	private Integer positiveTestCount;

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public Integer getActiveApps() {
		return activeApps;
	}

	public void setActiveApps(Integer activeApps) {
		this.activeApps = activeApps;
	}

	public Integer getUsedAuthorizationCodesCount() {
		return usedAuthorizationCodesCount;
	}

	public void setUsedAuthorizationCodesCount(Integer usedAuthorizationCodesCount) {
		this.usedAuthorizationCodesCount = usedAuthorizationCodesCount;
	}

	public Integer getPositiveTestCount() {
		return positiveTestCount;
	}

	public void setPositiveTestCount(Integer positiveTestCount) {
		this.positiveTestCount = positiveTestCount;
	}

	@Override
	public String toString() {
		return "SplunkResult [time=" + time + ", activeApps=" + activeApps + ", usedAuthorizationCodesCount="
				+ usedAuthorizationCodesCount + ", positiveTestCount=" + positiveTestCount + "]";
	}

}
