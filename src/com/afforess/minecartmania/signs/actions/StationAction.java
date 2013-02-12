package com.afforess.minecartmania.signs.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.block.Sign;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.debug.Logger;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.stations.StationCommands;
import com.afforess.minecartmania.stations.StationConditions;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmania.utils.SignUtils;
import com.afforess.minecartmania.utils.StationUtil;
import com.afforess.minecartmania.utils.StringUtils;

public class StationAction extends SignAction {

	@Override
	public boolean execute(MMMinecart minecart) {
		if (!processStation(minecart)) {
			if(Settings.IntersectionPromptsMode <= 1){
				return new PromptAction().executeAsBlock(minecart, loc);
			}
		}
		return true;
	}

	public boolean processStation(MMMinecart minecart) {
		ArrayList<Sign> signList = SignUtils.getAdjacentSignList(minecart.getLocation(), 2);

		for (Sign sign : signList) {
			convertCraftBookSorter(sign);
			for (int k = 0; k < 4; k++) {
			
				//Trim line
				String val[] = sign.getLine(k).split(":");
				if (val.length != 2) {
					continue;
				}
				//Strip header and ending characters
				val[0] = StringUtils.removeBrackets(val[0]).trim();
				val[1] = StringUtils.removeBrackets(val[1]).trim();
				//end of trimming

				// check for vaid condition on this line
				for (StationConditions e : StationConditions.values()) {
					if (e.result(minecart, val[0])) {
						//execute command
						Logger.debug("Valid station condition " + e.toString());
						for (StationCommands c : StationCommands.values()) {
							if (c.execute(minecart, val[1])){
								Logger.debug("Executed station command " + c.toString());
								return true;
							}
						}

					}
				}

			}
		}


		return false;

	}

	public static boolean MatchStationName(MMMinecart minecart, String str) {
		boolean valid = false;	
		if (!str.toLowerCase().contains("st-")) {
			return false;
		}
		String[] val = str.toLowerCase().split(":"); //Will doing a toLowerCase here mess up regular expressions?  Probably not since we lower case the station name anyway.
		String[] keys = val[0].split("-| ?: ?", 2);  //Split the st- from the station name. the ",2" limits it to the first "-" followed by zero or one non-capturing spaces.
		//The ",2" is needed because regular expressions can have a "-" in them. example "st-[g-z].*"
		//Without the limit, we would have the following array in keys: "st", "[g", "z].*" and then only work with the "[g" portion which is wrong.
		String st = keys[1];                         //Get the station name/simple pattern/regular expression
		String station = MinecartManiaWorld.getMinecartManiaPlayer(minecart.getPlayerPassenger()).getLastStation().toLowerCase();
		Logger.debug("Given Sign Line: " + str + " Given Station setting: " + station);

		switch(com.afforess.minecartmania.config.Settings.StationParingMode){
		case 0: //default with no pattern matching
			valid = station.equalsIgnoreCase(st);
			break;
		case 1: //simple pattern matching
			st = st.replace("\\", "\\\\") //escapes backslashes in case people use them in station names
			.replace(".", "\\.") //escapes period
			.replace("*", ".*") //converts *
			.replace("?", ".") //converts ?
			.replace("#", "\\d") //converts #
			.replace("@", "[a-zA-Z]"); //converts @  NOTE:[A-Z] is probably not needed here since everything is lower case anyway, but left for completeness.
		case 2: //full regex //note the lack of break before this, case 1 comes down here after converting
			valid = station.matches(st); 
			break;
		}
		if (valid && MinecartManiaWorld.getMinecartManiaPlayer(minecart.getPlayerPassenger()).getDataValue("Reset Station Data") == null) {
			if (!StationUtil.isStationCommandNeverResets()) {
				MinecartManiaWorld.getMinecartManiaPlayer(minecart.getPlayerPassenger()).setLastStation("");
			}
		}
		return valid;
	}

	public static CompassDirection convertFromOldDirections(com.afforess.minecartmania.utils.DirectionUtils.CompassDirection old){
		switch(old){
		case EAST:
			return CompassDirection.NORTH;
		case NORTH:
			return CompassDirection.WEST;
		case SOUTH:
			return CompassDirection.EAST;
		case WEST:
			return CompassDirection.SOUTH;
		default:
			return old;
		}
	}
	
	public static CompassDirection convertToOldDirections(com.afforess.minecartmania.utils.DirectionUtils.CompassDirection newd){
		switch(newd){
		case EAST:
			return CompassDirection.SOUTH;
		case NORTH:
			return CompassDirection.EAST;
		case SOUTH:
			return CompassDirection.WEST;
		case WEST:
			return CompassDirection.NORTH;
		default:
			return newd;
		}
	}

	private  void convertCraftBookSorter(Sign sign) {
		if (sign.getLine(1).contains("[Sort]")) {
			if (!sign.getLine(2).trim().isEmpty()) {
				sign.setLine(2, "st-" + sign.getLine(2).trim().substring(1) + ": L");
			}
			if (!sign.getLine(3).trim().isEmpty()) {
				sign.setLine(3, "st-" + sign.getLine(3).trim().substring(1) + ": R");
			}
			sign.setLine(1, "");
			sign.update();
		}
	}

	public static Set<CompassDirection> getRestrictedDirections(MMMinecart minecart) {
		Set<CompassDirection> restricted = new HashSet<CompassDirection>();
		ArrayList<Sign> signList = SignUtils.getAdjacentSignList(minecart.getLocation(), 2);
		for (Sign sign : signList) {
			for (int i = 0; i < 4; i++) {
				if (sign.getLine(i).toLowerCase().contains("restrict")) {
					String[] directions = sign.getLine(i).split(":");
					if (directions.length > 1) {
						for (int j = 1; j < directions.length; j++) {
							if (directions[j].contains("N")) {
								restricted.add(CompassDirection.NORTH);
							}
							if (directions[j].contains("S")) {
								restricted.add(CompassDirection.SOUTH);
							}
							if (directions[j].contains("E")) {
								restricted.add(CompassDirection.EAST);
							}
							if (directions[j].contains("W")) {
								restricted.add(CompassDirection.WEST);
							}
						}
						return restricted;
					}
				}
			}
		}
		return restricted;
	}



	@Override
	public boolean async() {
		return false;
	}

	@Override
	public boolean process(String[] lines) {
		for (String line : lines) {
			if (line.toLowerCase().contains("[station") && !line.toLowerCase().contains("set")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getPermissionName() {
		return "stationsign";
	}

	@Override
	public String getFriendlyName() {
		return "Station";
	}

}
