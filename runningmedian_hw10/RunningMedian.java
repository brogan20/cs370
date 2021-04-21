// Names: Brogan Clements, Dominick DiMaggio, Ishaan Patel
// Pledge: I pledge my honor that I have abided by the Stevens Honor System.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Comparator;

public class RunningMedian {

    public static void main(String[] args) throws IOException {
        //boring input parsing and stuff
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int size = Integer.parseInt(br.readLine());
        int [] lst = new int[size];
        for (int i=0; i<size; i++) 
            lst[i] = Integer.parseInt(br.readLine());

        /**
         * Heap plan or whatever
         * got a min heap and max heap
         * if we get a value larger then the current median, store in min heap
         * if we get a value smaller then current median, store in max heap
         * when you need to rebalance, pluck the head, and insert into the other one
         * if the heap sizes aren't equal, the median will be the head of the larger heap
         */

         PriorityQueue<Integer> minHeap = new PriorityQueue<>();
         PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
         double median = 0;

         for (int i: lst) {
            // add element to heap
            if (i > median)
                minHeap.add(i);
            else
                maxHeap.add(i);
            
            // rebalance heap if an insertion makes it imbalanced
            if (minHeap.size() > maxHeap.size()+1)
                maxHeap.add(minHeap.poll());
            else if (maxHeap.size() > minHeap.size() + 1)
                minHeap.add(maxHeap.poll());

            // determines the median based on heap sizes
            if (minHeap.size() == maxHeap.size()) {
                median = (minHeap.peek() + maxHeap.peek())/2.0;
            } else if (minHeap.size() > maxHeap.size()) {
                median = minHeap.peek();
            } else {
                median = maxHeap.peek();
            }
            System.out.println(median);
        }
    }
}