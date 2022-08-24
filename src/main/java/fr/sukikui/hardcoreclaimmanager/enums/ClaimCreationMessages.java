package fr.sukikui.hardcoreclaimmanager.enums;

import org.bukkit.ChatColor;

public enum ClaimCreationMessages {
    PlayerDoesNotExists(ChatColor.RED + "The player does not exists!"),
    CornersNotInTheSameWorld(ChatColor.RED + "The two corners are not in the same world!"),
    ClaimsRiding(ChatColor.RED + "The claim is riding another claim!"),
    MinClaimSizeNotValid(ChatColor.RED + "The parameter min-claim-size is not valid!"),
    ClaimNotValid(ChatColor.RED + "The claim is not valid!"),
    ClaimTooSmall(ChatColor.RED + "The claim is too small!"),
    ClaimAdminCreated(ChatColor.GREEN + "Claim successfully added! (admin claim)"),
    ClaimCreated(ChatColor.GREEN + "Claim successfully added!"),
    NotEnoughBlock(ChatColor.RED + "You have not enough blocks (%d) to claim this region because the surface of " +
            "this is: %d")
    ;

    private String message;

    ClaimCreationMessages(String message) {
        this.message = message;
    }
}