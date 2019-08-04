package server.instance4.pojo;

public enum UserType {

	CUSTOMER("C"), MANAGER("M");

	private String value;

	UserType(final String value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return value;
	}

	public static UserType fromString(String text) {
		for (UserType b : UserType.values()) {
			if (b.value.equalsIgnoreCase(text)) {
				return b;
			}
		}
		throw new IllegalArgumentException(text);
	}

}
