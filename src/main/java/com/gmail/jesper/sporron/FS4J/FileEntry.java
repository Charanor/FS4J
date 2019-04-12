package com.gmail.jesper.sporron.FS4J;

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
		if (entry == null) throw new NullPointerException("Entry cannot be null.");
		this.entry = entry;
	}

	/** Checks if this file entry starts with some text.
	 *
	 * @param prefix
	 * @return true if this entry starts with prefix, false otherwise
	 * @see String#startsWith(String) */
	public boolean startsWith(final String prefix) {
		return entry.startsWith(prefix);
	}

	/** Checks if this file entry ends with some text.
	 *
	 * @param suffix
	 * @return true if this entry ends with suffix, false otherwise
	 * @see String#endsWith(String) */
	public boolean endsWith(final String suffix) {
		return entry.endsWith(suffix);
	}

	/** Checks if this file entry contains some text.
	 *
	 * @param s
	 * @return true if this entry contains s, false otherwise
	 * @see String#contains(CharSequence) */
	public boolean contains(final CharSequence s) {
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

	public boolean equals(final String str) {
		return entry == str || entry.equals(str);
	}

	public static final FileEntry[] from(final String path, final String divider)
			throws NullPointerException {
		final String[] split = path.split(Pattern.quote(divider));
		final FileEntry[] result = Stream.of(split).map(FileEntry::new).toArray(FileEntry[]::new);
		return result;
	}

	public static final FileEntry[] from(final String path) throws NullPointerException {
		return from(path, "/");
	}

	public static final String join(final FileEntry[] entries) {
		return String.join("/", Stream.of(entries).map(FileEntry::toString).toArray(String[]::new));
	}
}
