package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.utils.StringUtils;

public class DataValuecAction extends SignAction {
    protected String setting = null;
    protected String key = null;
    protected Object value = null;

    public DataValuecAction(String setting) {
        this.setting = setting;
        this.key = setting;
        this.value = true;
    }

    public DataValuecAction(String setting, String key, Object value) {
        this.setting = setting;
        this.key = key;
        this.value = value;
    }


    public boolean execute(MMMinecart minecart) {
        minecart.setDataValue(key, value);
        return true;
    }


    public boolean async() {
        return false;
    }


    public boolean process(String[] lines) {
        for (String line : lines) {
            if (line.toLowerCase().contains("[" + setting.toLowerCase())) {
                return true;
            }
        }
        return false;
    }


    public String getPermissionName() {
        return StringUtils.removeWhitespace(setting.toLowerCase()) + "sign";
    }


    public String getFriendlyName() {
        return setting + " Sign";
    }

}
