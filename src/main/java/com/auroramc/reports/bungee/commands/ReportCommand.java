package com.auroramc.reports.bungee.commands;

import dev.auroramc.laas.bungee.cmd.Commands;
import dev.auroramc.laas.player.role.Role;
import com.auroramc.reports.bungee.Main;
import com.auroramc.reports.bungee.manager.ReportsManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReportCommand extends Commands {

    public ReportCommand() {
        super("report", "reportar");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cEste comando é exlusivo para jogadores!"));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        ProxiedPlayer target;

        if (args.length < 1) {
            player.sendMessage(TextComponent.fromLegacyText("§cUtilize \"/reportar <jogador>\" para reportar um hacker."));
            return;
        }

        target = Main.getInstance().getProxy().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(TextComponent.fromLegacyText("§cEste usuario não se encontra online no momento!"));
            return;
        }

        if (target == player) {
            player.sendMessage(TextComponent.fromLegacyText("§cNão é possível reportar a si mesmo!"));
            return;
        }

        String reason = "Não Informado";

        if (args.length > 1) {
            StringBuilder a = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                a.append(args[i]).append(i == 1 ? "" : " ");
            }

            reason = a.toString();
        }

        ReportsManager.createReport(target.getUniqueId(), player.getUniqueId(), reason);
        player.sendMessage(TextComponent.fromLegacyText("\n§a* Você reportou o jogador " + Role.getPrefixed(target.getName()) + "§a. Um membro de nossa equipe foi notificado e o comportamento deste jogador será analisado em breve.\n\n §a* O uso abusivo deste comando poderá resultar em punição.\n"));
    }

}
