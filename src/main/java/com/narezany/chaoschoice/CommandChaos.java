package com.narezany.chaoschoice;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

/** /chaos start | stop | info */
public class CommandChaos extends CommandBase {

    @Override
    public String getCommandName() {
        return "chaos";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/chaos <start|stop|info>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        String sub = args.length > 0 ? args[0].toLowerCase() : "info";
        if ("start".equals(sub)) {
            ChoiceManager.forceStart();
            reply(sender, "§8[Хаос] §7запущен.");
        } else if ("stop".equals(sub)) {
            ChoiceManager.stop();
        } else {
            reply(sender, ChoiceManager.status());
        }
    }

    private void reply(ICommandSender sender, String message) {
        sender.addChatMessage(new ChatComponentText(message));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, new String[] { "start", "stop", "info" });
        }
        return null;
    }
}
