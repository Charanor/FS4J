package com.gmail.jesper.sporron.FS4J.util;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Represents an immutable full file path, e.g. <code>"music/dungeon/dungeon_dark.mp"</code>.
 *
 * @author Jesper Sporron */
public class FilePath implements Iterable<FileEntry> {
	private final FileEntry[] entries;

	public FilePath(final FileEntry[] entries) {
		this.entries = requireNonNull(entries, "entries must not be null");
		Stream.of(entries).forEach(e -> requireNonNull(e, "entries in the array must not be null"));
	}

	/** Checks if this path contains the specified {@link FileEntry}.
	 *
	 * @param entry
	 *            the entry to check
	 * @return true if this path contains the entry, false otherwise */
	public boolean containsEntry(final FileEntry entry) {
		requireNonNull(entry, "entry must not be null");

		for (final FileEntry ent : entries)
			if (ent.equals(entry)) return true;
		return false;
	}

	/** Checks if this path contains some text.
	 *
	 * @param text
	 *            the text to check
	 * @return true if this path contains the text, false otherwise
	 * @see String#contains(CharSequence) */
	public boolean containsText(final String text) {
		requireNonNull(text, "text must not be null");
		return toString().contains(text);
	}

	/** Returns a new FilePath with the {@link FileEntry} prepended to this path.
	 *
	 * @param entry
	 *            the entry to append
	 * @return a new FilePath with the FileEntry prepended to this path. */
	public FilePath prepend(final FileEntry entry) {
		requireNonNull(entry, "entry must not be null");

		final FileEntry[] newEntries = new FileEntry[this.entries.length + 1];
		System.arraycopy(entries, 0, newEntries, 1, entries.length);
		newEntries[0] = entry;
		return new FilePath(newEntries);
	}

	/** Returns a new FilePath with the path prepended to this path.
	 *
	 * @param other
	 *            the FilePath to prepend
	 * @return a new FilePath with the path prepended to this path. */
	public FilePath prepend(final FilePath other) {
		requireNonNull(other, "other must not be null");
		return other.append(this);
	}

	/** Returns a new FilePath with the {@link FileEntry} appended to this path.
	 *
	 * @param entry
	 *            the entry to append
	 * @return a new FilePath with the FileEntry appended to this path. */
	public FilePath append(final FileEntry entry) {
		requireNonNull(entry, "entry must not be null");

		final FileEntry[] newEntries = new FileEntry[this.entries.length + 1];
		System.arraycopy(entries, 0, newEntries, 0, entries.length);
		newEntries[this.entries.length] = entry;
		return new FilePath(newEntries);
	}

	/** Returns a new FilePath with the path appended to this path.
	 *
	 * @param other
	 *            the FilePath to append
	 * @return a new FilePath with the path appended to this path. */
	public FilePath append(final FilePath other) {
		requireNonNull(other, "other must not be null");

		final FileEntry[] newEntries = new FileEntry[this.entries.length + other.entries.length];
		System.arraycopy(entries, 0, newEntries, 0, entries.length);
		System.arraycopy(other.entries, 0, newEntries, entries.length, other.entries.length);
		return new FilePath(newEntries);
	}

	/** returns a new FilePath split into a smaller path, starting at <code>start</code> (inclusive)
	 * and ending at <code>end</code> (exclusive). For example:
	 *
	 * <code>
	 * <pre>
	 * >> FilePath path = FilePath.from("some_0/really_1/long_2/path_3");
	 * >> FilePath sub = path.subpath(1, 3);
	 * >> System.out.println(sub.toString());
	 * really_1/long_2
	 * </pre>
	 * </code>
	 *
	 * @param start
	 *            where to start (inclusive)
	 * @param end
	 *            where to end (exclusive)
	 * @throws IllegalArgumentException
	 *             if {@code start} or {@code end} is < 0 OR if start > end.
	 * @return a new split FilePath */
	public FilePath subpath(final int start, final int end) throws IllegalArgumentException {
		if (start < 0) throw new IllegalArgumentException("start must be >= 0");
		if (end < 0) throw new IllegalArgumentException("end must be >= 0");
		if (start > end) throw new IllegalArgumentException("start must be <= end");

		final int length = Math.abs(end - start);
		final FileEntry[] newEntries = new FileEntry[length];
		System.arraycopy(entries, start, newEntries, 0, length);
		return new FilePath(newEntries);
	}

	/** Minimizes the file path, removing redundant entries such as <code>"path/.."</code> and
	 * <code>"path/./otherpath"</code>. For example:
	 *
	 * <code>
	 * <pre>
	 * >> FilePath path = FilePath.from("./p1/p2/../p3/./p4");
	 * >> FilePath mini = path.minimize();
	 * >> System.out.println(mini.toString());
	 * ./p1/p3/p4
	 * </pre>
	 * </code>
	 *
	 * @return minimized FilePath */
	public FilePath minimize() {
		String asString = toString();
		asString = asString.replaceAll("/\\./([^/]+)", "/$1"); // /./otherpath -> /otherpath
		asString = asString.replaceAll("[^/]+/\\.\\./?", ""); // path/.. -> <nothing>
		return FilePath.from(asString);
	}

	/** @return the number of file entries in this path. */
	public int numEntries() {
		return entries.length;
	}

	/** @see FileEntry#join(FileEntry[]) */
	@Override
	public String toString() {
		return FileEntry.join(entries);
	}

	/** Returns the string representation of this path, up to a certain amount of entries.
	 *
	 * @param numEntries
	 *            how many entries to include
	 * @throws IllegalArgumentException
	 *             if {@code numEntries} < 0 */
	public String toString(final int numEntries) {
		if (numEntries < 0) throw new IllegalArgumentException("numEntries must be >= 0");
		return Arrays.stream(entries).limit(numEntries).map(FileEntry::toString)
				.collect(Collectors.joining("/"));
	}

	@Override
	public Iterator<FileEntry> iterator() {
		return Arrays.stream(entries).iterator();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(entries);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final FilePath other = (FilePath) obj;
		if (!Arrays.equals(entries, other.entries)) return false;
		return true;
	}

	public boolean equals(final String text) {
		return toString().equals(text);
	}

	/** Creates a {@link FilePath} from the input path and divider. E.g:
	 *
	 * <code><pre>
	 * >> FilePath path = FilePath.from("data/colors/bright", "/");
	 * >> path.toString();
	 * data/colors/bright
	 * </pre></code>
	 *
	 *
	 *
	 * @param path
	 *            the path
	 * @param divider
	 *            the divider
	 * @return the constructed FilePath
	 * @see FileEntry#from(String, String) */
	public static final FilePath from(final String path, final String divider) {
		requireNonNull(path, "path must not be null");
		requireNonNull(divider, "divider must not be null");
		return new FilePath(FileEntry.from(path, divider));
	}

	/** Convenience method for calling <code>FilePath.from(path, "/")</code>
	 *
	 * @param path
	 *            the path
	 * @return */
	public static final FilePath from(final String path) {
		return from(path, "/");
	}
}
