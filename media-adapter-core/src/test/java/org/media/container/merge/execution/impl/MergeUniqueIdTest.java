package org.media.container.merge.execution.impl;

import org.junit.Test;
import org.media.container.merge.MergeFactory;

import java.util.UUID;

import static org.junit.Assert.*;

public class MergeUniqueIdTest {

	@Test
	public void shouldBeEqual() throws Exception {
		final UUID uuid = UUID.randomUUID();
		assertEquals(MergeFactory.id(uuid.toString()), MergeFactory.id(uuid.toString()));
	}

	@Test
	public void shouldNotBeEqualIfDifferentConstructors() throws Exception {
		assertFalse(MergeFactory.id(UUID.randomUUID().toString()).equals(MergeFactory.id()));
	}

	@Test
	public void shouldNotBeEqualIfDifferentSources() throws Exception {
		assertFalse(MergeFactory.id().equals(MergeFactory.id()));
	}
}