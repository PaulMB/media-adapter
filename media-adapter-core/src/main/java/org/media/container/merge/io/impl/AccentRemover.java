package org.media.container.merge.io.impl;

import org.media.container.merge.io.CharacterConverter;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class AccentRemover implements CharacterConverter {

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public String convert(String sequence) {
		final Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		final String decomposed = Normalizer.normalize(sequence, Normalizer.Form.NFD);
		return pattern.matcher(decomposed).replaceAll("");
	}
}
