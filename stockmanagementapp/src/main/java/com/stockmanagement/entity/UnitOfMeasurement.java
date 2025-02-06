package com.stockmanagement.entity;
public enum UnitOfMeasurement  {
    // Mass
    GRAM ("GRAM"),
    KILOGRAM("KILOGRAM"),
    MILLIGRAM("MILLIGRAM"),

    // Volume
    LITER("LITER"),
    ML("ML"),
	UNDEFINED("UNDEFINED");
	
  private final String value;

	UnitOfMeasurement(String value) {
		this.value = value;
	}
//
//	public String getValue() {
//		return value;
//	}
//
//	public static UnitOfMeasurement fromString(String value) {
//		for (UnitOfMeasurement type : UnitOfMeasurement.values()) {
//			if (type.value.equalsIgnoreCase(value)) {
//				return type;
//			}
//		}
//		throw new IllegalArgumentException("Unknown Storage type: " + value);
//	}
//
	@Override
	public String toString() {
		return value;
	}
  
    
}
