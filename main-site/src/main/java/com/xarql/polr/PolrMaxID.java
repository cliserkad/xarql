package com.xarql.polr;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.xarql.util.DatabaseQuery;

public class PolrMaxID extends DatabaseQuery<Integer>
{
    private static final String COMMAND = "SELECT MAX(id) FROM polr";

    private static Integer max;

    public PolrMaxID()
    {
        super(COMMAND);
    }

    public static Integer useStatic()
    {
        return new PolrMaxID().use();
    }

    @Override
    protected Integer getData()
    {
        return max;
    }

    @Override
    protected void setVariables(PreparedStatement statement) throws SQLException
    {
        return;
    }

    @Override
    protected void processResult(ResultSet rs) throws SQLException
    {
        max = rs.getInt(1);
    }

}
