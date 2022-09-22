package fr.sukikui.hardcoreclaimmanager.enums;

import org.bukkit.ChatColor;

/**
 * Enum to store all messages related to claim creation
 */
public enum ClaimCreationMessages {
    PlayerDoesNotExists(ChatColor.RED + "The player does not exists!"),
    CornersNotInTheSameWorld(ChatColor.RED + "The two corners are not in the same world!"),
    ClaimsRiding(ChatColor.RED + "The claim is riding another claim!"),
    ClaimNotValid(ChatColor.RED + "The claim is not valid!"),
    ClaimTooSmall(ChatColor.RED + "The claim is too small!"),
    ClaimTooBig(ChatColor.RED + "The claim is too big!"),
    ClaimTooShrink(ChatColor.RED + "The claim is too shrink, the minimum width and height is %d but the claim is %d" +
            " block(s) width and %d block(s) height!"),
    ClaimAdminCreated(ChatColor.GREEN + "Claim successfully added! (admin claim)"),
    ClaimCreated(ChatColor.GREEN + "Claim successfully added!"),
    NotEnoughBlock(ChatColor.RED + "You have not enough blocks (%d) to claim this region because the surface of " +
            "this is: %d");

    private String message;

    ClaimCreationMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
