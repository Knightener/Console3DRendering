package building3D;

import java.util.ArrayList;
import java.util.List;

import classes3D.R3Point;
import rendering3D.Mesh;
import rendering3D.Observer;
import texturing.Texture;
import texturing.TexturePresets;

public class Shape3D {

	public static Mesh getCuboid(Observer observer, R3Point cornerA, R3Point cornerB) {
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

		// X fixed
		cuboid.unrestrictedCreateFace(texture, 0b000, 0b001, 0b011, 0b010);
		cuboid.unrestrictedCreateFace(texture, 0b100, 0b101, 0b111, 0b110);
		
		// Y fixed
		cuboid.unrestrictedCreateFace(texture, 0b000, 0b001, 0b101, 0b100);
		cuboid.unrestrictedCreateFace(texture, 0b010, 0b011, 0b111, 0b110);
		
		// Z fixed
		cuboid.unrestrictedCreateFace(texture, 0b000, 0b010, 0b110, 0b100);
		cuboid.unrestrictedCreateFace(texture, 0b001, 0b011, 0b111, 0b101);
		
		return cuboid; 
	}
}
