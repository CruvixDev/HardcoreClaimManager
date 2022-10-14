package fr.sukikui.hardcoreclaimmanager.enums;

import fr.sukikui.hardcoreclaimmanager.Messages;
import org.bukkit.ChatColor;

/**
 * Enum to store all messages related to claim creation
 */
public enum ClaimCreationMessages {
    PlayerDoesNotExists(ChatColor.RED + Messages.getMessages("player_not_exist")),
    CornersNotInTheSameWorld(ChatColor.RED + Messages.getMessages("corner_different_worlds")),
    ClaimsRiding(ChatColor.RED + Messages.getMessages("claim_ridding")),
    ClaimNotValid(ChatColor.RED + Messages.getMessages("claim_not_valid")),
    ClaimTooSmall(ChatColor.RED + Messages.getMessages("claim_too_small")),
    ClaimTooBig(ChatColor.RED + Messages.getMessages("claim_too_big")),
    ClaimTooNarrow(ChatColor.RED + Messages.getMessages("claim_too_narrow")),
    ClaimAdminCreated(ChatColor.GREEN + Messages.getMessages("claim_added_admin")),
    ClaimCreated(ChatColor.GREEN + Messages.getMessages("claim_added")),
    NotEnoughBlock(ChatColor.RED + Messages.getMessages("not_enough_blocks"));

    private String message;

    ClaimCreationMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
