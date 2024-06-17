package cent.wong.compedia.constant;

import lombok.Getter;

@Getter
public enum ApprovalStatus {
    WAITING_FOR_APPROVAL(0L),
    ACCEPTED(1L),
    REJECTED(2L);

    private final Long status;

    ApprovalStatus(Long status) {
        this.status = status;
    }

    public static ApprovalStatus getApprovalStatus(Long status){
        for(int i = 0 ; i < ApprovalStatus.values().length ; i++){
            if(ApprovalStatus.values()[i].getStatus() == status){
                return ApprovalStatus.values()[i];
            }
        }

        return null;
    }
}
