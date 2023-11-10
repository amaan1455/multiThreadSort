import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ParallelMergeSort {
    public static void main(String[] args) {
        long startTime;
        long endTime;
        int flag=args.length>=2?Integer.parseInt(args[1]):1;
        int size=args.length>=1?Integer.parseInt(args[0]):5000000;
        int[] largeArray1 = new int[size];
        int[] largeArray2 = new int[size];
        for(int i = 0; i<largeArray1.length; i++){
            largeArray1[i] = largeArray2[i] = (int)(Math.random()*1000000);
        }
        startTime = System.currentTimeMillis();
        parallelMergeSort(largeArray1,flag);
        endTime = System.currentTimeMillis();
        long tP=endTime - startTime;
        if(tP==0)tP=1;
        System.out.println("Parallel: " + (endTime-startTime) + " ms");
        startTime = System.currentTimeMillis();
        MergeSort.mergeSort(largeArray2);
        endTime = System.currentTimeMillis();
        long tNP=endTime - startTime;
        if(tNP==0)tNP=1;
        System.out.println("Not Parallel: " + (endTime-startTime) + " ms");
        System.out.print(tNP/tP);
    }

    public static void parallelMergeSort(int[] array,int flag) {
        SortTask mainTask = new SortTask(array,flag);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(mainTask);
    }

    private static class SortTask extends RecursiveAction {
        private int[] array;
        private int flag;

        public SortTask(int[] array,int flag) {
            this.array = array;
            this.flag=flag;
        }
        @Override
        protected void compute() {
            
            if(array.length > 1) {
                if(array.length<1000000 && flag==1){
                    MergeSort.mergeSort(array);
                    return;
                }
                int mid = array.length/2;

                // Obtain the first half
                int[] firstHalf = new int[mid];
                System.arraycopy(array, 0, firstHalf, 0, mid);

                // Obtain the second half
                int[] secondHalf = new int[array.length - mid];
                System.arraycopy(array, mid, secondHalf, 0, array.length - mid);

                // Recursively sort the two halves
                SortTask firstHalfTask = new SortTask(firstHalf,flag);
                SortTask secondHalfTask = new SortTask(secondHalf,flag);
                // Invoke declared tasks
                invokeAll(firstHalfTask, secondHalfTask);

                //Merge firstHalf with secondHalf into our array
                MergeSort.merge(firstHalf, secondHalf, array);
            }
        }
    }
}