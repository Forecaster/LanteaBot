/**
 * 
 */
package pcl.lc.irc.hooks;

import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import pcl.lc.irc.AbstractListener;
import pcl.lc.irc.Command;
import pcl.lc.irc.Config;
import pcl.lc.irc.IRCBot;
import pcl.lc.utils.Helper;

/**
 * @author Caitlyn
 *
 */
@SuppressWarnings("rawtypes")
public class l33t extends AbstractListener {
	private Command local_command;

	public static String toLeet(String str){
		boolean ck = false;
		boolean s = false;
		if(str.endsWith("ck")){
			ck = true;
			str = str.substring(0, str.length() - 2);
		} else if(str.endsWith("s")){
			s = true;
			str = str.substring(0, str.length() - 1);
		}
		char[] arr = str.toCharArray();

		for(int i=0; i < str.length(); ++i){
			switch(arr[i]){
			case 'a':	arr[i]='@'; break;
			case 'e':	arr[i]='3'; break;
			case 'i':	arr[i]='1'; break;
			case 'o':	arr[i]='0'; break;
			case 'u':	arr[i]='v'; break;
			case 'f':	arr[i]='p'; break;
			case 's':	arr[i]='$'; break;
			case 'g':	arr[i]='9'; break;
			case 'y':	arr[i]='j'; break;
			case 't':	arr[i]='+'; break;
			case '!':	arr[i]='1'; break;
			}
			++i;
			if(Character.isLowerCase(arr[i-1])){
				arr[i-1] = Character.toUpperCase(arr[i-1]);
			} else /*if(Character.isUpperCase(arr[i]))*/ {
				arr[i-1] = Character.toLowerCase(arr[i-1]);
			}
		}

		String result = new String(arr);
		if(ck){
			result = result.concat("x");
		} else if(s) {
			result = result.concat("z");
		}

		return result;
	}

	@Override
	protected void initHook() {
		local_command = new Command("1337", 0);
		IRCBot.registerCommand(local_command, "Returns leetspeak of inputted text");
	}

	public String chan;
	public String target = null;
	@Override
	public void handleCommand(String sender, MessageEvent event, String command, String[] args) {
		if (local_command.shouldExecuteBool(command)) {
			chan = event.getChannel().getName();
		}
	}

	@Override
	public void handleCommand(String nick, GenericMessageEvent event, String command, String[] copyOfRange) {
		if (local_command.shouldExecuteBool(command)) {
			if (!event.getClass().getName().equals("org.pircbotx.hooks.events.MessageEvent")) {
				target = nick;
			} else {
				target = chan;
			}
			String message = "";
			for (String aCopyOfRange : copyOfRange) {
				message = message + " " + aCopyOfRange;
			}
			String s = message.trim();
			Helper.sendMessage(target ,  toLeet(s), nick);
		}	
	}

	@Override
	public void handleMessage(String sender, MessageEvent event, String command, String[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleMessage(String nick, GenericMessageEvent event, String command, String[] copyOfRange) {
		// TODO Auto-generated method stub
		
	}
}
