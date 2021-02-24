package com.josephm101.pricecalc.UpdateHandler.API;

import java.util.Date;

/**
 * ReleaseInfo class (contains GitHub repository information retrieved from class instance GitHub through method GetData();
 * GitHub.GetData(): retrieves a JSON string from GitHub's API containing info on the latest package release in the repository)
 */
public class ReleaseInfo {
    private String releaseVersion;
    private String releaseName;
    private String downloadUrl;
    private String releaseNotes;
    private String uploadDate_raw;
    private Date uploadDate;
    private String uploadDateString;

    /**
     * Instantiates a new ReleaseInfo class with predefined info
     *
     * @param releaseVersion   the release version
     * @param releaseName      the release name
     * @param downloadUrl      the download url
     * @param releaseNotes     the release notes
     * @param uploadDate       the upload date
     * @param uploadDateString the upload date string
     */
    @Deprecated
    public ReleaseInfo(String releaseVersion, String releaseName, String downloadUrl, String releaseNotes, Date uploadDate, String uploadDateString) {
        this.releaseVersion = releaseVersion;
        this.releaseName = releaseName;
        this.downloadUrl = downloadUrl;
        this.releaseNotes = releaseNotes;
        this.uploadDate = uploadDate;
        this.uploadDateString = uploadDateString;
    }

    /**
     * Instantiates a new ReleaseInfo class
     */
    public ReleaseInfo() {

    }

    /**
     * Gets release version.
     *
     * @return the release version
     */
    public String getReleaseVersion() {
        return releaseVersion;
    }

    /**
     * Sets release version.
     *
     * @param releaseVersion the release version
     */
    public void setReleaseVersion(String releaseVersion) {
        this.releaseVersion = releaseVersion;
    }

    /**
     * Gets release name.
     *
     * @return the release name
     */
    public String getReleaseName() {
        return releaseName;
    }

    /**
     * Sets release name.
     *
     * @param releaseName the release name
     */
    public void setReleaseName(String releaseName) {
        this.releaseName = releaseName;
    }

    /**
     * Gets download url.
     *
     * @return the download url
     */
    public String getDownloadUrl() {
        return downloadUrl;
    }

    /**
     * Sets download url.
     *
     * @param downloadUrl the download url
     */
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    /**
     * Gets release notes.
     *
     * @return the release notes
     */
    public String getReleaseNotes() {
        return releaseNotes;
    }

    /**
     * Sets release notes.
     *
     * @param releaseNotes the release notes
     */
    public void setReleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    /**
     * Gets the timestamp of when the release was pushed to GitHub.
     *
     * @return the timestamp (Date object)
     */
    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    /**
     * Gets upload date string.
     *
     * @return timestamp UTC string (converted to default time zone)
     */
    public String getUploadDateString() {
        return uploadDateString;
    }

    public void setUploadDateString(String uploadDateString) {
        this.uploadDateString = uploadDateString;
    }

    /**
     * Gets the raw UTC upload date string.
     *
     * @return raw UTC-format string
     */
    public String getUploadDate_raw() {
        return uploadDate_raw;
    }

    public void setUploadDate_raw(String uploadDate_raw) {
        this.uploadDate_raw = uploadDate_raw;
    }
}