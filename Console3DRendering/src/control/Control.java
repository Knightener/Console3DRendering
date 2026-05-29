package control;

import rendering3D.Observer;

public class Control {
	
	private static Observer user;
	
	private static double rotationSpeed;
	private static double movementSpeed;
	
	private static double sinRotSpeed;
	private static double cosRotSpeed;
	
	public static void turn(RotationDirection rotationDirection) {
		user.turn(rotationDirection, cosRotSpeed, sinRotSpeed);
	}
	
	public static void move(TranslationDirection translationDirection) {
		user.move(translationDirection, movementSpeed);
	}
	
	public static void setRotationSpeed(double rotationSpeed) {
		Control.rotationSpeed = rotationSpeed;
		cosRotSpeed = Math.cos(rotationSpeed);
		sinRotSpeed = Math.sin(rotationSpeed);
	}
	
	public static void setUser(Observer user) {
		Control.user = user;
	}

	public static void setMovementSpeed(double movementSpeed) {
		if (movementSpeed > 0) {
			Control.movementSpeed = movementSpeed;
		} else {
			throw new IllegalArgumentException("Movement speed must be positive.");
		}
	}
}
