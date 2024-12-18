package com.example.computation.utils;

import com.example.computation.compute.FDS;
import com.example.computation.compute.FIV;

import java.util.*;

public class Container {
    private Queue<List<Object>> values = new ArrayDeque<>();

    public void addValue(String graphStr, FDS fds, FIV fiv) {
        List<Object> list = new ArrayList<>();
        list.addAll(List.of(graphStr,
                fds.getDominantNumber(),
                fds.getIndependentNumber(),
                fds.getDependentNumber(),
                fds.getGeoDominantNumber(),
                fds.getIndependentGeoDominantNumber(),
                fds.getDependentGeoDominantNumber(),
                fiv.getMinimumDegree(),
                fiv.getMaximumDegree(),
                fiv.getNumberOfBridges(),
                fiv.getNumberOfCutVertices(),
                fiv.getCliqueNumber(),
                fiv.getGirth(),
                fiv.getDiameter(),
                fiv.getPeriphery(),
                fiv.getRadius(),
                fiv.getWienerIndex(),
                fiv.getFirstZagrebIndex(),
                fiv.getSecondZagrebIndex(),
                fiv.getChromaticNumber(),
                fiv.getVectorOfDegrees(),
                fiv.isTree()
        ));
        values.add(list);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public int size() {
        return values.size();
    }

    public List<Object> poll() {
        return values.poll();
    }
}
