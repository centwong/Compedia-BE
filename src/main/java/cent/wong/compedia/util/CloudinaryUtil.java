package cent.wong.compedia.util;

import cent.wong.compedia.entity.CloudinaryResponse;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CloudinaryUtil {

    public CloudinaryResponse extractMap(Map map){
        CloudinaryResponse resp = new CloudinaryResponse();
        resp.setPublicId((String)map.get("public_id"));
        resp.setSecureUrl((String)map.get("secure_url"));
        return resp;
    }

    public byte[] readBytes(DataBuffer dataBuffer){
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        return bytes;
    }
}
