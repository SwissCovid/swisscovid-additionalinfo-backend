package org.dpppt.additionalinfo.backend.ws.statistics;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import java.time.LocalDateTime;

/**
 * This class is used to represent multiple splunk result variants
 */
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

	/**
	 * queryCovidCodesEnteredAfterXDaysOnsetOfSymptoms result
	 */
	@JsonProperty(value = "00_days")
	private Integer afterZeroDays;
	@JsonProperty(value = "01_days")
	private Integer afterOneDays;
	@JsonProperty(value = "02_days")
	private Integer afterTwoDays;
	@JsonProperty(value = "Total")
	private Integer total;

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

    public int getAfterZeroDays() {
        return (afterZeroDays != null) ? afterZeroDays : 0;
    }

    public void setAfterZeroDays(String afterZeroDays) {
        this.afterZeroDays = Integer.parseInt(afterZeroDays);
    }

    public void setAfterZeroDays(Integer afterZeroDays) {
        this.afterZeroDays = afterZeroDays;
    }

    public int getAfterOneDays() {
        return (afterOneDays != null) ? afterOneDays : 0;
    }

    public void setAfterOneDays(String afterOneDays) {
        this.afterOneDays = Integer.parseInt(afterOneDays);
    }

    public void setAfterOneDays(Integer afterOneDays) {
        this.afterOneDays = afterOneDays;
    }

    public int getAfterTwoDays() {
        return (afterTwoDays != null) ? afterTwoDays : 0;
    }

    public void setAfterTwoDays(String afterTwoDays) {
        this.afterTwoDays = Integer.parseInt(afterTwoDays);
    }

    public void setAfterTwoDays(Integer afterTwoDays) {
        this.afterTwoDays = afterTwoDays;
    }

    public int getTotal() {
        return (total != null) ? total : 0;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

	@Override
	public String toString() {
		return "SplunkResult{" +
				"time=" + time +
				", activeApps=" + activeApps +
				", usedAuthorizationCodesCount=" + usedAuthorizationCodesCount +
				", positiveTestCount=" + positiveTestCount +
				", afterZeroDays=" + afterZeroDays +
				", afterOneDays=" + afterOneDays +
				", afterTwoDays=" + afterTwoDays +
				", total=" + total +
				'}';
	}
}
