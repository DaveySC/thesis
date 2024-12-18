package com.example.computation.clerk;

import com.example.computation.utils.ConnectionManager;
import com.example.computation.utils.Container;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SqlClerk{

    private Connection connection;
    private PreparedStatement ps;

    public SqlClerk() {
        this.connection = ConnectionManager.getConnection();
    }

    public void write(Container container){
        try {
            ps = connection
                    .prepareStatement("INSERT INTO Graphs10WithVals VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        while ( !container.isEmpty()) {
            int size = container.size();
            while (size-- > 0) {
                List<Object> values = container.poll();
                ps.setString(1, (String) values.get(0));
                for (int i = 2; i < 21; i++) {
                    ps.setInt(i, (int) values.get(i - 1));
                }
                ps.setString(21, (String) values.get(20));
                ps.setBoolean(22, (boolean) values.get(21));
                ps.addBatch();
            }
            ps.executeBatch();
            ps.clearBatch();
        }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage() + e.getStackTrace()[0].toString());
            System.exit(1);
        }
    }

}
