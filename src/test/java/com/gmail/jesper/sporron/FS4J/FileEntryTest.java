package com.gmail.jesper.sporron.FS4J;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.gmail.jesper.sporron.FS4J.util.FileEntry;

class FileEntryTest {
	private FileEntry entry;

	@BeforeEach
	void setUp() throws Exception {
		entry = new FileEntry("entry");
	}

	@Test
	void testStartsWith() {
		assertTrue(entry.startsWith("e"));
		assertTrue(entry.startsWith("ent"));
		assertTrue(entry.startsWith("entry"));
		assertFalse(entry.startsWith("try"));
		assertFalse(entry.startsWith("y"));
		assertFalse(entry.startsWith(" entry")); // space
		assertFalse(entry.startsWith("entry ")); // space

		assertThrows(NullPointerException.class, () -> entry.startsWith(null));
	}

	@Test
	void testEndsWith() {
		assertTrue(entry.endsWith("y"));
		assertTrue(entry.endsWith("try"));
		assertTrue(entry.endsWith("entry"));
		assertFalse(entry.endsWith("e"));
		assertFalse(entry.endsWith("ent"));
		assertFalse(entry.endsWith(" entry")); // space
		assertFalse(entry.endsWith("entry ")); // space

		assertThrows(NullPointerException.class, () -> entry.endsWith(null));
	}

	@Test
	void testContains() {
		assertTrue(entry.contains("y"));
		assertTrue(entry.contains("try"));
		assertTrue(entry.contains("entry"));
		assertTrue(entry.contains("e"));
		assertTrue(entry.contains("ent"));
		assertFalse(entry.contains(" entry")); // space
		assertFalse(entry.contains("entry ")); // space

		assertThrows(NullPointerException.class, () -> entry.contains(null));
	}

	@Test
	void testFromStringString() {
		final FileEntry[] entries = FileEntry.from("entry#another#three", "#");
		assertEquals(3, entries.length);
		assertEquals(entry, entries[0]);
		assertEquals(new FileEntry("another"), entries[1]);
		assertEquals(new FileEntry("three"), entries[2]);

		assertThrows(NullPointerException.class, () -> FileEntry.from(null, "/"));
		assertThrows(NullPointerException.class, () -> FileEntry.from("something", null));
	}

	@Test
	void testFromString() {
		final FileEntry[] entries = FileEntry.from("entry/another/three");
		assertEquals(3, entries.length);
		assertEquals(entry, entries[0]);
		assertEquals(new FileEntry("another"), entries[1]);
		assertEquals(new FileEntry("three"), entries[2]);

		final FileEntry[] empty = FileEntry.from("");
		assertEquals(0, empty.length);

		assertThrows(NullPointerException.class, () -> FileEntry.from(null));
	}

	@Test
	void testJoinFileEntryArray() {
		final String str = "entry/another/three";
		final FileEntry[] entries = FileEntry.from(str);
		assertEquals(str, FileEntry.join(entries));

		assertThrows(NullPointerException.class, () -> FileEntry.join(null));
	}

	@Test
	void testJoinFileEntryArrayString() {
		final String str = "entry#another#three";
		final String divider = "#";
		final FileEntry[] entries = FileEntry.from(str, divider);
		assertEquals(str, FileEntry.join(entries, divider));

		assertThrows(NullPointerException.class, () -> FileEntry.join(null, "/"));
		assertThrows(NullPointerException.class, () -> FileEntry.join(new FileEntry[0], null));
	}
}
