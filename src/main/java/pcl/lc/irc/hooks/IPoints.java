/**
 * 
 */
package pcl.lc.irc.hooks;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.WhoisEvent;

import pcl.lc.irc.Config;
import pcl.lc.irc.IRCBot;

@SuppressWarnings("rawtypes")
public class IPoints extends ListenerAdapter {

	@SuppressWarnings({ "unchecked" })
	@Override
	public void onMessage(final MessageEvent event) throws Exception {
		super.onMessage(event);
		String sender = event.getUser().getNick();
		String prefix = Config.commandprefix;
		String ourinput = event.getMessage().toLowerCase();
		String trigger = ourinput.trim();
		if (trigger.length() > 1) {
			String[] firstWord = StringUtils.split(trigger);
			String triggerWord = firstWord[0];
			if (triggerWord.contains(prefix + "+")) {
				Pattern p = Pattern.compile("^\\+?\\d+");
				Matcher m = p.matcher(event.getMessage().replace(prefix,""));
				int newPoints = 0;
				if (m.find()) {
					String[] splitMessage = event.getMessage().split(" ");
					String recipient = splitMessage[1];
					if (!sender.equals(recipient)) {
						try {
							PreparedStatement addPoints = IRCBot.getInstance().getPreparedStatement("addPoints");
							PreparedStatement getPoints = IRCBot.getInstance().getPreparedStatement("getPoints");
							PreparedStatement getPoints2 = IRCBot.getInstance().getPreparedStatement("getPoints");
							if (splitMessage.length == 1) {
								event.respond("Who did you want give points to?");
								return;
							}

							if (getAccount(event.getUser(), event) != null) {
								recipient = getAccount(event.getUser(), event);
							}
							
							getPoints.setString(1, recipient);
							ResultSet points = getPoints.executeQuery();
							if(points.next()){
								newPoints = points.getInt(1) + Integer.parseInt(splitMessage[0].replaceAll("[^\\.0123456789]",""));
							} else {
								newPoints = 1;
							}

							addPoints.setString(1, recipient);
							addPoints.setDouble(2, newPoints);
							addPoints.executeUpdate();

							getPoints2.setString(1, recipient);
							ResultSet points2 = getPoints2.executeQuery();
							if(points.next()){
								event.respond(recipient + " now has " + points2.getInt(1) + " points");
							} else {
								event.respond("Error getting " + recipient + "'s points");      	
							}
						} catch (Exception e) {
							e.printStackTrace();
							event.respond("An error occurred while processing this command");
						}
					} else {
						event.respond("You can not give yourself points.");
					}

				}
			}
		}
	}

	public static String getAccount(User u, MessageEvent event) {
		String user = null;
		if (IRCBot.authed.containsKey(u.getNick())) {
			return IRCBot.authed.get(u.getNick());
		} else {
			event.getBot().sendRaw().rawLineNow("WHOIS " + u.getNick());
			WaitForQueue waitForQueue = new WaitForQueue(event.getBot());
			WhoisEvent test;
			try {
				test = waitForQueue.waitFor(WhoisEvent.class);
				waitForQueue.close();
				user = test.getRegisteredAs();
			} catch (InterruptedException ex) {
				event.getUser().send().notice("Please enter a valid username!");
			}

			return user;
		}

	}
}