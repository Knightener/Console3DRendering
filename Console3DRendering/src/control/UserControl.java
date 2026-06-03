package control;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

public class UserControl extends JFrame implements KeyListener{
	
    private final Map<Integer, ScheduledFuture<?>> heldKeys = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static int actionDelayMS = 200;
	
	public UserControl() {
		setTitle("Control");
		setSize(400, 300);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setFocusable(true);
		addKeyListener(this);
		setVisible(true);
		requestFocusInWindow();
	}

	@Override
	public void keyPressed(KeyEvent e) {

		Runnable action;
		
		int key = e.getKeyCode();

		if (heldKeys.containsKey(key)) {
			return;
		}

		switch (key) {
		case (KeyEvent.VK_LEFT):
			action = () -> {
				User.turn(RotationDirection.LEFT);
				User.printView();
			};
			break;
		case (KeyEvent.VK_RIGHT):
			action = () -> {
				User.turn(RotationDirection.RIGHT);
				User.printView();
			};
			break;
		case (KeyEvent.VK_UP):
			action = () -> {
				User.turn(RotationDirection.UP);
				User.printView();
			};
			break;
		case (KeyEvent.VK_DOWN):
			action = () -> {
				User.turn(RotationDirection.DOWN);
				User.printView();
			};
			break;
		case (KeyEvent.VK_W):
			action = () -> {
				User.move(TranslationDirection.FORWARDS);
				User.printView();
			};
			break;
		case (KeyEvent.VK_A):
			action = () -> {
				User.move(TranslationDirection.LEFT);
				User.printView();
			};
			break;
		case (KeyEvent.VK_S):
			action = () -> {
				User.move(TranslationDirection.BACKWARDS);
				User.printView();
			};
			break;
		case (KeyEvent.VK_D):
			action = () -> {
				User.move(TranslationDirection.RIGHT);
				User.printView();
			};
			break;
		case (KeyEvent.VK_SHIFT):
			action = () -> {
				User.move(TranslationDirection.UP);
				User.printView();
			};
			break;
		case (KeyEvent.VK_SPACE):
			action = () -> {
				User.move(TranslationDirection.DOWN);
				User.printView();
			};
			break;
		default:
			action = () -> {};
		}
		
		heldKeys.put(key, scheduler.scheduleAtFixedRate(action, 0, actionDelayMS, TimeUnit.MILLISECONDS));

	}

	@Override
	public void keyReleased(KeyEvent e) {
		 heldKeys.remove(e.getKeyCode()).cancel(false);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

}
