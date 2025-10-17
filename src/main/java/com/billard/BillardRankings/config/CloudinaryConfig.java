package com.billard.BillardRankings.config;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Map;
@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> values = ObjectUtils.asMap(
                "cloud_name", "djeohgclg",
                "api_key", "778676216696754",
                "api_secret", "cMk7USAzkYsH436XCB77yc3acN8"
        );
        return new Cloudinary(values);
    }
}
