package org.media.container.merge;

import java.nio.charset.Charset;

public interface SubtitleDefinition extends TrackDefinition {

	SubtitleDefinition setCharset(Charset charset);

	Charset getCharset();
}
