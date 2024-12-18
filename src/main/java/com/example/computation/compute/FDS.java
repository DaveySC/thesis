package com.example.computation.compute;

import java.util.*;

public class FDS {

	private final boolean[][] graph;

	private int[][] shortestPathsSizes;

	private int[] maxShortestPathSize;

	private final int vertex;

	private int dominantNumber = Integer.MAX_VALUE,
			independentNumber = Integer.MAX_VALUE,
			dependentNumber = Integer.MAX_VALUE;

	private int geoDominantNumber = Integer.MAX_VALUE,
			independentGeoDominantNumber = Integer.MAX_VALUE,
			dependentGeoDominantNumber = Integer.MAX_VALUE;

	private int geoDominantSet,
			independentGeoDominantSet,
			dependentGeoDominantSet;

	private int dominantSet,
			independentDominantSet,
			dependentDominantSet;

	private Map<String, Set<Integer>> shortestPaths = new HashMap<>();

	public FDS(boolean[][] graph) {
		this.graph = graph;
		this.vertex = graph.length;
		this.shortestPathsSizes = new int[vertex][vertex];
		this.maxShortestPathSize = new int[vertex];
		findShortestPathsSizes();
		findShortestPaths();
		findAllDominantSets();
	}

	private void findShortestPaths(){
		for (int i = 0; i < vertex; i++) findShortestPath(i, i, new HashSet<>(), new boolean[vertex], 0);
	}
	//сравнить результат обеих программ и понять почему они разные
	//вот эта функция странно работает
	//if shortestPathsSizes[s][e] == 1 => то существует только один путь между ними, {s, e}
	//очень много холостых проходов, зачем искать путь длиной > максимального пути в строке
	//можно ограничить эту функцию на длину рекурсии
	private void findShortestPath(int s, int e, Set<Integer> q, boolean[] visited, int length) {
		if (length > maxShortestPathSize[s]) return;
		visited[e] = true;
		q.add(e);
		String key = s + Integer.toString(e);
		String reversedKey = e + Integer.toString(s);

		if (length == shortestPathsSizes[s][e]) {
			shortestPaths.computeIfAbsent(key, k -> new HashSet<>()).addAll(q);
			shortestPaths.computeIfAbsent(reversedKey, k -> new HashSet<>()).addAll(q);
		}

		for (int i = 0; i < vertex; i++) {
			if (!visited[i] && graph[e][i]) {
				findShortestPath(s, i, q, visited, length + 1);
			}
		}
		visited[e] = false;
		q.remove(e);
	}

	private void findShortestPathsSizes() {
		int totalFind = 0;
		int totalToFind = vertex * (vertex - 1);
		for (int i = 0; i < vertex && totalFind < totalToFind; i++) totalFind = bfs(i, totalFind);
	}

	private int bfs(int s, int totalFind) {
		Queue<Integer> q = new LinkedList<>();
		boolean[] visited = new boolean[vertex];
		visited[s] = true;
		shortestPathsSizes[s][s] = 0;
		q.add(s);
		int length = 1;
		while (!q.isEmpty()) {
			int currentQSize = q.size();
			for (int counter = 0; counter < currentQSize; counter++) {
				int curr = q.poll();
				for (int i = 0; i < vertex; i++) {
					if (!visited[i] && graph[curr][i]) {
						visited[i] = true;
						q.add(i);
						if (shortestPathsSizes[s][i] != length) totalFind++;
						if (shortestPathsSizes[i][s] != length) totalFind++;
						shortestPathsSizes[s][i] = length;
						shortestPathsSizes[i][s] = length;
						maxShortestPathSize[s] = Math.max(maxShortestPathSize[s], length);
					}
				}
			}
			length++;
		}
		return totalFind;
	}

	private void findAllDominantSets() {
		for (int i = 0; i < 1 << vertex; i++) {
			int count = Integer.bitCount(i);
			findGeoDominantSets(i, count);
			findDominantSets(i, count);
		}
	}

	private void findDominantSets(int i, int count) {
		if (isDominant(i)) {
			if (count == 0) return;
			if (count < dominantNumber) {
				dominantNumber = count;
				dominantSet = i;
			}
			if (count < independentNumber) {
				if (isIndependent(i)) {
					independentNumber = count;
					independentDominantSet = i;
				}
			}

			if (count < dependentNumber) {
				if (isDependent(i)) {
					dependentNumber = count;
					dependentDominantSet = i;
				}
			}

		}
	}


	private void findGeoDominantSets(int i, int count) {
		if (count == 0 || count == 1) return;
		if (count > geoDominantNumber
				&& count > independentGeoDominantNumber
				&& count > dependentGeoDominantNumber) {
			return;
		}
		if (isItGeoDominant(i)) {
			if (count < geoDominantNumber) {
				geoDominantNumber = count;
				geoDominantSet = i;
			}
			if (count < independentGeoDominantNumber) {
				if (isIndependent(i)) {
					independentGeoDominantNumber = count;
					independentGeoDominantSet = i;
				}
			}

			if (count < dependentGeoDominantNumber) {
				if (isDependent(i)) {
					dependentGeoDominantNumber = count;
					dependentGeoDominantSet = i;
				}
			}

		}
	}

	private boolean isItGeoDominant(int n) {
		Set<Integer> I = new HashSet<>();
		for (int i = 0; i < vertex; i++) {
			if (getBitFromInt(n, i)) {
				for (int j = i; j < vertex; j++) {
					if (getBitFromInt(n, j)) {
						I.addAll(shortestPaths.get(i + Integer.toString(j)));
						if (I.size() == vertex) return true;
					}
				}
			}
		}
		return I.size() == vertex;
	}

	private boolean isTotalDominant(int n) {
		boolean[] adjacent = new boolean[vertex];
		for (int i = 0; i < vertex; i++) {
			for (int j = 0; j < vertex; j++) {
				if (graph[i][j] && getBitFromInt(n, j)) adjacent[i] = true;
				//if (getBitFromInt(n, j) && i == j) adjacent[i] = true;
			}
		}
		for (boolean b : adjacent) if (!b) return false;
		return true;
	}

	private boolean isDominant(int n) {
		boolean[] adjacent = new boolean[vertex];
		for (int i = 0; i < vertex; i++) {
			if (!getBitFromInt(n, i)) continue;
			for (int j = 0; j < vertex; j++) {
				if (graph[i][j]) adjacent[j] = true;
			}
			adjacent[i] = true;
		}
		for (boolean b : adjacent) if (!b) return false;
		return true;
	}

	private boolean getBitFromInt(int b, int position) {
		return ((b << ~position) < 0);
	}

	private boolean isIndependent(int n) {
		for (int i = 0; i < vertex; i++) {
			if (!getBitFromInt(n, i)) continue;
			for (int j = i + 1; j < vertex; j++) {
				if (!getBitFromInt(n, j)) continue;
				if (graph[i][j] || graph[j][i]) return false;
			}
		}
		return true;
	}

	private boolean isDependent(int n) {
		List<Integer> vertexes = new ArrayList<>();
		for (int i = 0; i < vertex; i++) if (getBitFromInt(n, i)) vertexes.add(i);
		boolean[] visited = new boolean[vertex];
		Queue<Integer> queue = new ArrayDeque<>();
		queue.add(vertexes.get(0));
		while (queue.size() > 0) {
			int currentVertex = queue.poll();
			visited[currentVertex] = true;
			for (int vertexToCheck : vertexes) {
				if (!visited[vertexToCheck] && graph[vertexToCheck][currentVertex]) queue.add(vertexToCheck);
			}
		}
		for (int v : vertexes) if (!visited[v]) return false;
		return true;
	}

	private void convertSetIntToString(int set) {
		StringBuilder str = new StringBuilder("{");
		for (int i = 0; i < vertex; i++) {
			if (getBitFromInt(set, i)) str.append(i).append(", ");
		}
		str.setLength(str.length() - 2);
		str.append("} ");
		System.out.print(str);
	}

	public int getDominantNumber() {
		return dominantNumber;
	}

	public int getIndependentNumber() {
		return independentNumber;
	}

	public int getDependentNumber() {
		return dependentNumber;
	}

	public int getGeoDominantNumber() {
		return geoDominantNumber == Integer.MAX_VALUE ? 0 : geoDominantNumber;
	}

	public int getDependentGeoDominantNumber() {
		return dependentGeoDominantNumber == Integer.MAX_VALUE ? 0 : dependentGeoDominantNumber;
	}

	public int getIndependentGeoDominantNumber() {
		return independentGeoDominantNumber == Integer.MAX_VALUE ? 0 : independentGeoDominantNumber;
	}

	public String getSetByInt(int val) {
		if (Integer.bitCount(val) == 0) return "";
		StringBuilder ans = new StringBuilder();
		for (int i = 0; i < vertex; i++) {
			if (getBitFromInt(val, i)) ans.append(i).append(" ");
		}
		ans.setLength(ans.length() - 1);
		return ans.toString();
	}

	public String getGeoDominantSet() {
		return getSetByInt(geoDominantSet);
	}

	public String getIndependentGeoDominantSet() {
		return getSetByInt(independentGeoDominantSet);
	}

	public String getDependentGeoDominantSet() {
		return getSetByInt(dependentGeoDominantSet);
	}

	public String getDominantSet() {
		return getSetByInt(dominantSet);
	}

	public String getIndependentDominantSet() {
		return getSetByInt(independentDominantSet);
	}

	public String getDependentDominantSet() {
		return getSetByInt(dependentDominantSet);
	}
}
