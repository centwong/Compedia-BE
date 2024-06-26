package cent.wong.compedia.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class DateUtil {

    public String convertIntoString(Long epochMilli){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));
        LocalDateTime localDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(epochMilli),
                ZoneId.of("Asia/Jakarta")
        );
        return localDateTime.format(dateTimeFormatter);
    }
}
