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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MergeInstance that = (MergeInstance) o;

		if (!id.equals(that.id)) return false;
		if (!input.equals(that.input)) return false;
		if (message != null ? !message.equals(that.message) : that.message != null) return false;
		//noinspection RedundantIfStatement
		if (!status.equals(that.status)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + status.hashCode();
		result = 31 * result + input.hashCode();
		result = 31 * result + (message != null ? message.hashCode() : 0);
		return result;
	}
}
