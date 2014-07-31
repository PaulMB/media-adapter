package org.media.web.merge;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.media.container.merge.execution.Merge;
import org.media.container.merge.execution.MergeCollector;
import org.media.container.merge.execution.MergeExecutorFactory;
import org.media.container.merge.execution.MergeListener;
import org.media.container.merge.execution.MergeOperation;
import org.media.container.merge.execution.impl.ThreadedMergeCollector;
import org.media.web.merge.listener.ContainerRename;

import javax.ws.rs.core.MediaType;

public class MergeContext implements MergeListener {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private final MergeCollector collector;
	private SseBroadcaster broadcaster;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public MergeContext(MergeExecutorFactory factory) {
		collector = new ThreadedMergeCollector(factory);
		collector.addMergeListener(this);
		collector.addMergeListener(new ContainerRename());
		broadcaster = new SseBroadcaster();
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	public MergeCollector getCollector() {
		return collector;
	}

	public EventOutput addEventOutput() {
		final EventOutput eventOutput = new EventOutput();
		broadcaster.add(eventOutput);
		return eventOutput;
	}

	@Override
	public void onChange(MergeOperation operation, Merge merge) {
		final String name = "merge_" + operation.name().toLowerCase();
		broadcaster.broadcast(new OutboundEvent.Builder().name(name).mediaType(MediaType.APPLICATION_JSON_TYPE).data(MergeInstance.class, new MergeInstance(merge)).build());
	}
}
