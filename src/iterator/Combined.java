package iterator;

import java.util.Iterator;

public class Combined<E> implements Iterable<E> {
    Iterator<E> itrOne, itrTwo; //lets declare an iterator's

    public Combined(Iterable<E> first, Iterable<E> second) {
        itrOne = first.iterator(); //saving them
        itrTwo = second.iterator();
    }

    /* nested class of "combined iterator" */
    private class CombinedIterator implements Iterator<E> {
        private int f = 0; //first counter
        private int s = 0; //second counter

        @Override
        public boolean hasNext() {
            return itrOne.hasNext() || itrTwo.hasNext();
        }

        @Override
        public E next() {
            if (f == s) { //if true: first must be next
                if (itrOne.hasNext()) { //but check if he even exist
                    f++;
                    return itrOne.next();
                } else if (itrTwo.hasNext()) {
                    s++;
                    return itrTwo.next();
                }
            } else { //if was false: then second must be next
                if (itrTwo.hasNext()) { //but, again, check if exist
                    s++;
                    return itrTwo.next();
                } else if (itrOne.hasNext()) {
                    f++;
                    return itrOne.next();
                }
            }
            return null;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new CombinedIterator();
    }
}
