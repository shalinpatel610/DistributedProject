package server.instance4.pojo;

public enum EventType {

	SEMINAR, CONFERENCE, TRADESHOW;

	public static EventType getEventType(String text) {
		EventType event = isValidEventType(text);
		return event;

	}

	public static EventType isValidEventType(final String event) {
		for (EventType s : EventType.values()) {
			if (s.toString().equalsIgnoreCase(event))
				return s;
		}
		return null;
	}
}
