package lsc.kaleidoscopeDollWorkshop.network;

import lsc.kaleidoscopeDollWorkshop.KaleidoscopeDollWorkshop;
import lsc.kaleidoscopeDollWorkshop.gui.ComputerScreenHandler;
import lsc.kaleidoscopeDollWorkshop.item.DollItem;
import lsc.kaleidoscopeDollWorkshop.registry.ModItems;
import lsc.kaleidoscopeDollWorkshop.util.ProfileNbt;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

public class ModMessages {
    public record CraftDollPayload(String name, boolean all) implements CustomPayload {
        public static final Id<CraftDollPayload> ID = new Id<>(Identifier.of(KaleidoscopeDollWorkshop.MOD_ID, "craft_doll"));
        public static final PacketCodec<RegistryByteBuf, CraftDollPayload> CODEC = new PacketCodec<>() {
            public CraftDollPayload decode(RegistryByteBuf buf) { return new CraftDollPayload(buf.readString(16), buf.readBoolean()); }
            public void encode(RegistryByteBuf buf, CraftDollPayload value) { buf.writeString(value.name, 16); buf.writeBoolean(value.all); }
        };
        @Override public Id<? extends CustomPayload> getId() { return ID; }
    }

    public static void registerC2SPackets() {
        PayloadTypeRegistry.playC2S().register(CraftDollPayload.ID, CraftDollPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(CraftDollPayload.ID, (payload, context) -> {
            craft(context.player(), payload);
        });
    }

    public static void craft(net.minecraft.server.network.ServerPlayerEntity player, CraftDollPayload payload) {
            if (!payload.name().matches("[A-Za-z0-9_]{1,16}") || player.isSpectator()) return;
            if (!(player.currentScreenHandler instanceof ComputerScreenHandler screen) || !screen.canUse(player)) return;
            var inventory = screen.getInventory();
            ItemStack input = inventory.getStack(0);
            ItemStack output = inventory.getStack(1);
            if (!input.isIn(ItemTags.WOOL)) return;
            if (!output.isEmpty()) {
                var owner = DollItem.getGameProfile(output);
                if (!output.isOf(ModItems.PLAYER_DOLL) || owner == null || !payload.name().equals(owner.getName())) return;
            }
            int space = output.isEmpty() ? 64 : output.getMaxCount() - output.getCount();
            int amount = Math.min(input.getCount(), Math.min(space, payload.all() ? 64 : 1));
            if (amount <= 0) return;
            input.decrement(amount);
            if (output.isEmpty()) {
                output = new ItemStack(ModItems.PLAYER_DOLL, amount);
                DollItem.setGameProfile(output, ProfileNbt.named(payload.name()));
                inventory.setStack(1, output);
                // Resolve off-thread, then update only the same output stack on the server thread.
                ItemStack crafted = output;
                SkullBlockEntity.fetchProfileByName(payload.name()).thenAccept(profile -> player.getServer().execute(() -> {
                    if (inventory.getStack(1) == crafted && profile.isPresent()) {
                        DollItem.setGameProfile(crafted, profile.get());
                        inventory.markDirty();
                        screen.sendContentUpdates();
                    }
                }));
            } else output.increment(amount);
            inventory.markDirty();
            screen.sendContentUpdates();
    }
}
