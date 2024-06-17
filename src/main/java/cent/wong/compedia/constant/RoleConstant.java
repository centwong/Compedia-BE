package cent.wong.compedia.constant;

import lombok.Getter;

@Getter
public enum RoleConstant {
    USER(1, "User"),
    MENTOR(2, "Mentor"),
    SUPER_ADMIN(3, "SuperAdmin");

    private final int id;

    private final String name;

    RoleConstant(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static RoleConstant getRoleById(int id){
        for(RoleConstant role: RoleConstant.values()){
            if(role.id == id){
                return role;
            }
        }

        return null;
    }
}
