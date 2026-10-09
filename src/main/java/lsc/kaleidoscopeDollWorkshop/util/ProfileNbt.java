package lsc.kaleidoscopeDollWorkshop.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.nbt.*;
import java.util.UUID;

/** Preserves the original Owner layout, including saves upgraded through CUSTOM_DATA. */
public final class ProfileNbt {
    public static GameProfile named(String name) { return new GameProfile(new UUID(0, 0), name); }
    public static GameProfile toGameProfile(NbtCompound tag) {
        String name = tag.getString("Name");
        GameProfile profile = new GameProfile(tag.containsUuid("Id") ? tag.getUuid("Id") : new UUID(0, 0), name);
        NbtCompound properties = tag.getCompound("Properties");
        for (String key : properties.getKeys()) {
            NbtList values = properties.getList(key, NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < values.size(); i++) {
                NbtCompound value = values.getCompound(i);
                profile.getProperties().put(key, value.contains("Signature")
                        ? new Property(key, value.getString("Value"), value.getString("Signature"))
                        : new Property(key, value.getString("Value")));
            }
        }
        return profile;
    }
    public static NbtCompound writeGameProfile(NbtCompound tag, GameProfile profile) {
        tag.putString("Name", profile.getName());
        tag.putUuid("Id", profile.getId());
        NbtCompound properties = new NbtCompound();
        for (String key : profile.getProperties().keySet()) {
            NbtList values = new NbtList();
            for (Property property : profile.getProperties().get(key)) {
                NbtCompound value = new NbtCompound();
                value.putString("Value", property.value());
                if (property.signature() != null) value.putString("Signature", property.signature());
                values.add(value);
            }
            properties.put(key, values);
        }
        tag.put("Properties", properties);
        return tag;
    }
}
