package org.bupt.persosnalfinance.dto;

public class ExportRequest {
    private String filename;        // 不含扩展名
    public ExportRequest() {}
    public ExportRequest(String filename) { this.filename = filename; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
}
