
package server.instance4.util;
public enum Role {

	CUSTOMER("C"), MANAGER("M");

	private String value;

	Role(final String value) {
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

	public static Role fromString(String text) {
		for (Role b : Role.values()) {
			if (b.value.equalsIgnoreCase(text)) {
				return b;
			}
		}
		throw new IllegalArgumentException(text);
	}
}
