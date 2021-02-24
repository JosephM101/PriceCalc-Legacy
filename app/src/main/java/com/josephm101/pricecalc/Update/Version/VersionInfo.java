package com.josephm101.pricecalc.Update.Version;

public class VersionInfo {

    public VersionInfo() {

    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public int getPatchVersion() {
        return patchVersion;
    }

    public VersionInfo(int majorVersion, int minorVersion, int patchVersion) {
        //this.majorVersion = majorVersion;
        //this.minorVersion = minorVersion;
        //this.patchVersion = patchVersion;
        setVersion(majorVersion, minorVersion, patchVersion); //We'll use this instead to maintain consistency.
    }

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

    public void setVersion(int majorVersion, int minorVersion, int patchVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.patchVersion = patchVersion;
        this.containsMajorVersion = (majorVersion > 0);
        this.containsMinorVersion = (minorVersion > 0);
        this.containsPatchVersion = (patchVersion > 0);
    }

    /**
     * Compare this VersionInfo with another to see which is newer. For example, if primary=1.3.2 and secondary=1.3.3, then the method would return VERSION_NEWER.
     *
     * @param toCompare The VersionInfo instance to compare this VersionInfo instance to (think of it as information received from an update server).
     * @return VersionComparison enum result (VERSION_SAME, VERSION_NEWER, VERSION_OLDER)
     */
    public VersionCompare.VersionComparison CompareTo(VersionInfo toCompare) {
        //Get all the version parts (major, minor, patch) from both VersionInfo instances, and put them into easily-accessible variables to make things easier.
        //First, the primary.
        int primaryMajor = this.getMajorVersion();
        int primaryMinor = this.getMinorVersion();
        int primaryPatch = this.getPatchVersion();
        //And now the secondary.
        int secondaryMajor = toCompare.getMajorVersion();
        int secondaryMinor = toCompare.getMinorVersion();
        int secondaryPatch = toCompare.getPatchVersion();

        //Now let's initialize the variable that we're going to return when we're done here.
        VersionCompare.VersionComparison versionComparisonResult = VersionCompare.VersionComparison.VERSION_SAME; //Default value

        //With our variables defined, let's get started.

        //Before we begin, let's check to see if both versions are exactly the same. If they are, we don't need to do anything.
        if (primaryMajor == secondaryMajor && primaryMinor == secondaryMinor && primaryPatch == secondaryPatch) {
            versionComparisonResult = VersionCompare.VersionComparison.VERSION_SAME; //The versions are the same.
        } else {
            //If we get here, then we have more work to do.
            if (primaryMajor != secondaryMajor) { //If the MAJOR versions are not equal, then one is (obviously) different from the other; we just have to find out if the secondary is newer or older.
                if (primaryMajor < secondaryMajor) { //Would be a newer version if this returned True.
                    versionComparisonResult = VersionCompare.VersionComparison.VERSION_NEWER; //It's definitely newer. Return this, and let's move on.
                } else { //It has to be older.
                    versionComparisonResult = VersionCompare.VersionComparison.VERSION_OLDER; //It's older. We can still move on because the major version is different.
                    /*
                      Explanation:
                      If the major version is different in any way, we can conclude that the VersionInfo instances aren't the same either.
                      We just have to see if the value is larger or smaller, and then we immediately know if the version is newer or older, without having to do any extra work.
                    */
                }
            } else { //But if it's not different...
                if (primaryMinor != secondaryMinor) { //...then let's check to see if the minor versions are.
                    //If we're here, they were different.
                    if (primaryMinor < secondaryMinor) { //Again, version would be newer if this returned True.
                        versionComparisonResult = VersionCompare.VersionComparison.VERSION_NEWER;
                    } else {
                        versionComparisonResult = VersionCompare.VersionComparison.VERSION_OLDER;
                    }
                } else {
                    if (primaryPatch != secondaryPatch) { //If we're here, it's because the "primary" and "secondary" major and minor versions are the same. This one has to be different, otherwise we wouldn't be here.
                        if (primaryPatch < secondaryPatch) { //Again, version would be newer if this returned True.
                            versionComparisonResult = VersionCompare.VersionComparison.VERSION_NEWER;
                        } else {
                            versionComparisonResult = VersionCompare.VersionComparison.VERSION_OLDER;
                        }
                    }
                }
            }
        }
        return versionComparisonResult; //Return the final comparison result. By default, it's set to "VERSION_SAME".
    }

    /**
     * See if this VersionInfo is newer than another.
     *
     * @param toCompare The VersionInfo instance to compare.
     * @return True if it's newer; otherwise False;
     */
    public Boolean IsNewerThan(VersionInfo toCompare) {
        //Get all the version parts (major, minor, patch) from both VersionInfo instances, and put them into easily-accessible variables to make things easier.
        //First, the primary.
        int primaryMajor = this.getMajorVersion();
        int primaryMinor = this.getMinorVersion();
        int primaryPatch = this.getPatchVersion();
        //And now the secondary.
        int secondaryMajor = toCompare.getMajorVersion();
        int secondaryMinor = toCompare.getMinorVersion();
        int secondaryPatch = toCompare.getPatchVersion();

        //Now let's initialize the variable that we're going to return when we're done here.
        Boolean result = false; //Default value

        //With our variables defined, let's get started.

        if (primaryMajor != secondaryMajor) { //If the MAJOR versions are not equal, then one is (obviously) different from the other; we just have to find out if the secondary is newer or older. Then we'll be done.
            if (primaryMajor < secondaryMajor) { //Would be a newer version if this returned True.
                result = true; //It's definitely newer.
            } else { //It has to be older.
                result = false; //It's older.
                    /*
                      Explanation:
                      If the major version is different in any way, we can conclude that the VersionInfo instances aren't the same either.
                      We just have to see if the value is larger or smaller, and then we immediately know if the version is newer or older, without having to do any extra work.
                    */
            }
        } else { //But if it's not different...
            if (primaryMinor != secondaryMinor) { //...then let's check to see if the minor versions are.
                //If we're here, they were different.
                if (primaryMinor < secondaryMinor) { //Again, version would be newer if this returned True.
                    result = true;
                } else {
                    result = false;
                }
            } else {
                if (primaryPatch != secondaryPatch) { //If we're here, it's because the "primary" and "secondary" major and minor versions are the same. This one has to be different, otherwise we wouldn't be here.
                    if (primaryPatch < secondaryPatch) { //Again, version would be newer if this returned True.
                        result = true;
                    } else {
                        result = false;
                    }
                }
            }
        }
        return result;
    }
    /**
     * See if this VersionInfo is newer than another.
     *
     * @param toCompare The VersionInfo instance to compare.
     * @return True if it's newer; otherwise False;
     */
    public Boolean IsOlderThan(VersionInfo toCompare) {
        int primaryMajor = this.getMajorVersion();
        int primaryMinor = this.getMinorVersion();
        int primaryPatch = this.getPatchVersion();
        //And now the secondary.
        int secondaryMajor = toCompare.getMajorVersion();
        int secondaryMinor = toCompare.getMinorVersion();
        int secondaryPatch = toCompare.getPatchVersion();

        Boolean result = false; //Default value

        if (primaryMajor != secondaryMajor) {
            if (primaryMajor < secondaryMajor) {
                result = false;
            } else {
                result = true;
            }
        } else {
            if (primaryMinor != secondaryMinor) {
                if (primaryMinor < secondaryMinor) {
                    result = false;
                } else {
                    result = true;
                }
            } else {
                if (primaryPatch != secondaryPatch) {
                    if (primaryPatch < secondaryPatch) {
                        result = false;
                    } else {
                        result = true;
                    }
                }
            }
        }
        return result;
    }
}