import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PathSumFourWays {

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
        // its a graph with nodes
        Node[][] graph = new Node[i_max + 1][j_max + 1];

        //issa a queue
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
            //grabs and removes the head of the queue
            Node u = q.poll();
            //System.out.println("" + graph[0][0].weight + " to " + u.weight + " is " + u.dist);

            if (u.weight == graph[i_max][j_max].weight) {
                System.out.println("Min sum: " + graph[i_max][j_max].dist);

                // Backtrack
                List<Integer> path = new ArrayList<>();
                path.add(u.weight);

                int i = i_max;
                int j = j_max;

                while(i > 0 || j > 0) {
                    List<Node> neighbors = new ArrayList<>();
                    if(i > 0)
                        neighbors.add(graph[i-1][j]);
                    if(j > 0)
                        neighbors.add(graph[i][j-1]);
                    if(i < i_max)
                        neighbors.add(graph[i+1][j]);
                    if(j < j_max)
                        neighbors.add(graph[i][j+1]);

                    int curDist = u.dist - u.weight;

                    for(Node n : neighbors) {
                        if(n.dist == curDist) {
                            path.add(n.weight);
                            i = n.i;
                            j = n.j;

                            u = n;
                        }
                    }
                }

                System.out.print("Values:  [");
                for(int iter = path.size()-1; iter > 0; iter--) {
                    System.out.print(path.get(iter) + ", ");
                }
                System.out.println(path.get(0) + "]");

                break;
            } else {
                if (u.i != 0)  { // Get node above
                    int up = graph[u.i - 1][u.j].weight + u.dist;
                    if (up < graph[u.i - 1][u.j].dist) {
                        q.remove(graph[u.i - 1][u.j]);
                        graph[u.i - 1][u.j].dist = up;
                        q.add(graph[u.i - 1][u.j]);
                    }
                }
                if (u.i != i_max) { // the down case
                    int down = graph[u.i + 1][u.j].weight + u.dist;
                    if (down < graph[u.i + 1][u.j].dist) {
                        q.remove(graph[u.i + 1][u.j]);
                        graph[u.i + 1][u.j].dist = down;
                        q.add(graph[u.i + 1][u.j]);
                    }
                }
                if (u.j != 0) { // the left case
                    int left = graph[u.i][u.j - 1].weight + u.dist;
                    if (left < graph[u.i][u.j - 1].dist) {
                        q.remove(graph[u.i][u.j - 1]);
                        graph[u.i][u.j - 1].dist = left;
                        q.add(graph[u.i][u.j - 1]);
                    }
                }
                if (u.j != j_max) { // the right case
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

// node class
class Node implements Comparable<Node>{
        
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

    public void printString() {
        System.out.println("Weight: " + weight + ", Dist: " + dist + ", i: " + i + ", j: " + j);
    }
}