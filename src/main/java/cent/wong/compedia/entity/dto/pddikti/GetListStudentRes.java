package cent.wong.compedia.entity.dto.pddikti;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GetListStudentRes implements Serializable {

    private List<StudentData> mahasiswa;

    @Data
    public static class StudentData implements Serializable{
        private String text;

        @JsonAlias("website-link")
        private String websiteLink;
    }
}
