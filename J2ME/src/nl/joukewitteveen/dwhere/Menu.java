package nl.joukewitteveen.dwhere;

import java.util.Vector;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;


public class Menu extends MIDlet implements ItemStateListener, CommandListener {
	private Display display;
	private Form menu;
	private TextField recipients;
	private ChoiceGroup trigger;
	private TextField delay;
	private TextField interval;
	private TextField locks;

	public Menu() {
		display = Display.getDisplay(this);
		menu = new Form("DWhere");
		recipients = new TextField("Recipients", null, 225, TextField.ANY);
		trigger = new ChoiceGroup("Trigger", Choice.POPUP, new String[]{ "time", "position" }, new Image[]{ null, null });
		delay    = new TextField("Delay",    null, 4,   TextField.NUMERIC);
		interval = new TextField("Interval", null, 4,   TextField.NUMERIC);
		locks    = new TextField("Locks",    null, 225, TextField.ANY);

		recipients.setString("+44");
		menu.append(recipients);
		menu.append(trigger);
		menu.append(delay);
		menu.append(interval);
		menu.addCommand(new Command("OK", Command.OK, 0));
		menu.setItemStateListener(this);
		menu.setCommandListener(this);
	}

	protected void startApp() throws MIDletStateChangeException {
		display.setCurrent(menu);
	}

	protected void pauseApp() {
	}

	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
	}

	public void itemStateChanged(Item item) {
		if (item != (Item) trigger) return;
		while (menu.size() > 2) menu.delete(menu.size() - 1);
		if (trigger.getSelectedIndex() == 0) {
			menu.append(delay);
			menu.append(interval);
		} else {
			menu.append(locks);
		}
	}

	public void commandAction(Command cmd, Displayable form) {
		Log log = new Log("Event log", 1000);
		display.setCurrent(log.getDisplayable());
		SMS sms = new SMS(log, split(recipients.getString()));
		GPS gps = new GPS(log);
		if (trigger.getSelectedIndex() == 0) {
			(new Interval(log, sms, gps))
				.start(Long.parseLong(delay.getString()) * 60000, Long.parseLong(interval.getString()) * 60000);
		} else {
			(new Position(log, sms, gps))
				.start(split(locks.getString()));
		}
	}

	private Vector split(String string) {
		Vector strings = new Vector();
		int i;

		string = string.replace(',', ' ');
		while(string.length() > 0) {
			i = string.indexOf(' ');
			switch(i){
			case 0:
				string = string.substring(1);
				break;
			case -1:
				strings.addElement(string);
				string = "";
				break;
			default:
				strings.addElement(string.substring(0, i));
				string = string.substring(i + 1);
			}
		}
		return strings;
	}
}


class Log {
	private TextBox text;

	public Log(String title, int maxSize) {
		text = new TextBox(title, null, maxSize, TextField.UNEDITABLE);
	}

	public Displayable getDisplayable(){
		return text;
	}

	public void log(String msg) {
		int end = text.getMaxSize() - msg.length() - 1;
		if (end < 0) {
			return;
		} else if (end < text.size()) {
			text.delete(end, text.size() - end);
		}
		text.insert(msg + "\n", -1);
	}
}
