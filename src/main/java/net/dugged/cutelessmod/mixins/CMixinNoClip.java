package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

public abstract class CMixinNoClip {
	@Mixin(EntityPlayer.class)
	public abstract static class MixinEntityPlayer extends Entity {
		public MixinEntityPlayer(final World world) {
			super(world);
		}

		@Shadow
		public abstract boolean isCreative();

		@Shadow
		public PlayerCapabilities capabilities;

		@Redirect(method = "onUpdate()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;isSpectator()Z"))
		private boolean updateNoClipping(final EntityPlayer player) {
			return player.isSpectator() || (Configuration.noClip && player.isCreative() && player.capabilities.isFlying);
		}

		@Override
		public void move(MoverType type, final double x, final double y, final double z) {
			if (type == MoverType.SELF || !(Configuration.noClip && this.isCreative() && this.capabilities.isFlying)) {
				super.move(type, x, y, z);
			}
		}
	}

	@Mixin(EntityRenderer.class)
	public abstract static class MixinEntityRenderer {
		@Redirect(method = "renderWorldPass(IFJ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isSpectator()Z"))
		private boolean fixSpectator(final EntityPlayerSP player) {
			return player.isSpectator() || player.isCreative();
		}
	}
}
