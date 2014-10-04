package org.media.web.config;

import org.media.container.config.Configuration;
import org.media.web.config.exception.ComponentNotFoundException;
import org.media.web.config.exception.ComponentStorageException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
@Path("media/config")
public class ConfigurationResource {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	@Inject
	private ApplicationConfiguration configuration;

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("executor")
	public ConfigurationDescription getExecutors() throws ComponentNotFoundException {
		return new ConfigurationDescription(this.configuration);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("executor/component")
	public List<String> getExecutorList() {
		return this.configuration.getExecutors();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("executor/component/{name}")
	public Configuration getExecutorConfiguration(@PathParam("name") String name) throws ComponentNotFoundException {
		return this.configuration.getExecutor(name).getConfiguration();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("executor/component/{name}")
	public void setExecutorConfiguration(@PathParam("name") String name, Configuration configuration) throws ComponentNotFoundException, ComponentStorageException {
		this.configuration.setExecutorConfiguration(name, configuration);
	}

	@GET
	@Path("executor/active")
	public String getActiveExecutor() {
		return this.configuration.getActiveExecutor();
	}

	@PUT
	@Path("executor/active")
	public void setActiveExecutor(String activeExecutor) throws ComponentStorageException, ComponentNotFoundException {
		this.configuration.setActiveExecutor(activeExecutor);
	}
}
