package com.gmail.jesper.sporron.FS4J;

import static java.util.Objects.requireNonNull;

import com.gmail.jesper.sporron.FS4J.util.FilePath;

class FSRegistration {
	public final FilePath path;
	public final FileLocation fileLocation;

	public FSRegistration(final FilePath path, final FileLocation fileLocation) {
		requireNonNull(path, "path must not be null");
		requireNonNull(fileLocation, "fileLocation must not be null");
		this.path = path;
		this.fileLocation = fileLocation;
	}

	public FSRegistration(final String path, final FileLocation fileLocation) {
		requireNonNull(path, "path must not be null");
		requireNonNull(fileLocation, "fileLocation must not be null");
		this.path = FilePath.from(path);
		this.fileLocation = fileLocation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fileLocation.hashCode();
		result = prime * result + path.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final FSRegistration other = (FSRegistration) obj;
		if (fileLocation != other.fileLocation) return false;
		if (!path.equals(other.path)) return false;
		return true;
	}
}
