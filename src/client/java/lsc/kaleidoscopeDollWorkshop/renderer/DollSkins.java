package lsc.kaleidoscopeDollWorkshop.renderer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.component.type.ProfileComponent;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/** Resolve dolls removed from the computer before the server's skin lookup finished. */
public final class DollSkins {
    private static final Cache<String, CompletableFuture<ProfileComponent>> PROFILES = CacheBuilder.newBuilder()
            .maximumSize(256).expireAfterAccess(10, TimeUnit.MINUTES).build();
    public static GameProfile resolve(GameProfile profile) {
        if (profile == null || profile.getProperties().containsKey("textures") || profile.getName().isEmpty()) return profile;
        try {
            var future = PROFILES.get(profile.getName(), () -> new ProfileComponent(Optional.of(profile.getName()),
                    Optional.empty(), new PropertyMap()).getFuture());
            var resolved = future.getNow(null);
            return resolved == null ? profile : resolved.gameProfile();
        } catch (Exception ignored) { return profile; }
    }
    public static void clear() { PROFILES.invalidateAll(); }
}
