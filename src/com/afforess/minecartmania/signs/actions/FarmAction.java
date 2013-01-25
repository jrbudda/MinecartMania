package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.signs.Sign;
import com.afforess.minecartmania.signs.SignAction;

public class FarmAction
  implements SignAction
{
  protected boolean deactivateAllFarming = false;
  protected String farmType = "";

  public FarmAction(Sign sign)
  {
    if (sign.getLine(0).toLowerCase().contains("farm"))
    {
      String line = sign.getLine(1).toLowerCase();
      if (line.contains("off"))
      {
        this.deactivateAllFarming = true;
      }
      else
      {
        if (line.startsWith("["))
        {
          line = line.substring(1, line.length() - 1);
        }
        this.farmType = line;
      }

      sign.addBrackets();
    }
  }

  public boolean execute(MinecartManiaMinecart minecart)
  {
    if (minecart.isStorageMinecart())
    {
      if (this.deactivateAllFarming)
      {
        minecart.setDataValue("Farm", null);
        return true;
      }
      if (!this.farmType.equals(""))
      {
        minecart.setDataValue("Farm", this.farmType);
        return true;
      }
    }

    return false;
  }

  public boolean async()
  {
    return true;
  }

  public boolean valid(Sign sign)
  {
    return (this.deactivateAllFarming) || (!this.farmType.equals(""));
  }

  public String getName()
  {
    return "farmsign";
  }

  public String getFriendlyName()
  {
    return "Farm Sign";
  }
}