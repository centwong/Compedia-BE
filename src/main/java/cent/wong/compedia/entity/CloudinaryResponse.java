package cent.wong.compedia.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class CloudinaryResponse implements Serializable {

    private String publicId;

    private String secureUrl;
}
