package com.gmail.jesper.sporron.FS4J.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

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
	private FileType type;

	public NIOFSRegistration(final FilePath filePath, final FileLocation location)
			throws URISyntaxException {
		if (filePath == null) {
			LOGGER.error("filePath is null.");
			throw new NullPointerException("filePath must not be null.");
		}

		if (location == null) {
			LOGGER.error("location is null.");
			throw new NullPointerException("location must not be null.");
		}

		this.filePath = filePath;
		this.location = location;
		this.type = FSUtils.tryPredictFileType(filePath);
	}

	public Path getPath() {
		return getPath(null);
	}

	public Path getPath(final FilePath appendPath) {
		String strPath;
		if (appendPath != null) {
			strPath = filePath.append(appendPath).toString();
		} else {
			strPath = filePath.toString();
		}

		switch (location) {
		case EXTERNAL: {
			final Path path = Paths.get(strPath);
			this.type = FSUtils.getFileType(path);
			return path;
		}
		case INTERNAL: {
			if (!strPath.startsWith("/")) strPath = "/" + strPath;
			final URL resource = getClass().getClassLoader().getResource(strPath);
			if (resource == null) {
				LOGGER.error("Could not find resource '{}'.", strPath);
				throw new NullPointerException("Could not find resource " + strPath);
			}
			URI uri;
			try {
				uri = resource.toURI();
			} catch (final URISyntaxException e) {
				LOGGER.error("Could not create path for '{}' ({})", strPath, location);
				throw new IllegalStateException(e);
			}
			final Path path = Paths.get(uri);
			this.type = FSUtils.getFileType(path);
			return path;
		}
		default:
			throw new IllegalStateException(
					String.format("Illegal FileLocation '%s' found.", location));
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
