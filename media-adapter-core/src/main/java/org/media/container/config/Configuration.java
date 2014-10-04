package org.media.container.config;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.media.container.merge.io.impl.CommandConfigurationImpl;

@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(name="command", value=CommandConfigurationImpl.class)})
public interface Configuration {
}
