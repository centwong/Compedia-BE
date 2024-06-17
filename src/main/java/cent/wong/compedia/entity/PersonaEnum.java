package cent.wong.compedia.entity;

import lombok.Getter;

@Getter
public enum PersonaEnum {
    TIKUS("Tikus"),
    ULAR("Ular"),
    ELANG("Elang");

    private final String persona;

    PersonaEnum(String persona){
        this.persona = persona;
    }
}
