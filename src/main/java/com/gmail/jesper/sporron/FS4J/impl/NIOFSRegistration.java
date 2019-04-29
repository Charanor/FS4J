package com.gmail.jesper.sporron.FS4J.impl;

import static com.gmail.jesper.sporron.FS4J.util.FSUtils.constructNIOPath;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import java.net.URISyntaxException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gmail.jesper.sporron.FS4J.FileLocation;
import com.gmail.jesper.sporron.FS4J.FileType;
import com.gmail.jesper.sporron.FS4J.util.FSUtils;
import com.gmail.jesper.sporron.FS4J.util.FilePath;

public class NIOFSRegistration {
	private static final Logger LOGGER = LoggerFactory.getLogger(NIOFSRegistration.class);

	private final FilePath filePath;
	private final FileLocation location;
	private final FileType type;

	public NIOFSRegistration(final FilePath filePath, final FileLocation location)
			throws URISyntaxException {
		requireNonNull(filePath, "filePath must not be null");
		requireNonNull(location, "location must not be null");

		this.filePath = filePath;
		this.location = location;
		this.type = FSUtils.tryPredictFileType(filePath);
	}

	public Path getPath() {
		return getPath(null);
	}

	public Path getPath(final FilePath appendPath) {
		final FilePath path = isNull(appendPath) ? filePath : filePath.append(appendPath);
		try {
			final Path nioPath = constructNIOPath(path, location);
			return nioPath;
		} catch (final NullPointerException e) {
			LOGGER.error("Could not create path for '{}' ({})", path, location);
			throw e;
		} catch (final URISyntaxException e) {
			LOGGER.error("Could not create path for '{}' ({})", path, location);
			throw new IllegalStateException(e);
		}
	}

	public FilePath getFilePath() {
		return filePath;
	}

	public FileLocation getLocation() {
		return location;
	}

	/** Returns the type of the file. The type is only accurate after a call to
	 * {@link NIOFSRegistration#getPath()} or {@link NIOFSRegistration#getPath(FilePath)}.
	 *
	 * @return */
	public FileType getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filePath == null) ? 0 : filePath.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final NIOFSRegistration other = (NIOFSRegistration) obj;
		if (filePath == null) {
			if (other.filePath != null) return false;
		} else if (!filePath.equals(other.filePath)) return false;
		if (location != other.location) return false;
		return true;
	}

}
