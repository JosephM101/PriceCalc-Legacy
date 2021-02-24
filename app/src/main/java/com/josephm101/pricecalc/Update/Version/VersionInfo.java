package com.josephm101.pricecalc.Update.Version;

public class VersionInfo {
    private int majorVersion, minorVersion, patchVersion; //"major.minor.patch" (Ex. X.X.X or 1.5.3)
    String versionString, modifiedVersionString;
    Boolean containsMajorVersion;
    Boolean containsMinorVersion;
    Boolean containsPatchVersion;

    public Boolean getContainsMajorVersion() {
        return containsMajorVersion;
    }

    public Boolean getContainsMinorVersion() {
        return containsMinorVersion;
    }

    public Boolean getContainsPatchVersion() {
        return containsPatchVersion;
    }

    public String getModifiedVersionString() {
        return modifiedVersionString;
    }

    public String getOriginalVersionString() {
        return versionString;
    }

    String[] charactersToFilter = {
            "v",
            "V"
    };

    public void setVersion(int major, int minor, int patch) {
        this.majorVersion = major;
        this.minorVersion = minor;
        this.patchVersion = patch;
        containsMajorVersion = (major > 0);
        containsMinorVersion = (minor > 0);
        containsPatchVersion = (patch > 0);
    }
}

