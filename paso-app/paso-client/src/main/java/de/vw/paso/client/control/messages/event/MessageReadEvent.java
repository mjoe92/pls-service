package de.vw.paso.client.control.messages.event;

public class MessageReadEvent {

	private final Long ressourceId;
	private final Integer subtrahend;

	public MessageReadEvent(Long ressourceId, Integer subtrahend) {
		this.ressourceId = ressourceId;
		this.subtrahend = subtrahend;
	}

	public Long getRessourceId() {
		return ressourceId;
	}

	public Integer getSubtrahend() {
		return subtrahend;
	}

}
