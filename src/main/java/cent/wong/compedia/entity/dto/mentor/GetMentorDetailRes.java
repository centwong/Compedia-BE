package cent.wong.compedia.entity.dto.mentor;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GetMentorDetailRes implements Serializable {

    private Long id;

    private String name;

    private String role;

    private List<String> types;

    private String instagramLink;

    private String linkedinLink;

    private String description;

    private List<String> achievement;

    private List<String> mentoringScope;

    private List<String> notes;
}
