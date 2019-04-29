package com.gmail.jesper.sporron.FS4J;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.gmail.jesper.sporron.FS4J.util.FileEntry;
import com.gmail.jesper.sporron.FS4J.util.FilePath;

class FilePathTest {
	private FilePath path1;

	@BeforeEach
	void setUp() {
		path1 = FilePath.from("welcome/to/the/moon/friend");
	}

	@Test
	void testContainsEntry() {
		final FileEntry[] full = FileEntry.from("welcome/to/the/moon/friend");
		Stream.of(full).forEach(fe -> assertTrue(path1.containsEntry(fe)));

		final FileEntry[] notContained = FileEntry.from("welc/tot/thetha/m0n/friend_");
		Stream.of(notContained).forEach(fe -> assertFalse(path1.containsEntry(fe)));

		assertThrows(NullPointerException.class, () -> path1.containsEntry(null));
	}

	@Test
	void testContainsText() {
		final String[] contained = { "welcome", "welcome/to/t", "/to/the/mo",
				"welcome/to/the/moon/friend" };
		Stream.of(contained).forEach(str -> assertTrue(path1.containsText(str)));

		final String[] notContained = { "dne", "42", "\\to\\the\\moon",
				"/welcome / to the moon / fr i e n..d aad." };
		Stream.of(notContained).forEach(str -> assertFalse(path1.containsText(str)));

		assertThrows(NullPointerException.class, () -> path1.containsText(null));
	}

	@Test
	void testPrependFileEntry() {
		final FileEntry prepend = new FileEntry("prepended");
		final FilePath prePath = path1.prepend(prepend);

		assertTrue(prePath.containsEntry(prepend));
		assertTrue(prePath.containsText(prepend.toString()));

		assertEquals(prePath.subpath(0, 1).toString(), prepend.toString());
		assertEquals(prePath.toString(), prepend.toString() + "/" + path1.toString());

		assertThrows(NullPointerException.class, () -> path1.prepend((FileEntry) null));
	}

	@Test
	void testPrependFilePath() {
		final FilePath prepend = FilePath.from("prepend/entire/path");
		final FilePath prePath = path1.prepend(prepend);

		assertTrue(prePath.containsText(prepend.toString()));

		assertEquals(prePath.subpath(0, prepend.numEntries()).toString(), prepend.toString());
		assertEquals(prePath.toString(), prepend.toString() + "/" + path1.toString());

		assertThrows(NullPointerException.class, () -> path1.prepend((FilePath) null));
	}

	@Test
	void testAppendFileEntry() {
		final FileEntry append = new FileEntry("appended");
		final FilePath prePath = path1.append(append);

		assertTrue(prePath.containsEntry(append));
		assertTrue(prePath.containsText(append.toString()));

		assertEquals(prePath.subpath(prePath.numEntries() - 1, prePath.numEntries()).toString(),
				append.toString());
		assertEquals(prePath.toString(), path1.toString() + "/" + append.toString());

		assertThrows(NullPointerException.class, () -> path1.append((FileEntry) null));
	}

	@Test
	void testAppendFilePath() {
		final FilePath append = FilePath.from("append/entire/path");
		final FilePath prePath = path1.append(append);

		assertTrue(prePath.containsText(append.toString()));

		assertEquals(
				prePath.subpath(prePath.numEntries() - append.numEntries(), prePath.numEntries())
						.toString(),
				append.toString());
		assertEquals(prePath.toString(), path1.toString() + "/" + append.toString());

		assertThrows(NullPointerException.class, () -> path1.append((FilePath) null));
	}

	@Test
	@SuppressWarnings("null")
	void testSubpath() {
		assertEquals(FilePath.from("welcome/to/the"), path1.subpath(0, 3));
		assertEquals(FilePath.from(""), path1.subpath(0, 0));
		assertEquals(FilePath.from(""), path1.subpath(path1.numEntries(), path1.numEntries()));

		assertEquals("start must be >= 0",
				assertThrows(IllegalArgumentException.class, () -> path1.subpath(-2, 0))
						.getMessage());
		assertEquals("end must be >= 0",
				assertThrows(IllegalArgumentException.class, () -> path1.subpath(0, -2))
						.getMessage());
		assertEquals("start must be <= end",
				assertThrows(IllegalArgumentException.class, () -> path1.subpath(3, 1))
						.getMessage());

		assertEquals(path1, path1.subpath(0, path1.numEntries()));
	}

	@Test
	void testMinimize() {
		final FilePath notMini = FilePath.from("not/././minimized/../path");
		final FilePath mini = notMini.minimize();

		assertTrue(mini.isMinimized());

		final FilePath expected = FilePath.from("not/path");
		assertEquals(expected, mini);

		final FilePath shouldNotMini = FilePath.from("should/not/mini");
		assertTrue(shouldNotMini.isMinimized());
		assertEquals(shouldNotMini, shouldNotMini.minimize());

		final FilePath shouldNotMini2 = FilePath.from("../should/not/mini");
		assertTrue(shouldNotMini2.isMinimized());
		assertEquals(shouldNotMini2, shouldNotMini2.minimize());
	}

	@Test
	void testIsMinimized() {
		assertTrue(path1.isMinimized());
		assertTrue(FilePath.from("./minimized/for/sure").isMinimized());
		assertTrue(FilePath.from("../minimized/this/is").isMinimized());
		assertFalse(FilePath.from("/./not/minimized").isMinimized());
		assertFalse(FilePath.from("this/./is/../././not/minimized").isMinimized());
	}

	@Test
	void testToString() {
		assertEquals("welcome/to/the/moon/friend", path1.toString());
	}

	@Test
	void testToStringInt() {
		assertEquals("welcome/to/the/moon/friend", path1.toString(path1.numEntries()));
		assertEquals("welcome/to/the/moon/friend", path1.toString(Integer.MAX_VALUE));
		assertEquals("welcome/to", path1.toString(2));
		assertEquals("", path1.toString(0));
		assertThrows(IllegalArgumentException.class, () -> path1.toString(-1));
	}

	@Test
	void testIterator() {
		final Iterator<FileEntry> it = path1.iterator();
		assertEquals(new FileEntry("welcome"), it.next());
		assertEquals(new FileEntry("to"), it.next());
		assertEquals(new FileEntry("the"), it.next());
		assertEquals(new FileEntry("moon"), it.next());
		assertEquals(new FileEntry("friend"), it.next());
		assertFalse(it.hasNext());
	}

	@Test
	void testFromStringString() {
		final FilePath hashSep = FilePath.from("welcome#to#the#moon#friend", "#");
		assertNotNull(hashSep);
		assertEquals(hashSep, path1);
	}

	@Test
	void testFromString() {
		assertNotNull(path1);
		assertEquals("welcome/to/the/moon/friend", path1.toString());
	}
}
