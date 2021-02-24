package com.josephm101.pricecalc.API;

import java.util.Date;

public class ReleaseInfo {
    private String releaseVersion;
    private String releaseName;
    private String downloadUrl;
    private String releaseNotes;
    private String uploadDate_raw;
    private Date uploadDate;
    private String uploadDateString;

    public ReleaseInfo(String releaseVersion, String releaseName, String downloadUrl, String releaseNotes, Date uploadDate, String uploadDateString) {
        this.releaseVersion = releaseVersion;
        this.releaseName = releaseName;
        this.downloadUrl = downloadUrl;
        this.releaseNotes = releaseNotes;
        this.uploadDate = uploadDate;
        this.uploadDateString = uploadDateString;
    }

    public ReleaseInfo() {

    }

    public String getReleaseVersion() {
        return releaseVersion;
    }

    public void setReleaseVersion(String releaseVersion) {
        this.releaseVersion = releaseVersion;
    }

    public String getReleaseName() {
        return releaseName;
    }

    public void setReleaseName(String releaseName) {
        this.releaseName = releaseName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getUploadDateString() {
        return uploadDateString;
    }

    public void setUploadDateString(String uploadDateString) {
        this.uploadDateString = uploadDateString;
    }

    public String getUploadDate_raw() {
        return uploadDate_raw;
    }

    public void setUploadDate_raw(String uploadDate_raw) {
        this.uploadDate_raw = uploadDate_raw;
    }
}