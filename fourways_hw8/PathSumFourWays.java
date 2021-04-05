import java.io.File;
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
    public static void main(String[] args) throws Exception{
        File file = new File(args[0]);
        BufferedReader br = new BufferedReader(new FileReader(file));
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

        for (int i = 0; i <= i_max; i++){
            for (int j = 0; j <= j_max; j++) {
                graph[i][j] = new Node(arr.get(i).get(j), i, j);
                q.add(graph[i][j]);
            }
        }
        graph[0][0].dist = graph[0][0].weight;
        
        while (!q.isEmpty()) {
            Node u = q.poll();
            System.out.println(u.weight + " " + u.dist);

            if (u == graph[i_max][j_max]) {
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
        System.out.println(graph[i_max][j_max].dist);
    }
}

