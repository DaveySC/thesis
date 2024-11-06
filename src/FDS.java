import java.util.*;

public class FDS {
	enum TypeOfGraph {DOMINANT, DEPENDENT, INDEPENDENT};

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

	private Map<String, Set<Integer>> shortestPaths = new HashMap<>();

	public FDS(boolean[][] graph) {
		this.graph = graph;
		this.vertex = graph.length;
		this.shortestPathsSizes = new int[vertex][vertex];
		this.maxShortestPathSize = new int[vertex];
		findShortestPathsSizes();
		findShortestPaths();
		findDominantSets();
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

	//вот тут понять почему total find не помог
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

	private void findDominantSets() {
		for (int i = 0; i < 1 << vertex; i++) {
			int count = Integer.bitCount(i);
			if (count == 0) continue;

			if (isItGeoDominant(i)) {
				if (count < geoDominantNumber) geoDominantNumber = count;
				if (count < independentGeoDominantNumber)
					if (isIndependent(i)) independentGeoDominantNumber = count;
				if (count < dependentGeoDominantNumber)
					if (isDependent(i)) dependentGeoDominantNumber = count;
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

}
