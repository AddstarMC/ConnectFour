package ch.dragon252525.connectFour;

import au.com.addstar.monolith.ItemMetaBuilder;
import au.com.addstar.monolith.MonoSpawnEgg;
import au.com.addstar.monolith.lookup.EntityDefinition;
import au.com.addstar.monolith.lookup.Lookup;
import au.com.addstar.monolith.lookup.MaterialDefinition;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 6/12/2016.
 */
public class Utilities {

    private static void msg(Player p, String prefix, String s)
    {
        p.sendMessage(prefix+ s);
    }

    public static void msg(Player p, String s)
    {
        msg(p,ConnectFour.prefix,s);
    }

    public static ItemStack getItem(String name,Integer objamount, Object odata) throws IllegalArgumentException
    {
        MaterialDefinition def = null;
        EntityDefinition edef = null;
        Material mat = Lookup.findByMinecraftName(name);
        Short dataShort = null;
        String dataStr = null;
        if(odata instanceof Short) {
            dataShort = (Short) odata;
            if(mat != null && dataShort > 0){
                def = new MaterialDefinition(mat, dataShort);
            }
            else
            {
                def = getMaterial(name);
            }
        }else if(odata instanceof String) {
            dataStr = (String) odata;
        }
        if (def == null)
                throw new IllegalArgumentException("Unknown material " + name);
        if (def.getData() < 0){
            int data = 0;
            if (dataShort != null) {


                if (data < 0)
                    throw new IllegalArgumentException("Data value cannot be less than 0");
            }
            if(def.getMaterial() == Material.MONSTER_EGG){
                if(dataStr != null)
                            edef = Lookup.findEntityByName(dataStr);
                        }else{
                            throw new IllegalArgumentException("Unable to parse data value " + dataStr);

                        }
                    }

                def = new MaterialDefinition(def.getMaterial(),dataShort);



        // Parse amount
        int amount = def.getMaterial().getMaxStackSize();
        if (objamount != null && objamount > 0) amount = objamount;
        if (amount < 0)
                    throw new IllegalArgumentException("Amount value cannot be less than 0");

        ItemStack item = def.asItemStack(amount);
        if(item.getItemMeta() instanceof SpawnEggMeta){
            if(edef != null) {
                ((SpawnEggMeta) item.getItemMeta()).setSpawnedType(edef.getType());
            }
        }
        return item;
    }

    @SuppressWarnings( "deprecation" )
    private static MaterialDefinition getMaterial(String name)
    {
        // Bukkit name
        Material mat = Material.getMaterial(name.toUpperCase());
        if (mat != null)
            return new MaterialDefinition(mat, (short)-1);

        // Id
        try
        {
            short id = Short.parseShort(name);
            mat = Material.getMaterial(id);
        }
        catch(NumberFormatException ignored)
        {
        }

        if(mat != null)
            return new MaterialDefinition(mat, (short)-1);

        // ItemDB
        return Lookup.findItemByName(name);
    }
}


