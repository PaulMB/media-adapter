package org.media.container.info.impl;

import org.junit.Test;
import org.media.container.merge.MergeFactory;

import static org.junit.Assert.assertEquals;

public class TrackIdImplTest {

	@Test
	public void shouldBeEqualForDifferentConstructors() throws Exception {
		assertEquals(MergeFactory.trackId(2), MergeFactory.trackId("2"));
	}

	@Test
	public void shouldHaveAConsistentExternalRepresentation() throws Exception {
		assertEquals("2", MergeFactory.trackId(2).toExternal());
		assertEquals("2", MergeFactory.trackId(2).toString());
	}
}