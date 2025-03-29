package com.hust.openerp.taskmanagement.algorithm;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HopcroftKarp {
    private final int n; // number of left nodes (users)
    private final int m; // number of right nodes (expanded sessions)
    private final List<Integer>[] graph;
    private final int[] pairU; // matching for left nodes: user -> session index
    private final int[] pairV; // matching for right nodes: expanded session -> user
    private final int[] dist;
    private final int INF = Integer.MAX_VALUE;

    public HopcroftKarp(int n, int m, List<Integer>[] graph) {
        this.n = n;
        this.m = m;
        this.graph = graph;
        pairU = new int[n];
        pairV = new int[m];
        dist = new int[n];
        Arrays.fill(pairU, -1);
        Arrays.fill(pairV, -1);
    }

    private boolean bfs() {
        Queue<Integer> queue = new LinkedList<>();
        for (int u = 0; u < n; u++) {
            if (pairU[u] == -1) {
                dist[u] = 0;
                queue.add(u);
            } else {
                dist[u] = INF;
            }
        }
        int distance = INF;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            if (dist[u] < distance) {
                for (int v : graph[u]) {
                    if (pairV[v] == -1) {
                        distance = dist[u] + 1;
                    } else if (dist[pairV[v]] == INF) {
                        dist[pairV[v]] = dist[u] + 1;
                        queue.add(pairV[v]);
                    }
                }
            }
        }
        return distance != INF;
    }

    private boolean dfs(int u) {
        if (u != -1) {
            for (int v : graph[u]) {
                int p = pairV[v];
                if (p == -1 || (dist[p] == dist[u] + 1 && dfs(p))) {
                    pairV[v] = u;
                    pairU[u] = v;
                    return true;
                }
            }
            dist[u] = INF;
            return false;
        }
        return true;
    }

    public int run() {
        int matching = 0;
        while (bfs()) {
            for (int u = 0; u < n; u++) {
                if (pairU[u] == -1 && dfs(u)) {
                    matching++;
                }
            }
        }
        return matching;
    }
}
