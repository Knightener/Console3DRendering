package building3D;

import java.util.ArrayList;
import java.util.List;

import classes3D.R3Point;
import rendering3D.Mesh;
import rendering3D.Observer;
import texturing.Texture;
import texturing.TexturePresets;

public class Shape3D {

	// Each component of cornerA assumed to be smaller than cornerB
	private static Mesh getCuboidAux(Observer observer, R3Point cornerA, R3Point cornerB) {
		Texture texture = new Texture(TexturePresets.WHITE, 1);

		List<R3Point> vertices = new ArrayList<>();

		// Binary counting order X -> bit 2 Y -> bit 1 Z -> bit 0, A -> 0 B -> 1
		vertices.add(new R3Point(cornerA.getX(), cornerA.getY(), cornerA.getZ()));
		vertices.add(new R3Point(cornerA.getX(), cornerA.getY(), cornerB.getZ()));
		vertices.add(new R3Point(cornerA.getX(), cornerB.getY(), cornerA.getZ()));
		vertices.add(new R3Point(cornerA.getX(), cornerB.getY(), cornerB.getZ()));
		vertices.add(new R3Point(cornerB.getX(), cornerA.getY(), cornerA.getZ()));
		vertices.add(new R3Point(cornerB.getX(), cornerA.getY(), cornerB.getZ()));
		vertices.add(new R3Point(cornerB.getX(), cornerB.getY(), cornerA.getZ()));
		vertices.add(new R3Point(cornerB.getX(), cornerB.getY(), cornerB.getZ()));

		Mesh cuboid = new Mesh(observer, vertices);

		// Vertices ordered to ensure outward pointing normals. 
		
		// X fixed
		cuboid.unrestrictedCreateFace(texture, 0b000, 0b001, 0b011, 0b010);
		cuboid.unrestrictedCreateFace(texture, 0b100, 0b110, 0b111, 0b101);
		
		// Y fixed
		cuboid.unrestrictedCreateFace(texture, 0b000, 0b100, 0b101, 0b001);
		cuboid.unrestrictedCreateFace(texture, 0b010, 0b011, 0b111, 0b110);
		
		// Z fixed
		cuboid.unrestrictedCreateFace(texture, 0b000, 0b010, 0b110, 0b100);
		cuboid.unrestrictedCreateFace(texture, 0b001, 0b101, 0b111, 0b011);
		
		return cuboid;
	}

	public static Mesh getCuboid(Observer observer, R3Point cornerA, R3Point cornerB) {
		R3Point cornerMin = new R3Point(
			Math.min(cornerA.getX(), cornerB.getX()),
			Math.min(cornerA.getY(), cornerB.getY()), 
			Math.min(cornerA.getZ(), cornerB.getZ()));

		R3Point cornerMax = new R3Point(
			Math.max(cornerA.getX(), cornerB.getX()),
			Math.max(cornerA.getY(), cornerB.getY()), 
			Math.max(cornerA.getZ(), cornerB.getZ()));
		
		return getCuboidAux(observer, cornerMin, cornerMax);
	}
}
