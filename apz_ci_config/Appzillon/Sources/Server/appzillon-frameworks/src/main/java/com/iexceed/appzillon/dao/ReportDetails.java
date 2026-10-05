/**
 *
 */
package com.iexceed.appzillon.dao;


/**
 * @author user
 *
 */
public class ReportDetails {

    private String credRequired = "N";
    private String reportType = null;
    private String dataSource = null;

    public String getCredRequired() {
        return credRequired;
    }

    public void setCredRequired(String credRequired) {
        this.credRequired = credRequired;
    }


    public String getReportType() {
        return reportType;
    }


    public void setReportType(String reportType) {
        this.reportType = reportType;
    }


    public String getDataSource() {
        return dataSource;
    }


    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }


}
