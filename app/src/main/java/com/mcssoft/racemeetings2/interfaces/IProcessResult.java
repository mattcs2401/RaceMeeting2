package com.mcssoft.racemeetings2.interfaces;

/**
 * Used to provide an interface between the async task of ProcessData and the MainActivity.
 */
public interface IProcessResult {
    /**
     * XML downloadResult results as a string
     * @param table Results relate to this table.
     * @param results Results of the operation.
     */
    void processResult(String table, String results);
}
