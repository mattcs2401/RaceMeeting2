package com.mcssoft.racemeetings2.interfaces;

import java.util.List;

/**
 * Used to provide an interface between the async task of ProcessData and the MainActivity.
 */
public interface IMeetingResult {
    /**
     * XML downloadResult results as a string
     * @param results Results of the operation.
     */
    void meetingResult(List results);
}
