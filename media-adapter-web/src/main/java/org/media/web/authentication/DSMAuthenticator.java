package org.media.web.authentication;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.media.container.merge.io.CommandConfiguration;
import org.media.container.merge.io.IOFactory;
import org.media.web.authentication.exception.AuthenticationException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class DSMAuthenticator implements Authenticator {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final CommandConfiguration configuration;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public DSMAuthenticator(Path config) throws IOException {
		this.configuration = IOFactory.loadConfiguration(config);
	}

	public DSMAuthenticator(CommandConfiguration configuration) {
		this.configuration = configuration;
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@Override
	public void authenticate(ContainerRequestContext requestContext) throws AuthenticationException {
		final String token = this.getHeaderIgnoreCase(requestContext, "x-syno-token");
		final Cookie cookie = requestContext.getCookies().get("id");
		if ( cookie == null ) {
			throw new AuthenticationException("No cookie named id");
		}
		final String cookieId = cookie.getValue();
		final String remoteAddress = this.getHeaderIgnoreCase(requestContext, "X-Forwarded-For");

		final CommandLine commandLine = new CommandLine(configuration.getBinary().toString());
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final DefaultExecutor executor = this.createExecutor(outputStream);

		//TODO get environment template from configuration
		final HashMap<String, String> env = new HashMap<>();
		env.put("QUERY_STRING", "SynoToken=" + token);
		env.put("HTTP_COOKIE", "id=" + cookieId);
		env.put("REMOTE_ADDR", remoteAddress);

		try {
			executor.execute(commandLine, env);
		} catch (IOException e) {
			throw new AuthenticationException(e);
		}
		if ( ! outputStream.toString().trim().equals("admin") ) {
			throw new AuthenticationException("Not authorized");
		}
	}

	//==================================================================================================================
	// Private methods
	//==================================================================================================================

	private DefaultExecutor createExecutor(ByteArrayOutputStream outputStream) {
		ExecuteWatchdog watchdog = new ExecuteWatchdog(5 * 1000);
		DefaultExecutor executor = new DefaultExecutor();
		executor.setExitValue(0);
		executor.setStreamHandler(new PumpStreamHandler(outputStream));
		executor.setWatchdog(watchdog);
		return executor;
	}

	private String getHeaderIgnoreCase(ContainerRequestContext requestContext, String headerName) throws AuthenticationException {
		final MultivaluedMap<String, String> headers = requestContext.getHeaders();
		for (String key : headers.keySet()) {
			if ( headerName.toLowerCase().equals(key.toLowerCase()) ) {
				final List<String> values = headers.get(key);
				if ( values.isEmpty() ) {
					throw new AuthenticationException("No header named " + headerName);
				}
				return values.get(0);
			}
		}
		throw new AuthenticationException("No header named " + headerName);
	}
}
