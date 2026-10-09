package lsc.kaleidoscopeDollWorkshop.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.block.entity.DollBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

import java.util.Map;

public class DollBlockModel extends GeoModel<DollBlockEntity> {
    private static final Identifier STEVE_MODEL = Identifier.of(KaleidoscopeDollWorkshop.MOD_ID, "geo/steve.json");
    private static final Identifier ALEX_MODEL = Identifier.of(KaleidoscopeDollWorkshop.MOD_ID, "geo/alex.json");
    private static final Identifier STEVE_TEXTURE = Identifier.of("minecraft", "textures/entity/player/wide/steve.png");

    @Override
    public Identifier getModelResource(DollBlockEntity object) {
        GameProfile profile = DollSkins.resolve(object.getOwnerProfile());
        if (profile != null) {
            if (MinecraftClient.getInstance().getSkinProvider().getSkinTextures(profile).model()
                    == net.minecraft.client.util.SkinTextures.Model.SLIM) return ALEX_MODEL;
        }
        return STEVE_MODEL;
    }

    @Override
    public Identifier getTextureResource(DollBlockEntity object) {
        GameProfile profile = DollSkins.resolve(object.getOwnerProfile());
        if (profile != null) {
            return MinecraftClient.getInstance().getSkinProvider().getSkinTextures(profile).texture();
        }
        return STEVE_TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(DollBlockEntity animatable) {
        return null;
    }
}
