package fr.sukikui.hardcoreclaimmanager.claim;

import fr.sukikui.hardcoreclaimmanager.enums.ClaimCreationMessages;

public class ClaimResults {
    public Claim claim;
    public ClaimCreationMessages message;

    public ClaimResults(Claim claim, ClaimCreationMessages message) {
        this.claim = claim;
        this.message = message;
    }
}
