package org.media.container.merge.execution.impl;

import org.media.container.merge.execution.MergeId;

import java.util.UUID;

public class MergeUniqueId implements MergeId {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final UUID id;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public MergeUniqueId() {
		this.id = UUID.randomUUID();
	}

	public MergeUniqueId(String id) {
		this.id = UUID.fromString(id);
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public String toString() {
		return id.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MergeUniqueId that = (MergeUniqueId) o;

		//noinspection RedundantIfStatement
		if (!id.equals(that.id)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
