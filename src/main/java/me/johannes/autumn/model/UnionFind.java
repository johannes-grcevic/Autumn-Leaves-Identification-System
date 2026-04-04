package me.johannes.autumn.model;

public class UnionFind {
    private final int[] parent;
    private final int[] rank;
    private int count;

    public UnionFind(int size) {
        parent = new int[size];
        rank = new int[size];
        count = size;

        // initializes with each element as its own representative (its own disjoint set)
        setInitialState();
    }

    // find with Path Compression
    public int find(int p) {
        if (parent[p] != p)
            parent[p] = find(parent[p]); // point directly to root
        return parent[p];
    }

    // implements Union by rank (height)
    public void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);

        // rootP and rootQ are already in the same set
        if (rootP == rootQ) {
            return;
        }

        // if rootP's rank is lower than rootQ's rank
        if (rank[rootP] < rank[rootQ]) {
            // then move rootP under rootQ
            parent[rootP] = rootQ;
            // if rootP's rank is larger than rootQ's rank
        } else if (rank[rootP] > rank[rootQ]) {
            // then move rootQ under rootP
            parent[rootQ] = rootP;
            // if they are the same height
        } else {
            // move rootP under rootQ
            parent[rootP] = rootQ;
            rank[rootQ]++; // increase rank if same height
        }

        count--; // decrease count by one as two sets are merged
    }

    // Check if two elements are connected (in the same set)
    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    // number of connected components
    public int count() {
        return count;
    }

    // reset the tree to its initial state
    public void clear() {
        setInitialState();
    }

    private void setInitialState() {
        for (int i = 0; i < parent.length; i++) {
            parent[i] = i;
            rank[i] = 0;
        }

        // reset count
        count = parent.length;
    }
}
