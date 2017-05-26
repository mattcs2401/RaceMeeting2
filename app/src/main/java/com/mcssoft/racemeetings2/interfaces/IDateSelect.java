package com.mcssoft.racemeetings2.interfaces;

/**
 * Used to provide an interface betweeen the DateSelectFragment and MeetingsActivity.
 */
public interface IDateSelect {
    /**
     * Date values (YYYY-MM-DD).
     * @param values [0] YYYY, [1] MM, [2] DD
     */
    void iDateValues(String[] values);
}