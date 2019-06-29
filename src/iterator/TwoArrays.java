package iterator;

import java.util.Iterator;

public class TwoArrays implements Iterable<Integer> {
    private Integer[] arr1; //declare about two arrays
    private Integer[] arr2;

    public TwoArrays(int[] a1, int[] a2) {
        /* create and fill first array */
        arr1 = new Integer[a1.length];
        for (int i = 0; i < a1.length; i++)
            arr1[i] = a1[i];
        /* create and fill second array */
        arr2 = new Integer[a2.length];
        for (int i = 0; i < a2.length; i++)
            arr2[i] = a2[i];
    }

    private class TwoArraysIterator implements Iterator<Integer> {
        int a1 = 0; //first array counter
        int a2 = 0; //second array counter

        @Override
        public boolean hasNext() {
            return a1 < arr1.length || a2 < arr2.length;
        }

        @Override
        public Integer next() {
            if (a1 == a2) //if true: arr1 first, false: arr2 first
                 return (a1 < arr1.length) ? arr1[a1++]:
                        (a2 < arr2.length) ? arr2[a2++]: null;
            else return (a2 < arr2.length) ? arr2[a2++]:
                        (a1 < arr1.length) ? arr1[a1++]: null;
        }

    }

    @Override
    public Iterator<Integer> iterator() {
        return new TwoArraysIterator();
    }
}
