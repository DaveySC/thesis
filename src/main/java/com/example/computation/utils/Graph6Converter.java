package com.example.computation.utils;

public class Graph6Converter {
	public static boolean[][] fromGraph6ToAdjacentMatrix(String g) {
		int size = g.charAt(0) - 63;
		StringBuilder gString = new StringBuilder();
		for (int i = 1; i < g.length(); i++) {
			int val = g.charAt(i) - 63;
			String tmp = Integer.toBinaryString(val);
			gString.append(bringToTheFrom(tmp));
		}
		boolean[][] graph = new boolean[size][size];
		int pos = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < i; j++) {
				if (gString.charAt(pos++) == '1') {
					graph[i][j] = true;
					graph[j][i] = true;
				}
			}
		}
		return graph;
	}
	public static boolean validate(String g) {
		return g.length() != 0 && Character.isLetter(g.charAt(0));
	}

	public static String bringToTheFrom(String what) {
		int length = what.length();
		if (length == 6) return what;
		if (length > 6) return what.substring(length - 6);
		StringBuilder helper = new StringBuilder(what);
		while (length++ < 6) helper.insert(0, '0');
		return helper.toString();
	}
}