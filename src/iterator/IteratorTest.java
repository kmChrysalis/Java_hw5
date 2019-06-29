package iterator;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import main.Tester;

public class IteratorTest {

	private Tester t;
	
	private <E> String iterableToString(Iterable<E> it) {
		StringBuilder b = new StringBuilder();
		for (E e : it)
			b.append(e + " ");
		return b.toString();
	}

	private void testTwoArrays() {
		Class<?> c = TwoArrays.class;
		t.new OneTest(c, 10, Tester.PUBLISHED) {
			@Override
			public void test() {
				int[] a1 = { 1, 2, 3, 4 };
				int[] a2 = { 100, 101, 102, 103, 104, 105, 106 };
				
				TwoArrays aa = new TwoArrays(a1, a2);
				checkEq(iterableToString(aa), "1 100 2 101 3 102 4 103 104 105 106 ", null);
			}
		};
	}

	private void testCombined() {
		Class<?> c = TwoArrays.class;
		t.new OneTest(c, 10, Tester.PUBLISHED) {
			@Override
			public void test() {
				List<String> list1 = Arrays.asList("one", "two", "three");
				List<String> list2 = Arrays.asList("A", "B", "C", "D", "E");
				
				Combined<String> c = new Combined<>(list2, list1);
				checkEq(iterableToString(c), "A one B two C three D E ", null);
			}
		};
		
		t.new OneTest(c, 10, Tester.PUBLISHED) {
			@Override
			public void test() {
				List<Integer> list1 = Arrays.asList(10, 20, 30);
				class InfiniteIterable implements Iterable<Integer> {
					@Override
					public Iterator<Integer> iterator() {
						return new Iterator<Integer>() {
							private int i = 0;
							@Override
							public boolean hasNext() {
								return true;
							}
							@Override
							public Integer next() {
								int res = i;
								i = (i + 1) % 5;
								return res;
							}
						};
					}
				}
				Combined<Integer> c = new Combined<Integer>(list1, new InfiniteIterable());
				Iterator<Integer> it = c.iterator();
				checkEq(it.next(), 10, "first next()");
				checkEq(it.next(), 0, "second next()");
				checkEq(it.next(), 20, "third next()");
				checkEq(it.next(), 1, "second next()");
			}
		};
		
	}

	
	
	// -------------------------------------------------------------------------------
	// main

	public Tester actualTest() {
		t = new Tester();
		testTwoArrays();
		testCombined();
		return t;
	}

	public static Tester test() {
		return new IteratorTest().actualTest();
	}

	public static void main(String[] args) {
		Tester.ignoreOutput();
		Tester t = test();
		Tester.restoreOutput();
		t.printAllGood("IteratorTest");
	}
}
