package pvpcore.worlds;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import pvpcore.utils.IKnockbackObject;
import pvpcore.utils.PvPCKnockback;
import pvpcore.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class PvPCWorld implements IKnockbackObject {

    /** The knockback input. */
    private PvPCKnockback knockbackInfo;
    /** The level name. */
    private String levelName;
    /** The corresponding level. */
    private Level level;
    /** Determines whether the KB is enabled. */
    private boolean enabled;

    /**
     * The PvPCWorld constructor.
     * @param level - The level instance.
     */
    public PvPCWorld(Level level)
    {
        this.knockbackInfo = new PvPCKnockback();
        this.enabled = true;
        this.level = level;
        this.levelName = level.getName();
    }

    /**
     * The PvPCWorld constructor.
     * @param level - The level name.
     * @param enabled - Determines whether the world is enabled.
     * @param knockback - The knockback object.
     */
    public PvPCWorld(String level, boolean enabled, PvPCKnockback knockback)
    {
        this.knockbackInfo = knockback;
        this.levelName = level;
        this.level = Server.getInstance().getLevelByName(level);
        this.enabled = enabled;
    }

    /**
     * Gets the level name of the world.
     * @return - The level name.
     */
    public String getLevelName()
    {
        return this.levelName;
    }

    /**
     * Sets the level of the world, mainly used for updates.
     * @param level - The new level.
     */
    public void setLevel(Level level)
    {
        this.levelName = level.getName();
        this.level = level;
    }

    /**
     * Gets the level that this world belongs to.
     * @return - The level.
     */
    public Level getLevel()
    {
        return this.level;
    }

    /**
     * Determines whether the world's knockback is enabled.
     * @return - True if enabled, false otherwise.
     */
    public boolean isKBEnabled()
    {
        return this.enabled;
    }

    /**
     * Sets the kb as enabled or not.
     * @param enabled - The value that determines whether it's enabled.
     */
    public void setKBEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Gets the knockback information.
     * @return - The knockback info.
     */
    public PvPCKnockback getKnockback()
    {
        return this.knockbackInfo;
    }

    /**
     * Determines if each player can use the knockback.
     * @param player - The player object.
     * @param attacker - The player who is attacking the player.
     * @return - true if the players can use the knockback.
     */
    @Override
    public boolean canUseKnockback(Player player, Player attacker)
    {
        if(this.level == null || !this.enabled)
        {
            return false;
        }

        return Utils.levelsEqual(player.getLevel(), this.level)
                && Utils.levelsEqual(attacker.getLevel(), this.level);
    }

    /**
     * Exports the value to a HashMap.
     * @return The hashmap containing the data.
     */
    @Override
    public HashMap<String, Object> export()
    {
        HashMap<String, Object> data = new HashMap<>();
        data.put("kbInfo", this.knockbackInfo.export());
        data.put("kbEnabled", this.enabled);
        return data;
    }

    /**
     * Determines if another object is equal to another object.
     * @param object - The object we are comparing.
     * @return - true if equivalent, false otherwise.
     */
    public boolean equals(Object object)
    {
        if(object instanceof PvPCWorld)
        {
            Level level = ((PvPCWorld) object).level;
            if(level != null && this.level != null)
            {
                return Utils.levelsEqual(this.level, level);
            }
        }

        return false;
    }

    /**
     * Decodes the object into a PvPCWorld object, the new format.
     * @param levelName - The corresponding level name.
     * @param object - The encoded object.
     * @return - A new PvPCWorld instance if successful, null otherwise.
     */
    public static PvPCWorld decode(String levelName, Object object)
    {
        if(
                object instanceof Map
                && ((Map) object).containsKey("kbEnabled")
                && ((Map) object).containsKey("kbInfo")
        )
        {
            PvPCKnockback knockback = PvPCKnockback.decode(((Map) object).get("kbInfo"));
            Object enabled = ((Map) object).get("kbEnabled");

            if(knockback != null && enabled instanceof Boolean)
            {
                return new PvPCWorld(
                        levelName,
                        (boolean)enabled,
                        knockback
                );
            }
        }
        return null;
    }

    /**
     * Decodes the PvPCWorld using the old format.
     * @param levelName - The name of the corresponding level.
     * @param object - The data containing the world.
     * @return PvPCWorld if successful, null otherwise.
     */
    public static PvPCWorld decodeLegacy(String levelName, Object object)
    {
        if(
                object instanceof Map
                && ((Map) object).containsKey("attack-delay")
                && ((Map) object).containsKey("knockback-xz")
                && ((Map) object).containsKey("knockback-y")
                && ((Map) object).containsKey("customkb")
        )
        {
            Object attackDelay = ((Map) object).get("attack-delay");
            Object verticalKB = ((Map) object).get("knockback-y");
            Object horizontalKB = ((Map) object).get("knockback-xz");
            Object enabled = ((Map) object).get("customkb");

            if(
                attackDelay instanceof Number
                && horizontalKB instanceof Number
                && verticalKB instanceof Number
                && enabled instanceof Boolean
            )
            {
                return new PvPCWorld(levelName, (boolean)enabled, new PvPCKnockback(
                                ((Number) horizontalKB).floatValue(),
                                ((Number) verticalKB).floatValue(),
                                ((Number) attackDelay).intValue())
                );
            }
        }
        return null;
    }
}
