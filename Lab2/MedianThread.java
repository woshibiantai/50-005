import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class MedianThread {
    ArrayList<Integer> readExternalFile(String filePath, int inputSize) throws FileNotFoundException {
        Scanner inputScanner = new Scanner(new File(filePath));
        ArrayList<Integer> arrayOutput = new ArrayList<>(inputSize);

        int i = 0;
        while (inputScanner.hasNext()) {
            arrayOutput.add(i, inputScanner.nextInt());
            i++;
        }

        return arrayOutput;
    }

    public ArrayList<MedianMultiThread> createThreads(int numOfThreads, ArrayList<ArrayList<Integer>> subArrays) {
        ArrayList<MedianMultiThread> threadArrayList = new ArrayList<>(numOfThreads);
        for (int i = 0; i < numOfThreads; i++) {
            MedianMultiThread thread = new MedianMultiThread(subArrays.get(i));
            threadArrayList.add(thread);
        }
        return threadArrayList;
    }

    private ArrayList<ArrayList<Integer>> partitioningArray(ArrayList<Integer> originalArray, int inputSize, int numOfThread) {
        int subArraySize = inputSize/numOfThread;
        ArrayList<ArrayList<Integer>> outputArray = new ArrayList<>(numOfThread);

        int startingIndex = 0;
        int endingIndex = subArraySize - 1;
        for (int i = 0; i < numOfThread; i++) {
            ArrayList<Integer> splitArray = new ArrayList<>(originalArray.subList(startingIndex,endingIndex));
            outputArray.add(i,splitArray);
            startingIndex += subArraySize;
            endingIndex += subArraySize;
        }
        return outputArray;
    }

    public static void main(String[] args) throws InterruptedException, FileNotFoundException  {
        MedianThread medianThread = new MedianThread();
        // TODO: read data from external file and store it in an array
        // Note: you should pass the file as a first command line argument at runtime.
        String filePath = args[0];
        int inputSize = 524288;
        ArrayList<Integer> originalArray = medianThread.readExternalFile(filePath,inputSize);

        // define number of threads
        int NumOfThread = Integer.parseInt(args[1]);;
//        int NumOfThread = Integer.parseInt(args[2]);

        // TODO: partition the array list into N subArrays, where N is the number of threads
        ArrayList<ArrayList<Integer>> subArrays = medianThread.partitioningArray(originalArray,inputSize,NumOfThread);

        // TODO: start recording time
        long startTime = System.currentTimeMillis();

        // TODO: create N threads and assign subArrays to the threads so that each thread sorts
        // its repective subarray. For example,
        ArrayList<MedianMultiThread> threadArrayList = medianThread.createThreads(NumOfThread,subArrays);

        // TODO: start each thread to execute your sorting algorithm defined under the run() method, for example,
        for (MedianMultiThread thread : threadArrayList) {
            thread.start();
        }

        for (MedianMultiThread thread : threadArrayList) {
            thread.join();
        }


        // TODO: use any merge algorithm to merge the sorted subarrays and store it to another array, e.g., sortedFullArray.
        ArrayList<ArrayList<Integer>> sortedArrays = new ArrayList<>();
        for (MedianMultiThread thread : threadArrayList) {
            sortedArrays.add(thread.getInternal());
        }
        ArrayList<Integer> sortedFullArray = medianThread.mergeKSortedArray(sortedArrays);

        //TODO: get median from sortedFullArray
        //e.g, computeMedian(sortedFullArray);
        double output = computeMedian(sortedFullArray);

        // TODO: stop recording time and compute the elapsed time
        long finalTime = System.currentTimeMillis();
        long timeElapsed = finalTime - startTime;

        // TODO: printout the final sorted array

        // TODO: printout median
        System.out.println("The Median value is ...");
        System.out.println(output);
        System.out.println("Running time is " + timeElapsed + " milliseconds\n");
    }

    public static ArrayList<Integer> mergeKSortedArray(ArrayList<ArrayList<Integer>> arr) {
        //PriorityQueue is heap in Java
        PriorityQueue<ArrayContainer> queue = new PriorityQueue<ArrayContainer>();
        int total=0;

        //add arrays to heap
        for (int i = 0; i < arr.size(); i++) {
            queue.add(new ArrayContainer(arr.get(i), 0));
            total = total + arr.get(i).size();
        }

        int m=0;
//        int result[] = new int[total];
        ArrayList<Integer> result = new ArrayList<>(total);

        //while heap is not empty
        while(!queue.isEmpty()){
            ArrayContainer ac = queue.poll();
            result.add(m++,ac.arr.get(ac.index));
//            result[m++]=ac.arr.get(ac.index);

            if(ac.index < ac.arr.size()-1){
                queue.add(new ArrayContainer(ac.arr, ac.index+1));
            }
        }

        return result;
    }


    public static double computeMedian(ArrayList<Integer> inputArray) {
        //TODO: implement your function that computes median of values of an array
        int arraySize = inputArray.size();
        int midpoint = arraySize / 2;
        return inputArray.get(midpoint);
    }

}

class ArrayContainer implements Comparable<ArrayContainer> {
    ArrayList<Integer> arr;
    int index;

    public ArrayContainer(ArrayList<Integer> arr, int index) {
        this.arr = arr;
        this.index = index;
    }

    @Override
    public int compareTo(ArrayContainer o) {
        return this.arr.get(this.index) - o.arr.get(o.index);
    }
}

// extend Thread
class MedianMultiThread extends Thread {
    private ArrayList<Integer> list;

    public ArrayList<Integer> getInternal() {
        return list;
    }

    MedianMultiThread(ArrayList<Integer> array) {
        list = array;
    }

    public void run() {
        // called by object.start()
        mergeSort(list);
    }

    public static void main(String[] args) {
        ArrayList<Integer> input = new ArrayList<>(Arrays.asList(5,1,2,8,9,3,4,6,2,7,6,3,9,0));
        MedianMultiThread medianMultiThread = new MedianMultiThread(input);
        System.out.println("mergeSort: " + medianMultiThread.mergeSort(input));
    }

    // TODO: implement merge sort here, recursive algorithm
    private ArrayList<Integer> mergeSort (ArrayList<Integer> list) {

        ArrayList<Integer> firstHalf = new ArrayList<>();
        ArrayList<Integer> secondHalf = new ArrayList<>();

        int midpoint;

        if (list.size() == 1) {
            return list;
        } else {
            midpoint = list.size()/2;
            for (int i = 0; i < midpoint; i++) {
                firstHalf.add(list.get(i));
            }
            for (int i = midpoint; i < list.size(); i++) {
                secondHalf.add(list.get(i));
            }

            firstHalf = mergeSort(firstHalf);
            secondHalf = mergeSort(secondHalf);

            merge(firstHalf,secondHalf,list);
        }
        return list;
    }

    private void merge(ArrayList<Integer> firstHalf, ArrayList<Integer> secondHalf, ArrayList<Integer> list) {
        int firstIndex = 0;
        int secondIndex = 0;
        int listIndex = 0;

        while (firstIndex < firstHalf.size() && secondIndex < secondHalf.size()) {
            if (firstHalf.get(firstIndex) < secondHalf.get(secondIndex)) {
                list.set(listIndex,firstHalf.get(firstIndex));
                firstIndex++;
            } else {
                list.set(listIndex,secondHalf.get(secondIndex));
                secondIndex++;
            }
            listIndex++;
        }

        while (firstIndex < firstHalf.size()) {
            list.set(listIndex,firstHalf.get(firstIndex));
            firstIndex++;
            listIndex++;
        }

        while (secondIndex < secondHalf.size()) {
            list.set(listIndex,secondHalf.get(secondIndex));
            secondIndex++;
            listIndex++;
        }
    }
}



