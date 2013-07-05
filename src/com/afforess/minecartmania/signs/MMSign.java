package com.afforess.minecartmania.signs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.utils.DirectionUtils;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmania.utils.StringUtils;

public class MMSign{
	protected List<SignAction> actions = new ArrayList<SignAction>();
	protected final Block block;
	protected ConcurrentHashMap<Object, Object> data = new ConcurrentHashMap<Object, Object>();
	protected volatile String[] lines;
	protected int updateId = -1;

	protected MMSign(Block block) {
		this.block = block;
		lines = getSign().getLines();
	}

	protected MMSign(Location loc) {
		block = loc.getBlock();
		lines = getSign().getLines();
	}

	public MMSign(org.bukkit.block.Sign sign) {
		block = sign.getBlock();
		lines = getSign().getLines();
	}

	public void addBrackets() {
		int max = getNumLines();

		if (this.hasSignAction(com.afforess.minecartmania.signs.actions.AnnouncementAction.class)) max = 1;
		if (this.hasSignAction(com.afforess.minecartmania.signs.actions.SetStationAction.class)) max = 1;
		
		for (int i = 0; i < max; i++) {
			if (!getLine(i).isEmpty() && getLine(i).length() <= 14) {
				setLine(i, StringUtils.capitalize(StringUtils.addBrackets((getLine(i)))));
			}
		}

	}


	public void addSignAction(SignAction action) {
		actions.add(action);
	}


	public void copy(MMSign sign) {
		if (sign instanceof MMSign) {
			MMSign temp = (MMSign)sign;
			temp.data = this.data;
			temp.lines = this.lines;
			temp.actions = this.actions;

			//copy lines
			for(int i = 0; i < 4; i++)
			{
				String line = "";
				if(lines.length > i) {
					line = lines[i];
				}
				setLine(i, line);
			}
		}
	}


	public boolean textMatches(Object obj) {
		if (obj instanceof MMSign) {
			return hashCode() == ((MMSign)obj).hashCode();
		}
		else if (obj instanceof org.bukkit.block.Sign) {
			return hashCode() == hashCode(((org.bukkit.block.Sign)obj).getLines());
		}
		return false;
	}


	public boolean executeAction(MMMinecart minecart, Class<? extends SignAction> action) {
		Iterator<SignAction> i = actions.iterator();
		boolean success = false;
		while(i.hasNext()){
			SignAction executor = i.next();
			if (action.isInstance(executor)) {
				if (executor.executeAsSign(minecart)) {
					success = true;
				}
			}
		}
		return success;
	}


	public boolean executeActions(MMMinecart minecart) {
		return executeActions(minecart, false);
	}


	public boolean executeActions(final MMMinecart minecart, boolean sync) {
		for (final SignAction action : actions) {
			if (!sync && action.async()) {		
			new Thread(new Runnable(){
				@Override
				public void run() {
					action.executeAsSign(minecart);			
				}
			}).start();			
			}
			else {
				action.executeAsSign(minecart);
			}
		}
		return actions.size() > 0;
	}


	public Block getBlock() {
		return block;
	}


	public final Object getDataValue(Object key) {
		return data.get(key);
	}


	public CompassDirection getFacingDirection() {
		return DirectionUtils.getSignFacingDirection(getSign());
	}


	public final String getLine(int line) {
		return lines[line];
	}

	public final String[] getLines() {
		return lines;
	}

	public Location getLocation() {
		return block.getLocation();
	}


	public final int getNumLines() {
		return lines.length;
	}


	protected final org.bukkit.block.Sign getSign() {
		return ((org.bukkit.block.Sign)getBlock().getState());
	}

	public List<SignAction> getSignActions() {
		return (List<SignAction>) actions;
	}

	public int getX() {
		return getBlock().getX();
	}


	public int getY() {
		return getBlock().getY();
	}


	public int getZ() {
		return getBlock().getZ();
	}

	public boolean hasActions(){
		return !actions.isEmpty();
	}

	public int hashCode() {
		return hashCode(lines);
	}

	private int hashCode(String[] lines) {
		int hash = getBlock().hashCode();
		for (int i = 0; i < lines.length; i++) {
			if (!lines[i].isEmpty()) {
				hash += lines[i].hashCode();
			}
		}
		return hash;
	}

	public boolean hasSignAction(Class<? extends SignAction> action) {
		Iterator<SignAction> i = actions.iterator();
		while(i.hasNext()){
			SignAction executor = i.next();
			if (action.isInstance(executor)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasSignAction(SignAction action) {
		return actions.contains(action);
	}

	public boolean removeSignAction(SignAction action) {
		return actions.remove(action);
	}

	public final void setDataValue(Object key, Object value) {
		if (value != null) {
			data.put(key, value);
		}
		else {
			data.remove(key);
		}
	}

	public final void setLine(int line, String text) {
		setLine(line, text, true);
	}

	public final void setLine(int line, String text, boolean update) {
		if (text.length() < 16) 
			lines[line] = text;
		else
			lines[line] = text.substring(0, 15);
		if (update) {
			org.bukkit.block.Sign sign = getSign();
			sign.setLine(line, lines[line]);
			sign.update();
		}
	}



}
