package control;

import classes3D.R3Point;
import rendering3D.Observer;
import rendering3D.Spotlight;
import zBuffered2DRendering.ZBuffer;
import zBuffered2DRendering.ZImage;

public class User {
	/*
	 * Class of only static variables/methods that represents the user that will be
	 * moving through the world.
	 */
	
	private static Observer user;
	private static ZImage userView;
	
	private static boolean printFrameRenderSpeed = false;
	private static boolean printAvgRenderSpeed = false;
	private static boolean printCoordinates = false;
	
	private static int framesElapsed = 0; 
	private static long totalTimeRendering = 0; 

	private static double rotationSpeed;
	private static double movementSpeed;

	private static double sinRotSpeed;
	private static double cosRotSpeed;

	// Default values
	static {
		setRotationSpeed(0.1);
		setMovementSpeed(0.4);
	}
	
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
		User.userView = user.getView();
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
	
	// Print the time it takes to print the frame after every frame. 
	public static void toggleFrameRenderSpeed() {
		User.printFrameRenderSpeed = !User.printFrameRenderSpeed;
	}
	
	// Print the average time it took to render frames during the entire run after every run. 
	public static void toggleAvgRenderSpeed() {
		User.printAvgRenderSpeed = !User.printAvgRenderSpeed;
	}
	
	// Print the position and orientation of the observer
	public static void toggleCoordinates() {
		User.printCoordinates = !User.printCoordinates;
	}

	public static void printView() {
		try {
			long start = System.nanoTime();			
			World.render();
			if (World.spotlight != null) {
				userView.applyRenderInfo(World.spotlight);
			} else {
				userView.applyRenderInfo();
			}
			userView.display();
			userView.clear();

			long end = System.nanoTime();
			
			if (printFrameRenderSpeed) {
				System.out
					.println("Frame render speed: " + (double) (end - start) / 1000000 + " ms");
			}
			
			User.framesElapsed++;
			User.totalTimeRendering += end - start;
			
			if (printAvgRenderSpeed) {
				System.out.println("Average frame render speed: "
					+ (double) (totalTimeRendering) / (1000000 * framesElapsed) + " ms");
			}
			
			if (printCoordinates) {
				System.out.println(user);
			}
			
		} catch (Exception e) {
			System.out.println("Error loading frame: ");
			e.printStackTrace();
			System.out.println("\n");
		}
	}
}
