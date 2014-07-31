package org.media.web.config;

public interface ApplicationConfiguration {

	ApplicationComponent getExecutorFactory();

	ApplicationComponent getAuthenticator();
}
