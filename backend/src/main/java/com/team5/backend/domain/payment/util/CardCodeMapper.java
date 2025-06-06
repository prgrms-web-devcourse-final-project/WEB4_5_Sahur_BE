package com.team5.backend.domain.payment.util;

import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.PaymentErrorCode;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class CardCodeMapper {

    private final Map<String, String> codeToName = new HashMap<>();

    @PostConstruct
    public void init() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new ClassPathResource("data/org_code.csv").getInputStream(),
                        StandardCharsets.UTF_8))) {
            String line;
            boolean isFirst = true;

            while ((line = reader.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }

                String[] token = line.split(",");
                if (token.length < 3) continue;

                String code = token[2].trim();
                String name = token[1].trim();

                codeToName.put(code, name);
            }
        } catch (IOException e) {
            throw new CustomException(PaymentErrorCode.INVALID_CARD_CODE);
        }
    }

    public String getInstitutionName(String code) {
        return codeToName.getOrDefault(code, code);
    }
}
