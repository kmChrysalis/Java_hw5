package main;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Tester {

	public static final String PUBLISHED = "From published test file";
	public static boolean forStudents = true;
	private static PrintStream stdout;

	static byte[] stdinBuf = null;
	static int stdinIndex = 0;

	static {
		InputStream inputStream = new InputStream() {
			@Override
			public int read() {
				if (stdinIndex >= stdinBuf.length)
					return -1;

				return stdinBuf[stdinIndex++];
			}
		};
		System.setIn(inputStream);
	}

	public static void setInput(String str) {
		stdinBuf = (str + "\n").getBytes(Charset.forName("UTF-8"));
		stdinIndex = 0;
	}

	public interface Thrower {
		public void run() throws Exception;
	}

	// -----------------------------------------------------------------
	// General Testing stuff

	private boolean allGood = true;
	private int totalMinus = 0;
	private StringBuilder b = null;
	private int maxMinus;

	public Tester(int totalPoints) {
		if (!forStudents)
			b = new StringBuilder();
		this.maxMinus = totalPoints;
	}

	public Tester() {
		this(100);
	}
	
	public boolean allGood() {
		return allGood;
	}

	public int totalMinus() {
		if (forStudents)
			throw new UnsupportedOperationException(
					"Using totalMinus when for students");
		return Math.min(maxMinus, totalMinus);
	}

	@Override
	public String toString() {
		if (forStudents)
			throw new UnsupportedOperationException(
					"Using StringBuilder when for students");
		return b.toString();
	}

	private static boolean emptyStr(String str) {
		return str == null || str.equals("");
	}

	public static void ignoreOutput() {
		stdout = System.out;
		System.setOut(new PrintStream(new OutputStream() {
			public void write(int b) {
			}
		}));
	}

	public static void restoreOutput() {
		System.setOut(stdout);
	}

	public void printAllGood(String name) {
		if (!forStudents)
			throw new UnsupportedOperationException(
					"Using printAllGood when not forStudents");

		if (allGood)
			System.out.println(name + " Test passed!");
		else {
			System.err.println("-------------------------------------");
			System.err.println(name + " Tests failed, see above.");
		}
	}

	void error(Class<?> c, int minus, String message) {
		allGood = false;
		if (forStudents) {
			System.err.print(c == null ? "Problem" : c.getName());
			if (!emptyStr(message))
				System.err.println(": " + message);
		} else {
			if (b.length() > 0)
				b.append("\n");
			b.append(c == null ? "Problem" : c.getName());
			b.append("(-" + minus + ")");
			if (!emptyStr(message))
				b.append(": " + message);
			totalMinus += minus;
		}
	}

	public void checkSuper(Class<?> sub, Class<?> sup) {
		if (!sub.getSuperclass().equals(sup))
			error(sub, 1, " should be a direct subclass of " + sup.getName());
	}

	public void checkImplements(Class<?> sub, Class<?> sup) {
		for (Class<?> x : sub.getInterfaces()) {
			if (x.equals(sup))
				return;
		}
		error(sub, 1, " should implement " + sup.getName());
	}

	// Checks public private protected stuff.
	public class AccessChecker {
		Set<String> pubM = new HashSet<>();
		Set<String> proM = new HashSet<>();
		Set<String> defM = new HashSet<>();
		Set<String> abs = new HashSet<>();

		Set<String> pubF = new HashSet<>();
		Set<String> proF = new HashSet<>();
		Set<String> defF = new HashSet<>();

		Set<String> pub, pro, def;
		Class<?> c;

		public AccessChecker(Class<?> c) {
			this.c = c;
		}
		public AccessChecker pM(String name) {
			pubM.add(name);
			return this;
		}
		public AccessChecker tM(String name) {
			proM.add(name);
			return this;
		}
		public AccessChecker abs(String name) {
			abs.add(name);
			return this;
		}
		public AccessChecker dM(String name) {
			defM.add(name);
			return this;
		}
		public AccessChecker pF(String name) {
			pubF.add(name);
			return this;
		}
		public AccessChecker tF(String name) {
			proF.add(name);
			return this;
		}
		public AccessChecker dF(String name) {
			defF.add(name);
			return this;
		}

		private void check(Collection<String> allowable, String methodName,
				String msg) {
			if (!allowable.contains(methodName))
				error(c, 1, methodName + ": " + msg);
		}

		private void checkAccess(String name, int m) {
			if (Modifier.isPrivate(m))
				return;
			if (Modifier.isProtected(m))
				check(pro, name, "should not be protected");
			else if (Modifier.isPublic(m))
				check(pub, name, "should not be public");
			else
				check(def, name, "should not be package-private (default)");
			if (abs.contains(name) && !Modifier.isAbstract(m))
				error(c, 1, name + " should be abstract. ");
		}

		public void checkAll() {
			pub = pubM;
			def = defM;
			pro = proM;
			for (Method m : c.getDeclaredMethods())
				checkAccess(m.getName(), m.getModifiers());
			pub = pubF;
			def = defF;
			pro = proF;
			for (Field f : c.getDeclaredFields())
				checkAccess(f.getName(), f.getModifiers());
		}
	}

	public abstract class OneTest implements Runnable {
		private int minus;
		private StringBuilder outMessage = new StringBuilder();
		private Class<?> currentClass;
		private boolean passed = true;

		public boolean passed() {
			return passed;
		}

		public OneTest(Class<?> currentClass, int minus, String testDesc) {
			this.minus = minus;
			if (!emptyStr(testDesc)) {
				outMessage.append(testDesc);
				outMessage.append(':');
			}
			this.currentClass = currentClass;
			runTest();
		}

		public void error(String message) {
			passed = false;
			if (!emptyStr(message)) {
				outMessage.append("\n   ");
				outMessage.append(message);
			}
		}

		public boolean check(boolean cond, String message) {
			if (!cond) {
				error(message);
				return false;
			}
			return true;
		}

		public boolean checkEq(Object a, Object b, String message) {
			return check(a == b || (a != null && a.equals(b)),
					String.format("%sExpected '%s' but got '%s'",
							emptyStr(message) ? "" : message + ": ", b, a));
		}

		public boolean checkEqStr(Object a, Object b, String message) {
			return checkEq(a == null ? null : a.toString(),
					b == null ? null : b.toString(), message);
		}

		public boolean checkEqDouble(double a, double b, String message) {
			return check(Math.abs(a - b) < 0.0001,
					String.format("%sExpected '%.4f' but got '%.4f'",
							emptyStr(message) ? "" : message + ": ", b, a));
		}

		public boolean checkThrows(Thrower runnable,
				Class<? extends Exception> ec, String message) {
			try {
				runnable.run();
			} catch (Exception c) {
				if (ec.isAssignableFrom(c.getClass()))
					return true;
				error(message + ", should throw " + ec.getName()
						+ ", but instead is throwing "
						+ c.getClass().getName());
				return false;
			}
			error(message + ", should throw exception " + ec.getName());
			return false;
		}

		public abstract void test() throws Exception;

		public void run() {
			try {
				test();
			} catch (Exception e) {
				error(e.toString());
			} catch (StackOverflowError e) {
				error(e.toString() + " : probably infinite recursion.");
			}
		}

		@SuppressWarnings("deprecation")
		public void runTest() {
			if (!forStudents) {
				Thread t = new Thread(this);
				t.start();
				try {
					t.join(5000);
				} catch (InterruptedException e) {
				}
				if (t.isAlive()) {
					t.stop();
					error("stuck!");
				}
			} else
				try {
					test();
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			if (!passed)
				Tester.this.error(currentClass, minus, outMessage.toString());
		}
	}

	// ------------------------------------------------------------------
	// Zip stuff

	public static void zipIt(String zipFileName) throws IOException {
		String sourceFile = "src";
		FileOutputStream fos = new FileOutputStream(zipFileName);
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		File fileToZip = new File(sourceFile);

		zipFile(fileToZip, fileToZip.getName(), zipOut);
		zipOut.close();
		fos.close();
	}

	private static void zipFile(File fileToZip, String fileName,
			ZipOutputStream zipOut) throws IOException {
		if (fileToZip.isHidden()) {
			return;
		}
		if (fileToZip.isDirectory()) {
			if (fileName.endsWith("/")) {
				zipOut.putNextEntry(new ZipEntry(fileName));
				zipOut.closeEntry();
			} else {
				zipOut.putNextEntry(new ZipEntry(fileName + "/"));
				zipOut.closeEntry();
			}
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				zipFile(childFile, fileName + "/" + childFile.getName(),
						zipOut);
			}
			return;
		}
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		fis.close();
	}

	private static void testExDetails() {
		ExDetails s1 = ExDetails.firstStudent();
		ExDetails s2 = ExDetails.secondStudent();

		System.out.println("Check your personal details are correct here:");
		System.out.println(s1);
		System.out.println(s2);
		String zipName = String.format("%s_%s.zip", s1.getId(), s2.getId());
		System.out.println("--------------------------------------");
		try {
			zipIt(zipName);
			System.out.println("Your zip file was created automatically! "
					+ "you can find it in the project directory");
			System.out.println("its name is " + zipName);
		} catch (IOException e) {
			System.err.println("Problem creating zip file! " + e);
		}
	}

	// -------------------------------------------------------------------------------
	// output stuff

	public static void outputCSV(List<Tester> testers) {
		ExDetails s1 = ExDetails.firstStudent();
		ExDetails s2 = ExDetails.secondStudent();

		System.out.format("%d, %d, \"%s\", \"%s\", %s, %s, ", s1.getId(),
				s2.getId(), s1.getFirstName() + " " + s1.getLastName(),
				s2.getFirstName() + " " + s2.getLastName(), s1.geteMail(),
				s2.geteMail());
		int score = 100;
		StringBuilder message = new StringBuilder();
		for (Tester t : testers) {
			score -= t.totalMinus;
			String errors = t.toString();
			if (message.length() > 0 && errors.length() > 0)
				message.append('\n');
			message.append(t);
		}
		System.out.println(score + ", \"" + message + "\"");
	}

	// -------------------------------------------------------------------------------
	// main

	public static void main(String[] args) {

		if (args != null && args.length > 0)
			forStudents = false;

		if (!Tester.class.getPackage().getName().equals("main")) {
			System.err.println("Tester is not in the the correct package!");
			return;
		}

		List<Tester> testers = actualTests();

		if (forStudents) {
			for (Tester t : testers)
				if (!t.allGood) {
					System.err
							.println("--------------------------------------");
					System.err.println("SEE PROBLEMS ABOVE");
					return;
				}
			System.out.println("ALL BASIC TESTS PASSED");
			System.out.println("--------------------------------------");
			testExDetails();
		} else
			outputCSV(testers);
	}

	// Change here if you don't want to run some tests.
	private static List<Tester> actualTests() {

		ignoreOutput();

		List<Tester> testers = new ArrayList<Tester>();
		testers.add(iterator.IteratorTest.test());
		testers.add(mines.MinesTest.test());

		
		restoreOutput();

		return testers;
	}
}
