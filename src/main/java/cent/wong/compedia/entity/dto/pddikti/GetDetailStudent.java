package cent.wong.compedia.entity.dto.pddikti;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;

@Data
public class GetDetailStudent implements Serializable {

    private CommonData dataumum;

    @Data
    public static class CommonData implements Serializable{
        @JsonAlias("ket_keluar")
        private String status;

        @JsonAlias("tgl_keluar")
        private String dateGraduate;

        @JsonAlias("no_seri_ijazah")
        private String diploma;
    }
}
