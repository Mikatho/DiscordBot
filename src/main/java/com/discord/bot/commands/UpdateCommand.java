package com.discord.bot.commands;

import com.discord.bot.UserManagement;
import net.dv8tion.jda.api.entities.*;

public class UpdateCommand implements CommandInterface {

    //!update [value to change] [new value]

    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        //Speichert sich User der Nachricht
        User user = msg.getAuthor();

        //Patters des Commands
        String[] patterns = {"!update address [new value]", "!update interests [new value1, new value2, etc.]", "!update competencies [new value1, new value2, etc.]"};

        //Prüft, ob nur der Command an sich geschrieben wurde
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage("Use one of the following patterns:\n" +
                    "```" + patterns[0] + "\n" + patterns[1] + "\n" + patterns[2] + "```").queue();
            return;
        }

        String[] args = msg.getContentRaw().split(" ", 3);

        args[1] = args[1].toLowerCase();

        //Prüft, ob erster Zusatz-Parameter existiert
        if (!args[1].matches("address|interests|competencies")) {
            channel.sendMessage(String.format("Unknown value to update: `%s` does not exist.", args[1])).queue();
            return;
        }

        //Prüft, ob noch weitere Zusatz-Parameter mitgegeben wurden
        if (args.length == 2) {
            channel.sendMessage("Please add the new values!").queue();
            return;
        }

        //Versucht User zu updaten
        if (!UserManagement.getINSTANCE().update(user.getId(), args[1], args[2])) {
            channel.sendMessage("Could not update your " + args[1]).queue();
            return;
        }

        channel.sendMessage("Successfully updated your: " + args[1]).queue();
    }
}
