package pvpcore.worlds.areas;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import pvpcore.utils.IKnockbackObject;
import pvpcore.utils.PvPCKnockback;
import pvpcore.utils.Utils;

import java.util.HashMap;

public class PvPCArea implements IKnockbackObject {

    /** The level that the pvp area is in.*/
    private Level level;
    /** The knockback variable. */
    private PvPCKnockback knockback;
    /** The position boundaries of the PvPArea. */
    private Vector3 firstPosition, secondPosition;
    /** The name of the area. */
    private String name;
    /** Determines if the PvPArea KB is enabled. */
    private boolean enabled;

    /**
     * The constructor for the PvPArea.
     * @param name - The name of the area.
     * @param level - The level of the area.
     * @param firstPosition - The first position of the area.
     * @param secondPosition - The second position of the area.
     */
    public PvPCArea(String name, Level level, Vector3 firstPosition, Vector3 secondPosition)
    {
        this.name = name;
        this.level = level;
        this.firstPosition = firstPosition;
        this.secondPosition = secondPosition;
        this.knockback = new PvPCKnockback();
        this.enabled = true;
    }

    /**
     * The constructor for the PvPArea.
     * @param name - The name of the PvPArea.
     * @param level - The level of the PvPArea.
     * @param firstPosition - The first position of the area.
     * @param secondPosition - The second position of area.
     * @param knockback - The knockback input of the PvPArea.
     * @param enabled - Determines whether or not the PvPArea is enabled.
     */
    public PvPCArea(String name, String level, Vector3 firstPosition, Vector3 secondPosition, PvPCKnockback knockback, boolean enabled)
    {
        this.name = name;
        this.level = Server.getInstance().getLevelByName(level);
        this.firstPosition = firstPosition;
        this.secondPosition = secondPosition;
        this.enabled = enabled;
    }


    /**
     * Sets the corresponding position.
     * @param position - The Vector3 position variable.
     * @param pos2 - Determines if we are setting the position to the second one.
     */
    public void setPosition(Vector3 position, boolean pos2)
    {
        if(pos2)
        {
            this.secondPosition = position;
        }
        else
        {
            this.firstPosition = position;
        }
    }

    /**
     * Sets the area knockback as enabled or not.
     * @param enabled - Determines whether kb is enabled.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Determines whether the KB is enabled.
     * @return - True if enabled, false otherwise.
     */
    public boolean isEnabled()
    {
        return this.enabled;
    }

    /**
     * Gets the knockback of the area.
     * @return - The knockback object.
     */
    public PvPCKnockback getKnockback()
    {
        return this.knockback;
    }

    /**
     * Gets the name of the area.
     * @return - The name of the area.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Gets the level of the area.
     * @return - The level or null.
     */
    public Level getLevel()
    {
        return this.level;
    }

    /**
     * Determines if each player can use the knockback.
     * @param player - The player object.
     * @param attacker - The player who is attacking the player.
     * @return true if the players can use the knockback.
     */
    @Override
    public boolean canUseKnockback(Player player, Player attacker)
    {
        if(this.level == null || !this.enabled)
        {
            return false;
        }

        Level player1Level = player.getLevel();
        if(
                !Utils.levelsEqual(player1Level, attacker.getLevel())
                || !Utils.levelsEqual(player1Level, this.level)
        )
        {
            return false;
        }

        return this.isWithinArea(player) && this.isWithinArea(attacker);
    }

    /**
     * Determines whether the position is within the provided area.
     * @param position - The input position.
     * @return - True if the position is within the area, false otherwise.
     */
    private boolean isWithinArea(Vector3 position)
    {
        double minX = (double) Utils.getMinValue(this.firstPosition.x, this.secondPosition.x);
        double maxX = (double) Utils.getMaxValue(this.firstPosition.x, this.secondPosition.x);
        double minY = (double) Utils.getMinValue(this.firstPosition.y, this.secondPosition.y);
        double maxY = (double)Utils.getMaxValue(this.firstPosition.y, this.secondPosition.y);
        double minZ = (double)Utils.getMinValue(this.firstPosition.z, this.secondPosition.z);
        double maxZ = (double) Utils.getMaxValue(this.firstPosition.z, this.secondPosition.z);

        return position.x >= minX && position.x <= maxX
                && position.y >= minY && position.y <= maxX
                && position.z >= minZ && position.z <= maxZ;
    }

    /**
     * Exports the value to a HashMap.
     * @return The hashmap containing the data.
     */
    @Override
    public HashMap<String, Object> export()
    {
        HashMap<String, Object> output = new HashMap<>();
        output.put("enabled", this.enabled);
        output.put("first-pos", Utils.vec3ToMap(this.firstPosition));
        output.put("second-pos", Utils.vec3ToMap(this.secondPosition));
        output.put("knockback", this.knockback.export());
        output.put("level", this.level != null ? this.level.getName() : null);
        return output;
    }

    /**
     * Determines if an object is equivalent to another object.
     * @param object - The input object.
     * @return - True if the area is equivalent, false otherwise.
     */
    public boolean equals(Object object)
    {
        if(object instanceof PvPCArea)
        {
            return ((PvPCArea) object).name.equals(this.name);
        }

        return false;
    }

    /**
     * Decodes the data into a new PvPCArea instance.
     * @param name - The name of the PvPArea.
     * @param data - The data value of the PvPArea.
     * @return - Null if unsuccessful, PvPCArea instance otherwise.
     */
    public static PvPCArea decode(String name, Object data)
    {
        // TODO
        return null;
    }

    /**
     * Decodes the data into a new PvPCArea instance based on legacy format.
     * @param name - The name of the PvPArea.
     * @param data - The data value of the PvPArea.
     * @return - Null if unsuccessful, PvPCArea instance otherwise.
     */
    public static PvPCArea decodeLegacy(String name, Object data)
    {
        // TODO:
        return null;
    }
}