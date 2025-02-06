package com.stockmanagement.entity;
public enum StorageType {
    // Common Beverage Packaging
    GLASS_BOTTLE("GLASS_BOTTLE"),
    PLASTIC_BOTTLE("PLASTIC_BOTTLE"),
    CAN("CAN"),
    BOX("BOX"),
    PET_BOTTLE("PET_BOTTLE"), // Polyethylene terephthalate, a common plastic for drinks
    TETRA_PACK("TETRA_PACK"),  // A brand of carton packaging, common for juices and milk
    GLASS_JAR("GLASS_JAR"),

    POUCH("POUCH"),          // Common for ready-to-eat food, soups, or sauces
    
   
    TIN_CAN ("TIN_CAN");    // For long-term storage like canned vegetables or bean
    
    private final String value;

	StorageType(String value) {
		this.value = value;
	}
//
//	public String getValue() {
//		return value;
//	}
//
//	public static StorageType fromString(String value) {
//		for (StorageType type : StorageType.values()) {
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