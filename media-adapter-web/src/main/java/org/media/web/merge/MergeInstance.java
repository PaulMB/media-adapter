package org.media.web.merge;

import org.media.container.merge.execution.Merge;

import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("UnusedDeclaration")
@XmlRootElement
public class MergeInstance {

	//==================================================================================================================
	// Attributes
	//==================================================================================================================

	private String id;
	private String status;
	private String input;
	private String message;

	//==================================================================================================================
	// Constructors
	//==================================================================================================================

	public MergeInstance() {
	}

	public MergeInstance(Merge merge) {
		this.id = merge.getId().toString();
		this.input = merge.getDefinition().getInput().getName();
		this.message = merge.getMessage();
		this.status = merge.getStatus().name();
	}

	//==================================================================================================================
	// Public methods
	//==================================================================================================================

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
