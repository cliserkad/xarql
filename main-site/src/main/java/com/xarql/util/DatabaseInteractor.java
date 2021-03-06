package com.xarql.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Used for classes that interact with the database.
 *
 * @author Bryan Johnson
 * @param <ResponseDataType> The Java class that represents the database rows.
 */
public abstract class DatabaseInteractor<ResponseDataType>
{
    /**
     * The current command selected to be executed.
     */
    private String command = null;
    /**
     * The index associated with the command. Will most likely be 0 and remain 0
     * during operation of child classes.
     */
    private int    index;

    /**
     * Sets command variable to the provided command and the index to 0.
     *
     * @param command A String containing an SQL query
     */
    public DatabaseInteractor(String command)
    {
        index = 0;
        this.command = command;
    }

    /**
     * Sets the command variable to null and the index to 0.
     */
    public DatabaseInteractor()
    {
        this(null);
    }

    /**
     * A least complex implementation of the common execute method. Practically,
     * alters makeRequest() to be public.
     *
     * @return A boolean denoting success or failure from makeRequest()
     * @see DatabaseInteractor#makeRequest()
     */
    protected boolean execute()
    {
        return makeRequest();
    }

    /**
     * Executes this object and returns useful data if the interaction with the
     * database was successful.
     *
     * @return useful data
     */
    public final ResponseDataType use()
    {
        if(execute())
            return getData();
        else
            return null;
    }

    /**
     * All of the data that was retrieved is to be returned in a useful form.
     *
     * @return Data that was retrieved
     */
    protected abstract ResponseDataType getData();

    /**
     * This allows children to inject the statement in makeRequest() with variables.
     *
     * @param statement An SQL statement
     * @throws SQLException If the statement can't be modified in the way specified
     */
    protected abstract void setVariables(PreparedStatement statement) throws SQLException;

    /**
     * Makes a request to the database.
     *
     * @return A boolean denoting success or failure.
     */
    abstract boolean makeRequest();

    public final String getCommand()
    {
        return command;
    }

    /**
     * Set's this objects current comamnd
     *
     * @param command A String with an SQL query
     */
    protected final void setCommand(String command)
    {
        this.command = command;
    }

    /**
     * Provides access to the object's command index
     *
     * @return The current command index
     */
    public final int getIndex()
    {
        return index;
    }

    /**
     * Increments the command index. Called in makeRequest()
     */
    protected final void nextIndex()
    {
        index++;
    }

}
