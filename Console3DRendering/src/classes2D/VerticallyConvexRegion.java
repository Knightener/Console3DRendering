package classes2D;

import array2D.JaggedIntArray2D;

public class VerticallyConvexRegion {

	/*
	 * Similar to VCRegionBuilder, but with lower overhead. Less functionality.
	 * 
	 * A region is built in VCRegionBuilder before being converted to a 
	 * VerticallyConvexRegion  .
	 */
	
	JaggedIntArray2D region;
	int[] rowOffsets;
	int regionOffset;

	public VerticallyConvexRegion(VCRegionBuilder VCRegion) {
		region = new JaggedIntArray2D(VCRegion.region);
		rowOffsets = VCRegion.rowOffsets.stream().mapToInt(x -> x).toArray();
		regionOffset = VCRegion.regionOffset;
	}

	public int getShade(int right, int down) {
		try {
			int a = right - regionOffset;
			int b = down - rowOffsets[a];
			return region.get(a, b);
		} catch (Exception e) {
			throw new IllegalArgumentException("Coordinates must be within region");
		}
	}
	
}
