package com.mcssoft.racemeetings2.interfaces;

import java.util.List;

/**
 * Used to provide an interface between the async task of ProcessData and the MainActivity.
 */
public interface IResult {
    /**
     * XML downloadResult results as a string
     * @param output  Where to send the output of the operation.
     * @param results Results of the operation.
     */
    void result(String output, List results);
}
