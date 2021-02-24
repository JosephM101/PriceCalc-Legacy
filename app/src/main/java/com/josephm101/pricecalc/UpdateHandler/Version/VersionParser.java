package com.josephm101.pricecalc.UpdateHandler.Version;

import java.util.List;

public class VersionParser {
    String originalVersionString;
    String modifiedVersionString;

    String[] charactersToOmit = {
            "v",
            "V"
    };

    /**
     * Turn a version string (ex. V1.5.3) into a usable set of three numbers (major, minor & patch versions).
     *
     * @param versionString String to parse. It should look like this: "X.X.X" (Ex. 1.4.1 or 1.5). If not, it'll be converted, but it may not be converted properly.
     * @return VersionInfo instance
     */
    public static VersionInfo parse(String versionString) {
        VersionInfo versionInfo = new VersionInfo(); //Create an instance of the class to pass through when we're done.
        String formattedString = versionString.replaceAll("[^0-9.]", ""); //Remove every other character other than numbers 0-9 and '.', to properly format string for parsing.
        List<String> numbers = List.of(formattedString.split(".")); //Split the string by the dot separator, so that we can get each value.
        switch (numbers.size()) {
            case 0:
                versionInfo.setVersion(0, 0, 0);
                break;
            case 1:
                versionInfo.setVersion(Integer.parseInt(numbers.get(0)), 0, 0);
                break;
            case 2:
                versionInfo.setVersion(Integer.parseInt(numbers.get(0)), Integer.parseInt(numbers.get(1)), 0);
                break;
            case 3:
                versionInfo.setVersion(Integer.parseInt(numbers.get(0)), Integer.parseInt(numbers.get(1)), Integer.parseInt(numbers.get(2)));
                break;
        }
        return versionInfo;
    }

    /**
     * Returns a copy of the input String "toFilter" with the exception of the characters specified in String[] CharactersToFilter.
     *
     * @param toFilter the string to filter
     * @param CharactersToFilter the characters to remove from the string
     * @return the filtered string
     */
    @Deprecated
    public String filterString(String toFilter, String[] CharactersToFilter) {
        String filtered = toFilter;
        for (String str : CharactersToFilter) {
            filtered.replace(str, "");
        }
        return filtered;
    }

    /**
     * Returns a copy of the input String "toFilter" with every occurrence of the characters specified in String[] CharactersToFilter replaced by the String specified in "replaceWith".
     *
     * @param toFilter the string to filter
     * @param CharactersToFilter the characters to remove from the string
     * @param replaceWith the character or string to replace every occurrence with
     * @return the filtered string
     */
    @Deprecated
    public String filterString(String toFilter, String[] CharactersToFilter, String replaceWith) {
        String filtered = toFilter;
        for (String str : CharactersToFilter) {
            filtered.replace(str, replaceWith);
        }
        return filtered;
    }
}