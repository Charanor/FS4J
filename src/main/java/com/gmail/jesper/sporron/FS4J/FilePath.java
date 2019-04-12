package com.gmail.jesper.sporron.FS4J;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

/** Represents an immutable full file path, e.g. <code>"music/dungeon/dungeon_dark.mp"</code>.
 *
 * @author Jesper Sporron */
public class FilePath implements Iterable<FileEntry> {
	private final FileEntry[] entries;

	public FilePath(final FileEntry[] entries) {
		this.entries = entries;
	}

	/** Checks if this path contains the specified {@link FileEntry}.
	 *
	 * @param entry
	 *            the entry to check
	 * @return true if this path contains the entry, false otherwise */
	public boolean containsEntry(final FileEntry entry) {
		for (final FileEntry ent : entries) {
			if (ent.equals(entry)) return true;
		}
		return false;
	}

	/** Checks if this path contains some text.
	 *
	 * @param text
	 *            the text to check
	 * @return true if this path contains the text, false otherwise
	 * @see String#contains(CharSequence) */
	public boolean containsText(final String text) {
		return toString().contains(text);
	}

	/** Returns a new FilePath with the {@link FileEntry} prepended to this path.
	 *
	 * @param entry
	 *            the entry to append
	 * @return a new FilePath with the FileEntry prepended to this path. */
	public FilePath prepend(final FileEntry entry) {
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
		return other.append(this);
	}

	/** Returns a new FilePath with the {@link FileEntry} appended to this path.
	 *
	 * @param entry
	 *            the entry to append
	 * @return a new FilePath with the FileEntry appended to this path. */
	public FilePath append(final FileEntry entry) {
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
	 * @return a new split FilePath */
	public FilePath subpath(final int start, final int end) {
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
		asString = asString.replaceAll("([\\w\\. ]+)/\\.\\./?", ""); // path/.. -> <nothing>
		asString = asString.replaceAll("/\\./([\\w\\. ]+)", "/$1"); // /./otherpath -> /otherpath
		return FilePath.from(asString);
	}

	/** @see FileEntry#join(FileEntry[]) */
	@Override
	public String toString() {
		return FileEntry.join(entries);
	}

	/** @param numEntries
	 * @return */
	public String toString(final int numEntries) {
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

	public static final FilePath from(final String path) {
		return new FilePath(FileEntry.from(path));
	}
}
