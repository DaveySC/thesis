package com.example.computation.compute;

import java.util.*;

public class FIV {
    private boolean[][] graph;
    private int vertex;
    private int minimumDegree;
    private int maximumDegree;
    private int numberOfBridges;
    private int numberOfCutVertices;
    private int cliqueNumber;
    private int girth;
    private int diameter;
    private int periphery;
    private int radius;
    private int wienerIndex;
    private int firstZagrebIndex;
    private int secondZagrebIndex;
    private int chromaticNumber;
    private String vectorOfDegrees = "";
    private boolean isTree = false;


    public FIV(boolean[][] graph) {
        this.graph = graph;
        this.vertex = graph.length;
        this.minimumDegree = Integer.MAX_VALUE;
        this.maximumDegree = Integer.MIN_VALUE;
        this.numberOfBridges = 0;
        this.cliqueNumber = 0;
        this.girth = Integer.MAX_VALUE;
        this.diameter = 0;
        this.periphery = 0;
        this.radius = Integer.MAX_VALUE;
        this.wienerIndex = 0;
        this.firstZagrebIndex = 0;
        this.secondZagrebIndex = 0;
        this.numberOfCutVertices = 0;
        this.chromaticNumber = 0;
        calcAll();
    }

    private void findMinimumDegree() {
        for (int i = 0; i < vertex; i++) {
            int degree = 0;
            for (int j = 0; j < vertex; j++) {
                if (graph[i][j]) {
                    degree++;
                }
            }
            if (degree < minimumDegree) {
                minimumDegree = degree;
            }
        }
    }

    private void findMaximumDegree() {
        for (int i = 0; i < vertex; i++) {
            int degree = 0;
            for (int j = 0; j < vertex; j++) {
                if (graph[i][j]) {
                    degree++;
                }
            }
            if (degree > maximumDegree) {
                maximumDegree = degree;
            }
        }
    }

    private void findNumberOfBridges() {
        boolean[] visited = new boolean[vertex];
        int time = 1;
        int[] tin = new int[vertex];
        int[] up = new int[vertex];
        HashSet<String> setOfBridges = new HashSet<>();
        dfsBridge(0, 0, tin, up, visited, time, setOfBridges);
        numberOfBridges = setOfBridges.size() / 2;
    }

    private void dfsBridge(int v, int p, int[] tin, int[] up, boolean[] visited, int time, Set<String> setOfBridges) {
        tin[v] = time++;
        up[v] = tin[v];
        visited[v] = true;
        for (int i = 0; i < vertex; i++) {
            if (graph[v][i]) {
                int u = i;
                if (u == p) continue;
                if (!visited[u]) {
                    dfsBridge(u, v, tin, up, visited, time, setOfBridges);
                    up[v] = Math.min(up[v], up[u]);
                    if (up[u] >= tin[v] && v != p) {
                        setOfBridges.add(v+""+u);
                        setOfBridges.add(u+""+v);
                    }
                } else {
                    up[v] = Math.min(up[v], tin[u]);
                }
            }
        }
    }


    private void findNumberOfCutPoints() {
        boolean[] visited = new boolean[vertex];
        int time = 1;
        int[] tin = new int[vertex];
        int[] up = new int[vertex];
        dfsCutPoints(0, 0, tin, up, visited, time);
    }

    private void dfsCutPoints(int v, int p, int[] tin, int[] up, boolean[] visited, int time) {
        tin[v] = time++;
        up[v] = tin[v];
        visited[v] = true;

        int children = 0;

        for (int i = 0; i < vertex; i++) {
            if (graph[v][i]) {
                int u = i;
                if (u == p) continue;
                if (!visited[u]) {
                    dfsCutPoints(u, v, tin, up, visited, time);
                    up[v] = Math.min(up[v], up[u]);
                    if (up[u] >= tin[v] && v != p) numberOfCutVertices++;
                    children++;
                } else {
                    up[v] = Math.min(up[v], tin[u]);
                }
            }
        }
        if (v == p && children > 1){
            numberOfCutVertices++;
        }
    }


    private void findCliqueNumber() {
        int N = vertex;
        for (int mask = 0; mask < (1 << N); mask++) {
            HashSet<Integer> clique = new HashSet<>();
            for (int j = 0; j < N; j++) {
                if((mask & (1 << j)) != 0){
                    clique.add(j);
                    if (isItClique(clique))
                        cliqueNumber = Math.max(cliqueNumber, clique.size());
                }
            }
        }
    }

    private boolean isItClique(HashSet<Integer> clique) {
        for (int v : clique) {
            for (int w : clique) {
                if (v == w) continue;
                if (!graph[v][w]) return false;
            }
        }
        return true;
    }

    private void findGirth() {
        for (int i = 0; i < vertex; i++) {
            boolean[] visited = new boolean[vertex];
            dfsGirth(i, visited, 1, i);
        }
    }

    private void dfsGirth(int u, boolean[] visited, int length, int start) {
        visited[u] = true;

        for (int v = 0; v < vertex; v++) {
            if (graph[u][v]) {
                if (!visited[v]) {
                    dfsGirth(v, visited, length + 1, start);
                } else if (v == start && length > 2) {
                    girth = Math.min(girth, length);
                }
            }
        }

        visited[u] = false;
    }

    private void findDiameter() {
        for (int i = 0; i < vertex; i++) {
            int[] distance = new int[vertex];
            Arrays.fill(distance, Integer.MAX_VALUE);
            distance[i] = 0;

            Queue<Integer> queue = new LinkedList<>();
            queue.add(i);

            while (!queue.isEmpty()) {
                int current = queue.poll();

                for (int v = 0; v < vertex; v++) {
                    if (graph[current][v] && distance[v] == Integer.MAX_VALUE) {
                        distance[v] = distance[current] + 1;
                        queue.add(v);
                    }
                }
            }

            for (int dist : distance) {
                if (dist != Integer.MAX_VALUE && dist > diameter) {
                    diameter = dist;
                }
            }
        }
    }


    private int degree(int u) {
        int degree = 0;
        for (int v = 0; v < vertex; v++) {
            if (graph[u][v]) {
                degree++;
            }
        }
        return degree;
    }


    private void findPeriphery() {
        for (int i = 0; i < vertex; i++) {
            int[] distance = new int[vertex];
            Arrays.fill(distance, Integer.MAX_VALUE);
            distance[i] = 0;

            Queue<Integer> queue = new LinkedList<>();
            queue.add(i);

            while (!queue.isEmpty()) {
                int current = queue.poll();

                for (int v = 0; v < vertex; v++) {
                    if (graph[current][v] && distance[v] == Integer.MAX_VALUE) {
                        distance[v] = distance[current] + 1;
                        queue.add(v);
                    }
                }
            }
            int helper = Integer.MIN_VALUE;
            for (int dist : distance) {
                if (dist != Integer.MAX_VALUE) helper = Math.max(helper, dist);
            }
            if (helper == diameter) {
                periphery++;
            }
        }
    }

    private void findRadius() {
        for (int i = 0; i < vertex; i++) {
            int[] distance = new int[vertex];
            Arrays.fill(distance, Integer.MAX_VALUE);
            distance[i] = 0;

            Queue<Integer> queue = new LinkedList<>();
            queue.add(i);

            while (!queue.isEmpty()) {
                int current = queue.poll();

                for (int v = 0; v < vertex; v++) {
                    if (graph[current][v] && distance[v] == Integer.MAX_VALUE) {
                        distance[v] = distance[current] + 1;
                        queue.add(v);
                    }
                }
            }

            int maxDistance = 0;
            for (int dist : distance) {
                if (dist != Integer.MAX_VALUE && dist > maxDistance) {
                    maxDistance = dist;
                }
            }

            radius = Math.min(radius, maxDistance);
        }
    }

    private void findRadiusDiameterPeriphery() {
        findDiameter();
        findRadius();
        findPeriphery();
    }

    private void findWienerIndex() {
        for (int i = 0; i < vertex; i++) {
            int[] distance = new int[vertex];
            Arrays.fill(distance, Integer.MAX_VALUE);
            distance[i] = 0;

            Queue<Integer> queue = new LinkedList<>();
            queue.add(i);

            while (!queue.isEmpty()) {
                int current = queue.poll();

                for (int v = 0; v < vertex; v++) {
                    if (graph[current][v] && distance[v] == Integer.MAX_VALUE) {
                        distance[v] = distance[current] + 1;
                        queue.add(v);
                    }
                }
            }

            for (int dist : distance) {
                if (dist != Integer.MAX_VALUE) {
                    wienerIndex += dist;
                }
            }
        }
    }

    private void findFirstZagrebIndex() {
        for (int i = 0; i < vertex; i++) {
            firstZagrebIndex += (int) Math.pow(degree(i), 2);
        }
    }

    private void findSecondZagrebIndex() {
        for (int i = 0; i < vertex; i++) {
            for (int j = 0; j < vertex; j++) {
                if (graph[i][j]) {
                    secondZagrebIndex += degree(i) * degree(j);
                }
            }
        }
    }

    boolean isSafe(int v, int[] color, int c) {
        for (int neighbor = 0; neighbor < vertex; neighbor++) {
            if (graph[v][neighbor]) {
                if (color[neighbor] == c) return false;
            }
        }
        return true;
    }

    // Backtracking function to find a valid coloring
    boolean graphColoringUtil(int v, int[] color) {
        if (v == vertex) {
            return true; // All vertices are colored, a solution is found
        }

        for (int c = 1; c <= 100; ++c) {
            if (isSafe(v, color, c)) {
                color[v] = c;

                // Recur for the next vertices
                if (graphColoringUtil(v + 1, color)) {
                    return true;
                }

                // Backtrack
                color[v] = 0;
            }
        }

        return false; // No solution found for this coloring
    }

    // com.example.Main function to find chromatic number
    int graphColoring() {
        int n = vertex;
        int[] color = new int[n];
        if (!graphColoringUtil(0, color)) {
            return -1;
        }
        // Count unique colors to determine chromatic number
        Set<Integer> uniqueColors = new HashSet<>();
        for (int c : color) {
            uniqueColors.add(c);
        }
        return uniqueColors.size();
    }

    private void findChromaticNumber(){
        chromaticNumber = graphColoring();
    }

    private void findVectorOfDegrees() {
        List<Integer> vector = new ArrayList<>();
        for (int i = 0; i < vertex; i++) {
            vector.add(degree(i));
        }
        Collections.sort(vector);
        StringBuilder helper = new StringBuilder();
        for (int value : vector) {
            helper.append(String.valueOf(value));
        }
        vectorOfDegrees = helper.toString();
    }

    private void isItTree() {
        int count = 0;
        for (int i = 0; i < vertex; i++) {
            for (int j = 0; j < vertex; j++) {
                if (graph[i][j]) count++;
            }
        }
        count = count / 2;
        isTree = count == vertex - 1;
    }


    public int getMinimumDegree() {
        return minimumDegree;
    }

    public int getMaximumDegree() {
        return maximumDegree;
    }

    public int getNumberOfBridges() {
        return numberOfBridges;
    }



    public int getCliqueNumber() {
        return cliqueNumber;
    }

    public int getGirth() {
        return girth;
    }



    public int getDiameter() {
        return diameter;
    }


    public int getPeriphery() {
        return periphery;
    }

    public int getRadius() {
        return radius;
    }



    public int getWienerIndex() {
        return wienerIndex;
    }

    public int getFirstZagrebIndex() {
        return firstZagrebIndex;
    }

    public int getSecondZagrebIndex() {
        return secondZagrebIndex;
    }

    public int getNumberOfCutVertices() {
        return numberOfCutVertices;
    }

    public int getChromaticNumber() {
        return chromaticNumber;
    }

    public String getVectorOfDegrees() {
        return vectorOfDegrees;
    }

    public boolean isTree() {
        return isTree;
    }

    private void calcAll() {
        findMinimumDegree(); //+
        findMaximumDegree();//+
        findNumberOfBridges();//+
        findNumberOfCutPoints();//+
        findCliqueNumber();//+
        findGirth();//+
        findRadiusDiameterPeriphery();//+
        findWienerIndex();//+
        findFirstZagrebIndex();//+
        findSecondZagrebIndex();//+
        findChromaticNumber();//+
        findVectorOfDegrees();//+
        isItTree();//+
    }

}
