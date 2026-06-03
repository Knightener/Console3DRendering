package control;

import classes3D.R3Point;
import rendering3D.Observer;

public class User {
	/*
	 * Class of only static variables/methods that represents the user that will be
	 * moving through the world.
	 */
	
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

	public static void setPosition(R3Point position) {
		user.setPosition(position);
	}

	public static void setRotationSpeed(double rotationSpeed) {
		User.rotationSpeed = rotationSpeed;
		cosRotSpeed = Math.cos(rotationSpeed);
		sinRotSpeed = Math.sin(rotationSpeed);
	}

	public static void setUser(Observer user) {
		User.user = user;
	}
	
	public static Observer getUser() {
		return user;
	}

	public static void setMovementSpeed(double movementSpeed) {
		if (movementSpeed > 0) {
			User.movementSpeed = movementSpeed;
		} else {
			throw new IllegalArgumentException("Movement speed must be positive.");
		}
	}
	
	public static void printView() {
		World.render();
		user.printView();
	}
}
