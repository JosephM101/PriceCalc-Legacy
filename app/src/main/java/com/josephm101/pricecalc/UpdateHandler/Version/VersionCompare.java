package com.josephm101.pricecalc.UpdateHandler.Version;

public class VersionCompare {
    enum VersionComparison {
        VERSION_SAME,
        VERSION_NEWER,
        VERSION_OLDER
    }

    /**
     * Compare two VersionInfo instances together (compares primary to secondary). For example, if primary=1.3.2 and secondary=1.3.3, then the method would return VERSION_NEWER.
     *
     * @param primary   The primary VersionInfo instance (for example, the currently installed version of an app).
     * @param secondary The VersionInfo instance to compare the primary against (for example, something retrieved from an update server).
     * @return VersionComparison enum result (VERSION_SAME, VERSION_NEWER, VERSION_OLDER)
     */
    public static VersionComparison CompareVersions(VersionInfo primary, VersionInfo secondary) {
        //Get all the version parts (major, minor, patch) from both VersionInfo instances, and put them into easily-accessible variables to make things easier.
        //First, the primary.
        int primaryMajor = primary.getMajorVersion();
        int primaryMinor = primary.getMinorVersion();
        int primaryPatch = primary.getPatchVersion();
        //And now the secondary.
        int secondaryMajor = secondary.getMajorVersion();
        int secondaryMinor = secondary.getMinorVersion();
        int secondaryPatch = secondary.getPatchVersion();

        //Now let's initialize the variable that we're going to return when we're done here.
        VersionComparison versionComparisonResult = VersionComparison.VERSION_SAME; //Default value

        //With our variables defined, let's get started.

        //Before we begin, let's check to see if both versions are exactly the same. If they are, we don't need to do anything.
        if (primaryMajor == secondaryMajor && primaryMinor == secondaryMinor && primaryPatch == secondaryPatch) {
            versionComparisonResult = VersionComparison.VERSION_SAME; //The versions are the same.
        } else {
            //If we get here, then we have more work to do.
            if (primaryMajor != secondaryMajor) { //If the MAJOR versions are not equal, then one is (obviously) different from the other; we just have to find out if the secondary is newer or older.
                if (primaryMajor < secondaryMajor) { //Would be a newer version if this returned True.
                    versionComparisonResult = VersionComparison.VERSION_NEWER; //It's definitely newer. Return this, and let's move on.
                } else { //It has to be older.
                    versionComparisonResult = VersionComparison.VERSION_OLDER; //It's older. We can still move on because the major version is different.
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
                        versionComparisonResult = VersionComparison.VERSION_NEWER;
                    } else {
                        versionComparisonResult = VersionComparison.VERSION_OLDER;
                    }
                } else {
                    if (primaryPatch != secondaryPatch) { //If we're here, it's because the "primary" and "secondary" major and minor versions are the same. This one has to be different, otherwise we wouldn't be here.
                        if (primaryPatch < secondaryPatch) { //Again, version would be newer if this returned True.
                            versionComparisonResult = VersionComparison.VERSION_NEWER;
                        } else {
                            versionComparisonResult = VersionComparison.VERSION_OLDER;
                        }
                    }
                }
            }
        }
        return versionComparisonResult; //Return the final comparison result. By default, it's set to "VERSION_SAME".
    }
}