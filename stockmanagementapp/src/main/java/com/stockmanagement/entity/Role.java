package com.stockmanagement.entity;

public enum Role {
	MANAGER("MANAGER"), SALESMAN("SALESMAN"), ADMIN("ADMIN"), SUPER_ADMIN("SUPER_ADMIN");

	private final String value;

	Role(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static Role fromString(String value) {
		for (Role role : Role.values()) {
			if (role.value.equalsIgnoreCase(value)) {
				return role;
			}
		}
		throw new IllegalArgumentException("Unknown role: " + value);
	}

	@Override
	public String toString() {
		return value;
	}
}
