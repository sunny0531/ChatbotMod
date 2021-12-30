package com.github.sunny0531.chatbot.mixin;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.github.sunny0531.chatbot.ChatbotKt.respond;

@Mixin(ChatScreen.class)
// Mixins HAVE to be written in java due to constraints in the mixin system.
public class ExampleMixin {
    @Shadow protected TextFieldWidget chatField;
    @Inject(at = @At(value="INVOKE",target="Lnet/minecraft/client/gui/screen/ChatScreen;sendMessage(Ljava/lang/String;)V",shift = At.Shift.AFTER), method = "keyPressed(III)Z")
    void sendMessage(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        respond(this.chatField.getText().trim());
    }
}
