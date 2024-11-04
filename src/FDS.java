import java.util.*;

public class FDS {
	enum TypeOfGraph {DOMINANT, DEPENDENT, INDEPENDENT};

	private final boolean[][] graph;

	private int[][] shortestPathsSizes;

	private final int vertex;

	private int dominantNumber = Integer.MAX_VALUE,
			independentNumber = Integer.MAX_VALUE,
			dependentNumber = Integer.MAX_VALUE;

	private int geoDominantNumber = Integer.MAX_VALUE,
			independentGeoDominantNumber = Integer.MAX_VALUE,
			dependentGeoDominantNumber = Integer.MAX_VALUE;

	private Map<String, List<Set<Integer>>> shortestPaths = new HashMap<>();

	public FDS(boolean[][] graph) {
		this.graph = graph;
		this.vertex = graph.length;
		this.shortestPathsSizes = new int[vertex][vertex];
		findShortestPathsSizes();
		findShortestPaths();
		findDominantSets();
	}

	private void findShortestPaths(){
		for (int i = 0; i < vertex; i++) findShortestPath(i, i, new HashSet<>(), new boolean[vertex], 0);
	}

	private void findShortestPath(int s, int e,  Set<Integer> q, boolean[] visited, int length) {
		visited[e] = true;
		q.add(e);
		String key = Integer.toString(s) + Integer.toString(e);
		String reversedKey = Integer.toString(e) + Integer.toString(s);
		if (!shortestPaths.containsKey(reversedKey)) {
			if (length == shortestPathsSizes[s][e])
				shortestPaths.put(key, shortestPaths.computeIfAbsent(key, k -> new ArrayList<>())).add((new HashSet<>(q)));
		}
		for (int i = 0; i < vertex; i++) {
			if (graph[e][i] && !visited[i]) {
				findShortestPath(s, i, q, visited, length + 1);
			}
		}
		visited[e] = false;
		q.remove(e);
	}

	private void findShortestPathsSizes() {
		for (int i = 0; i < vertex; i++) bfs(i);
	}

	private void bfs(int s) {
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
					if (graph[curr][i] && !visited[i]) {
						visited[i] = true;
						q.add(i);
						shortestPathsSizes[s][i] = length;
					}
				}
			}
			length++;
		}
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
						String key = Integer.toString(i) + Integer.toString(j);
						for (Set<Integer> subset : shortestPaths.get(key)) {
							I.addAll(subset);
						}
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
