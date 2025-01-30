package com.wonkglorg.utilitylib.inventory;

/**
 * Enum representing valid sizes of an inventory.
 */
public enum InventorySize{
	ROWS_1(9),
	ROWS_2(18),
	ROWS_3(27),
	ROWS_4(36),
	ROWS_5(45),
	ROWS_6(54);
	private final int size;
	
	
	InventorySize(int size) {
		this.size = size;
	}
	
	public int getSize() {
		return size;
	}
}