package mines;

import main.Tester;

public class MinesTest {

	private Tester t;
	

	private void testMines() {
		Class<?> c = Mines.class;
		t.new OneTest(c, 10, "4X4 board, mine at (0,0)") {
			@Override
			public void test() {
				Mines m = new Mines(4, 4, 0);
				m.addMine(0, 0);
				checkEqStr(m, "....\n....\n....\n....\n", "clear board");
				check(!m.isDone(), "Should not win yet!");
				m.open(3, 3);
				checkEqStr(m, ".1  \n11  \n    \n    \n", "after open(3,3)");
				check(m.isDone(), "Should win after open(3,3).");	
			}
		};

		t.new OneTest(c, 10, "3X2 board, mine at (2, 1)") {
			@Override
			public void test() {
				Mines m = new Mines(3, 2, 0);
				m.addMine(2, 1);
				checkEqStr(m, "..\n..\n..\n", "clear board");
				check(m.open(0, 1), "open (0,1)");
				checkEqStr(m, "  \n11\n..\n", "after open(0,1)");
				check(!m.open(2, 1), "open (2,1)");
				checkEqStr(m, "  \n11\n..\n", "after open(0,1), open(2,1)");
				m.setShowAll(true);
			}
		};

		
		t.new OneTest(c, 10, "4X4 board, mine at (1,1), (2,3), (3,3)") {
			@Override
			public void test() {
				Mines m = new Mines(4, 4, 0);
				m.addMine(1, 1);
				m.addMine(2, 3);
				m.addMine(3, 3);
				check(!m.isDone(), "Should not win yet!");
				m.open(2, 2);
				checkEqStr(m, "....\n....\n..3.\n....\n", "after open(2,2)");
				m.open(3, 0);
				checkEqStr(m, "....\n....\n113.\n  2.\n", "after open(2,2), open(3,0)");
				m.toggleFlag(3, 3);
				checkEqStr(m, "....\n....\n113.\n  2F\n", "after open(2,2), open(3,0), toggleFlag(3,3)");
				m.toggleFlag(3, 3);
				checkEqStr(m, "....\n....\n113.\n  2.\n", "after open(2,2), open(3,0), toggleFlag(3,3) X 2");
				m.setShowAll(true);
				checkEqStr(m, "111 \n1X21\n113X\n  2X\n", "after open(2,2), open(3,0), setShowAll(true)");
				m.setShowAll(false);
				m.open(1,2);
				checkEqStr(m, "....\n..2.\n113.\n  2.\n", "after open(2,2), open(3,0), "
						+ "setShowAll(true), open(1,2), setShowAll(false)");				
				check(!m.isDone(), "should not win yet.");
			}
		};
		
		
	}


		
	// -------------------------------------------------------------------------------
	// main

	public Tester actualTest() {
		t = new Tester();
		testMines();
		return t;
	}

	public static Tester test() {
		return new MinesTest().actualTest();
	}

	public static void main(String[] args) {
		Tester.ignoreOutput();
		Tester t = test();
		Tester.restoreOutput();
		t.printAllGood("MinesTest");
	}
}
