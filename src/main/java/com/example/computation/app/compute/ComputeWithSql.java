package com.example.computation.app.compute;

import com.example.computation.clerk.SqlClerk;
import com.example.computation.compute.FDS;
import com.example.computation.compute.FIV;
import com.example.computation.utils.Container;
import com.example.computation.utils.Graph6Converter;


public class ComputeWithSql implements Compute{

    private final SqlClerk sqlClerk;
    private final Container container;

    public ComputeWithSql() {
        this.sqlClerk = new SqlClerk();
        this.container = new Container();
    }

    @Override
    public void compute(String line) {
        if (!Graph6Converter.validate(line)) throw new IllegalArgumentException("Wrong graph6 value");
        FDS fds = new FDS(Graph6Converter.fromGraph6ToAdjacentMatrix(line));
        FIV fiv = new FIV(Graph6Converter.fromGraph6ToAdjacentMatrix(line));
        container.addValue(line, fds, fiv);
    }

    @Override
    public void makeRecordAction() {
        sqlClerk.write(container);
    }


}
