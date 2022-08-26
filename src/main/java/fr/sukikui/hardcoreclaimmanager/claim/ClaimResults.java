package fr.sukikui.hardcoreclaimmanager.claim;

import fr.sukikui.hardcoreclaimmanager.enums.ClaimCreationMessages;

public class ClaimResults {
    public Claim claim;
    public ClaimCreationMessages message;
    public int claimSurface;

    public ClaimResults(Claim claim, ClaimCreationMessages message, int claimSurface) {
        this.claim = claim;
        this.message = message;
        this.claimSurface = claimSurface;
    }
}
