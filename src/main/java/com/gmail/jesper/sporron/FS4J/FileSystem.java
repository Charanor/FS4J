package com.gmail.jesper.sporron.FS4J;

import java.util.Optional;

import com.gmail.jesper.sporron.FS4J.util.FilePath;

/** Interface that represents an abstract file system that restricts reading / writing to only
 * specified directories. A file system can handle both directories (e.g. "documents/savegames/") or
 * archives (e.g. "mods/coolmod.zip").
 *
 * @author Jesper Sporron */
public interface FileSystem<T extends FSFile> {
	/**
	 * <p>
	 * Adds the given {@link FilePath} to the search path so that files in that path can be found
	 * with {@link FileSystem#open(FilePath, FileAccessType)}.
	 * </p>
	 *
	 * <p>
	 * The path can either point to a directory (e.g. <code>"savegames/"</code>) or an archive (e.g.
	 * <code>"graphics.zip"</code>).
	 * </p>
	 *
	 * @param path
	 *            the path to add
	 * @param location
	 *            where the file resides
	 * @return <code>true</code> if path was added to the search path, <code>false</code> otherwise.
	 * @see FileLocation
	 * @see FilePath */
	boolean addToSearchPath(FilePath path, FileLocation location);

	/** Returns true if and only if the {@link FilePath} is on the search path. False otherwise.
	 *
	 * @param path
	 *            the path
	 * @return true if and only if the {@link FilePath} is on the search path. False otherwise */
	boolean isOnSearchPath(FilePath path);

	/** Adds all immediate child archives of <code>path</code> to the search path, but not the path
	 * itself. Useful for adding things like a "mods" directory. E.g:
	 *
	 * <code>
	 * <pre>
	 * // mods/
	 * //   coolmod.zip
	 * //   soundmod.zip
	 * //   4kgraphics.zip
	 * //   logs/
	 * //      2019-03-31.txt
	 * //   someotherfolder/
	 * //      somezip.zip
	 * FilePath path = FilePath.from("mods/");
	 *
	 * // coolmod.zip, soundmod.zip, 4kgraphics.zip are now all added to the search path,
	 * // but <b>NOT</b> either logs/, logs/2019-03-31.txt, or somezip.zip.
	 * // mods/ itself is also <b>NOT</b> added to the search path.
	 * fs.addAllArchivesToSearchPath(path);
	 * </pre>
	 * </code>
	 *
	 * @param path
	 *            where to look for archives.
	 * @param location
	 *            where the archives reside.
	 * @return true if and only if ALL archives were added to the file path, false otherwise.
	 * @see FileSystem#addToSearchPath(FilePath, FileLocation)
	 * @see FileLocation
	 * @see FilePath */
	boolean addAllArchivesToSearchPath(FilePath path, FileLocation location);

	/** Sets the write directory. The write directory must not be an archive or a file. The write
	 * directory is always {@link FileLocation#EXTERNAL}. There can only ever exist one write
	 * directory for safety reasons.
	 *
	 * @param path
	 *            the directory
	 * @return true if the directory was set, false otherwise
	 * @see FileLocation
	 * @see FilePath */
	boolean setWriteDirectory(FilePath path);

	/** Returns an {@link Optional} containing the current write directory, or an empty Optional if
	 * no write directory has been set.
	 *
	 * @return an {@link Optional} containing the current write directory. */
	Optional<FilePath> getWriteDirectory();

	/** Opens a file for reading or writing.
	 *
	 * @param path
	 *            the path to the file
	 * @param accessType
	 *            how the file should be opened.
	 * @return an optional containing the {@link FSFile}, or an empty optional if the file does not
	 *         exist.
	 * @see FilePath
	 * @see FileAccessType */
	Optional<T> open(FilePath path, FileAccessType accessType);

	/** Creates a new directory in the write directory. If the path contains several directories all
	 * intermediate directories will be created, e.g. "documents/logs/crashes/" will create
	 * "documents/" and "documents/logs/" if they don't already exist.
	 *
	 * @param path
	 *            where to create the directory.
	 * @return true if and only if the <i>final</i> directory in the path was created (so "crashes/"
	 *         in "documents/logs/crashes/"). */
	boolean createDirectory(FilePath path);

	/** Creates a new file in the write directory. If the directory the file is located in does not
	 * exist this method returns an empty {@link Optional}, otherwise it returns an optional with
	 * the created file.
	 *
	 * @param path
	 *            where to create the file
	 * @return an optional containing the created file, or an empty optional if file could not be
	 *         created. */
	Optional<T> createFile(FilePath path);

	/** Convenience function that converts a string to a {@link FilePath} then calls
	 * {@link FileSystem#addToSearchPath(FilePath, FileLocation)}.
	 *
	 * @see FileSystem#addToSearchPath(FilePath, FileLocation)
	 * @param path
	 *            the string path
	 * @return <code>true</code> if path was added to the search path, <code>false</code> otherwise.
	 * @see FileLocation
	 * @see FilePath */
	default boolean addToSearchPath(final String path, final FileLocation location) {
		return addToSearchPath(FilePath.from(path), location);
	}

	/** Convenience function that converts a string to a {@link FilePath} then calls
	 * {@link FileSystem#addAllArchivesToSearchPath(FilePath, FileLocation)}.
	 *
	 * @param path
	 *            the string path.
	 * @param location
	 *            where the archives reside.
	 * @return true if and only if ALL archives were added to the file path, false otherwise.
	 * @see FileSystem#addToSearchPath(FilePath, FileLocation)
	 * @see FileLocation
	 * @see FilePath */
	default boolean addAllArchivesToSearchPath(final String path, final FileLocation location) {
		return addAllArchivesToSearchPath(FilePath.from(path), location);
	}

	/** Convenience function that converts a string to a {@link FilePath} then calls
	 * {@link FileSystem#setWriteDirectory(FilePath)}.
	 *
	 * @see FileSystem#setWriteDirectory(FilePath)
	 * @param path
	 *            the string path
	 * @return <code>true</code> if path was set at the write directory, <code>false</code>
	 *         otherwise.
	 * @see FileLocation
	 * @see FilePath */
	default boolean setWriteDirectory(final String path) {
		return setWriteDirectory(FilePath.from(path));
	}

	/** Convenience function that converts a string to a {@link FilePath} then calls
	 * {@link FileSystem#open(FilePath, FileAccessType)}.
	 *
	 * @see FileSystem#open(FilePath, FileAccessType)
	 * @param path
	 *            the string path
	 * @param accessType
	 *            which directory we should open the file from
	 * @return an optional containing the {@link FSFile}, or an empty optional if the file does not
	 *         exist.
	 * @see FilePath
	 * @see FileAccessType */
	default Optional<T> open(final String path, final FileAccessType accessType) {
		return open(FilePath.from(path), accessType);
	}
}
