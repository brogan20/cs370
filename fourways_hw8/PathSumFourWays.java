import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class PathSumFourWays {
    private static class Node implements Comparable<Node>{
        
        int weight;
        
        int dist = Integer.MAX_VALUE;
    
        int i;
        int j;
    
        Node(int weight, int i, int j) {
            this.weight = weight;
            this.i = i;
            this.j = j;
        }
    
        
        public int compare(Node n1, Node n2) {
            if (n1.dist == n2.dist) return 0;
            //returns 1 if greater
            if (n1.dist > n2.dist) return 1;
            return -1;
        } 
    
        public int compareTo(Node comp) {
            if (this.dist == comp.dist) return 0;
            if (this.dist > comp.dist) return 1;
            return -1;
        }
    
    }

    // Recursive backtracking to determine the path. Allows for rewinding in case two adjacencies match weights
    private static void backTrack(Node u, Node[][] graph) {
        backTrack_helper(u, graph, graph.length-1, graph[0].length-1, new ArrayList<Integer>());
    }

    private static boolean backTrack_helper(Node u, Node[][] graph, int i, int j, List<Integer> path) {
        int i_max = graph.length-1;
        int j_max = graph[0].length-1;

        path.add(u.weight);

        if(i > 0 || j > 0) {
            List<Node> neighbors = new ArrayList<>();

            // Check all adjacencies
            if(i > 0)
                neighbors.add(graph[i-1][j]);
            if(j > 0)
                neighbors.add(graph[i][j-1]);
            if(i < i_max)
                neighbors.add(graph[i+1][j]);
            if(j < j_max)
                neighbors.add(graph[i][j+1]);

            if(neighbors.size() == 0) {
                return false;
            }

            int curDist = u.dist - u.weight;
            boolean found = false; // For rewinding

            for(Node n : neighbors) {
                if(n.dist == curDist) {
                    found = true;

                    if(backTrack_helper(n, graph, n.i, n.j, path))
                        return true;
                    else {
                        path.remove(path.size()-1);
                        found = false;
                    }
                }
            }

            // We took a wrong path
            if(!found)
                return false;
        }

        System.out.print("Values:  [");
        for(int iter = path.size()-1; iter > 0; iter--) {
            System.out.print(path.get(iter) + ", ");
        }
        System.out.println(path.get(0) + "]");

        return true;
    }

    public static void main(String[] args) throws Exception{
        if(args.length < 1) {
            System.out.println("Usage: java PathSumFourWays <input file>");
            return;
        }
        File file = new File(args[0]);
        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(file));
        } catch (Exception e) {
            System.err.println("Error: File '" + args[0] + "' not found.");
            return;
        }
        
        String line;
        ArrayList<ArrayList<Integer>> arr = new ArrayList<>();

        // fill in the array list with numbers
        while ((line = br.readLine()) != null) {
            String[] strVals = line.split(",");
            ArrayList<Integer> vals = new ArrayList<>();
            for (String s: strVals) {
                vals.add(Integer.parseInt(s));
            }
            arr.add(vals);
        }

        int i_max = arr.size() - 1;
        int j_max = arr.get(0).size() - 1;
        Node[][] graph = new Node[i_max + 1][j_max + 1];

        PriorityQueue<Node> q = new PriorityQueue<>();

        // fill in the graph with nodes and also begin Dijkstra's thing
        for (int i = 0; i <= i_max; i++){
            for (int j = 0; j <= j_max; j++) {
                graph[i][j] = new Node(arr.get(i).get(j), i, j);
                q.add(graph[i][j]);
            }
        }
        graph[0][0].dist = graph[0][0].weight;
        
        while (!q.isEmpty()) {
            Node u = q.poll();
            //System.out.println("" + graph[0][0].weight + " to " + u.weight + " is " + u.dist);

            if (u == graph[i_max][j_max]) {
                System.out.println("Min sum: " + graph[i_max][j_max].dist);

                backTrack(u, graph);
                // Backtrack


                break;
            } else {
                if (u.i != 0)  { // Up
                    int up = graph[u.i - 1][u.j].weight + u.dist;
                    if (up < graph[u.i - 1][u.j].dist) {
                        q.remove(graph[u.i - 1][u.j]);
                        graph[u.i - 1][u.j].dist = up;
                        q.add(graph[u.i - 1][u.j]);
                    }
                }
                if (u.i != i_max) { // Down
                    int down = graph[u.i + 1][u.j].weight + u.dist;
                    if (down < graph[u.i + 1][u.j].dist) {
                        q.remove(graph[u.i + 1][u.j]);
                        graph[u.i + 1][u.j].dist = down;
                        q.add(graph[u.i + 1][u.j]);
                    }
                }
                if (u.j != 0) { // Left
                    int left = graph[u.i][u.j - 1].weight + u.dist;
                    if (left < graph[u.i][u.j - 1].dist) {
                        q.remove(graph[u.i][u.j - 1]);
                        graph[u.i][u.j - 1].dist = left;
                        q.add(graph[u.i][u.j - 1]);
                    }
                }
                if (u.j != j_max) { // Right
                    int right = graph[u.i][u.j + 1].weight + u.dist;
                    if (right < graph[u.i][u.j + 1].dist) {
                        q.remove(graph[u.i][u.j + 1]);
                        graph[u.i][u.j + 1].dist = right;
                        q.add(graph[u.i][u.j + 1]);
                    }
                }
            }   
        }
        //System.out.println(graph[i_max][j_max].dist);
    }
}

