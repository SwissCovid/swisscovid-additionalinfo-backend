package org.dpppt.additionalinfo.backend.ws.statistics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SplunkResponse {
    private Boolean preview;
    private SplunkResult result;

    public Boolean getPreview() {
        return preview;
    }

    public void setPreview(Boolean preview) {
        this.preview = preview;
    }

    public SplunkResult getResult() {
        return result;
    }

    public void setResult(SplunkResult result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "SplunkResponse{" + "preview=" + preview + ", result=" + result + '}';
    }
}
