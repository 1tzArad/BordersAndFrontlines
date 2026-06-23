package xyz.ItzArad.baf.dialogs;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.Player;
import xyz.ItzArad.baf.managers.NationCreationSessionManager;
import xyz.ItzArad.baf.models.sessions.NationCreationSession;
import xyz.ItzArad.bafLibs.Colors;
import xyz.ItzArad.bafLibs.models.BAFPlayer;

import java.util.List;

@UtilityClass
public class NationCreationSessionInviteDialog {

    public void open(BAFPlayer player, NationCreationSession session){

        Dialog dialog = Dialog.create(builder ->{
            builder.empty()
                    .base(DialogBase.builder(Colors.color("Nation Invite!"))
                            .canCloseWithEscape(false)
                            .build())
                    .type(DialogType.multiAction(
                            List.of(
                                    getRejectButton(session),
                                    getAcceptButton(session)
                            )
                    ).build()
                    );
        });


        player.getPlayer().showDialog(dialog);
    }

    public ActionButton getAcceptButton(NationCreationSession session){
        ActionButton.Builder builder = ActionButton.builder(Colors.color("<green>Accept"));
        builder.tooltip(Colors.color("<green>Click To Join!"));
        builder.action(DialogAction.customClick(
                (view, audience) -> {
                    if (!(audience instanceof Player player)) return;
                    NationCreationSessionManager.acceptSessionInvite(BAFPlayer.of(player), session);
                }, ClickCallback.Options.builder().uses(1).build()));
        return builder
                .build();
    }

    public ActionButton getRejectButton(NationCreationSession session){
        return ActionButton.builder(Colors.color("<red>Reject"))
                .tooltip(Colors.color("<red>Click To Reject the invitation!"))
                .action(DialogAction.customClick(
                        (view, audience) -> {
                            if(!(audience instanceof Player player)) return;
                            NationCreationSessionManager.rejectSessionInvite(BAFPlayer.of(player), session);
                        }, ClickCallback.Options.builder().uses(1).build()))
                .build();
    }
}
