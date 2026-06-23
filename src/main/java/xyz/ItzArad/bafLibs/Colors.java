package xyz.ItzArad.bafLibs;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;

import java.util.Random;

@UtilityClass
public class Colors {
    public Component color(String text){
        return deserialize(serialize(legacyAmpersandDeserialize(text))
                .replace("\\<", "<"));
    }

    public String serialize(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    public Component legacyAmpersandDeserialize(String input) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(input.replace("§", "&"))
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public Component executeCommandOnClick(Component component, String cmd, String hover){
        return component.clickEvent(ClickEvent.runCommand("/" + cmd))
                .hoverEvent(HoverEvent.showText(color(hover)));
    }
    public Component executeCodeOnClick(Component component, ClickCallback<Audience> callback, String hover){
        return component.clickEvent(ClickEvent.callback(callback))
                .hoverEvent(HoverEvent.showText(color(hover)));
    }
    public Component executeCodeOnClick(Component component, ClickCallback<Audience> callback){
        return component.clickEvent(ClickEvent.callback(callback));
    }

    public Component executeCommandOnClick(Component component, String cmd){
        return component.clickEvent(ClickEvent.runCommand("/" + cmd));
    }

    public Component deserialize(String s){
        return MiniMessage.miniMessage().deserialize(s)
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public void sendConsoleMessage(String s){
        Bukkit.getConsoleSender().sendMessage(color("<gold>[BorderAndFrontlines] <white>" + s));
    }
    public void sendConsoleMessage(Component s){
        Bukkit.getConsoleSender().sendMessage("<gold>[BorderAndFrontlines] <white>" + s);
    }

    public String generateRandomHexCodeColor(){
        Random random = new Random();
        return String.format("#%06X", random.nextInt(0xFFFFFF + 1));
    }
}
