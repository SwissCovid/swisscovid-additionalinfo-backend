package org.dpppt.additionalinfo.backend.ws.statistics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SplunkResponse {

	private SplunkResult result;

	public SplunkResult getResult() {
		return result;
	}

	public void setResult(SplunkResult result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "SplunkResponse [result=" + result + "]";
	}

}
