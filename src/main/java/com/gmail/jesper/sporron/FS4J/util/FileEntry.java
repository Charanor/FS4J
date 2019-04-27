package com.gmail.jesper.sporron.FS4J.util;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/** Represents one part of a full file path. E.g. the path "music/dungeon/dungeon_dark.mp3" is
 * composed of three file entries: "music", "dungeon", and "dungeon_dark.mp3".
 *
 * @author Jesper Sporron */
public class FileEntry {
	private final String entry;

	/** Constructs a new FileEntry from the string. You should only use the constructor if you know
	 * <b>for a fact</b> that the entry is valid (i.e. not something like "?}?hello////.../world").
	 * If you are not sure, instead use {@link FileEntry#from(String)}.
	 *
	 * @param entry
	 *            the string entry
	 * @throws NullPointerException
	 *             if entry is null
	 * @see {@link FileEntry#from(String)} */
	public FileEntry(final String entry) throws NullPointerException {
		this.entry = requireNonNull(entry, "entry must not be null");
	}

	/** Checks if this file entry starts with some text.
	 *
	 * @param prefix
	 * @return true if this entry starts with prefix, false otherwise
	 * @see String#startsWith(String) */
	public boolean startsWith(final String prefix) {
		requireNonNull(prefix, "prefix must not be null");
		return entry.startsWith(prefix);
	}

	/** Checks if this file entry ends with some text.
	 *
	 * @param suffix
	 * @return true if this entry ends with suffix, false otherwise
	 * @see String#endsWith(String) */
	public boolean endsWith(final String suffix) {
		requireNonNull(suffix, "suffix must not be null");
		return entry.endsWith(suffix);
	}

	/** Checks if this file entry contains some text.
	 *
	 * @param s
	 * @return true if this entry contains s, false otherwise
	 * @see String#contains(CharSequence) */
	public boolean contains(final CharSequence s) {
		requireNonNull(s, "s must not be null");
		return entry.contains(s);
	}

	@Override
	public String toString() {
		return entry;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + entry.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final FileEntry other = (FileEntry) obj;
		if (!entry.equals(other.entry)) return false;
		return true;
	}

	/** Checks if the text representation of this FileEntry is equal to the string.
	 *
	 * @param str
	 * @return true if and only if the internal string representation of this FileEntry is equal to
	 *         str, false otherwise. */
	public boolean equals(final String str) {
		return entry == str || entry.equals(str);
	}

	/** Creates a {@link FileEntry} array from the given string and divider. E.g:
	 *
	 * <code>
	 * <pre>
	 * >> FileEntry[] entries = FileEntry.from("hello/world/some/folder", "/");
	 * >> Arrays.toString(entries);
	 * hello, world, some, folder
	 * </pre>
	 * </code>
	 *
	 * <ul>
	 * <li>If <code>path</code> is empty, the array will be empty.</li>
	 * <li>If <code>divider</code> is empty, the array will contain a singular entry equal to
	 * <code>path</code></li>
	 * </ul>
	 *
	 * @param path
	 *            the string to split
	 * @param divider
	 *            the divider to split by
	 * @return an array of file entries
	 * @throws NullPointerException
	 *             if <code>path</code> or <code>divider</code> is <code>null</code> */
	public static final FileEntry[] from(final String path, final String divider)
			throws NullPointerException {
		requireNonNull(path, "path must not be null");
		requireNonNull(divider, "divider must not be null");
		if (path.length() == 0) return new FileEntry[0];
		if (divider.length() == 0) return new FileEntry[] { new FileEntry(path) };

		final String[] split = path.split(Pattern.quote(divider));
		final FileEntry[] result = Stream.of(split).map(FileEntry::new).toArray(FileEntry[]::new);
		return result;
	}

	/** Convenience method for calling <code>FileEntry.from(path, "/")</code>.
	 *
	 * @param path
	 *            the path
	 * @return an array of file entries
	 * @throws NullPointerException
	 *             if <code>path</code> or <code>divider</code> is <code>null</code>
	 * @see FileEntry#from(String, String) */
	public static final FileEntry[] from(final String path) throws NullPointerException {
		return from(path, "/");
	}

	/** Convenience method for calling <code>FileEntry.join(entries, "/")</code>.
	 *
	 * @param entries
	 *            the entries to join
	 * @return the joined entries
	 * @throws NullPointerException
	 *             if <code>entries</code> is <code>null</code>
	 * @see FileEntry#join(FileEntry[], String) */
	public static final String join(final FileEntry[] entries) throws NullPointerException {
		return join(entries, "/");
	}

	/** Joins the entries in the array to form a nice string representation. E.g.
	 *
	 * <code>
	 * <pre>
	 * >> FileEntry[] entries = FileEntry.from("hello/world/some/folder", "/");
	 * >> FileEntry.join(entries, "##");
	 * hello##world##some##folder
	 * </pre>
	 * </code>
	 *
	 * If entries is empty this method will return an empty string.
	 *
	 * @param entries
	 *            the entries to join
	 * @param divider
	 *            how to separate the entries
	 * @return the joined entries
	 * @throws NullPointerException
	 *             if <code>entries</code> is <code>null</code> */
	public static final String join(final FileEntry[] entries, final String divider)
			throws NullPointerException {
		requireNonNull(entries, "entries must not be null");
		return String.join(divider, Stream.of(entries).filter(Objects::nonNull)
				.map(FileEntry::toString).toArray(String[]::new));
	}
}
