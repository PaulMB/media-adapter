package org.media.container.config;

public interface ListenerCollector<T> {

	void addListener(Listener<T> listener);
}
