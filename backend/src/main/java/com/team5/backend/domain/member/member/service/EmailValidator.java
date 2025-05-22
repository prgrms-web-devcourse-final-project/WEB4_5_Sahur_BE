package com.team5.backend.domain.member.member.service;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

public class EmailValidator {

    // 이메일 주소의 도메인 MX 레코드 검증
    public static boolean isValidEmailDomain(String email) {

        // 이메일에서 도메인 추출
        String domain = email.substring(email.indexOf('@') + 1);

        // MX 레코드 확인
        return hasMxRecord(domain);
    }

    // 도메인의 MX 레코드 존재 여부 확인
    private static boolean hasMxRecord(String domain) {
        try {

            // DNS 조회를 위한 환경 설정
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");

            // DirContext 생성
            DirContext dirContext = new InitialDirContext(env);

            // MX 레코드 조회
            Attributes attributes = dirContext.getAttributes(domain, new String[] {"MX"});
            Attribute attribute = attributes.get("MX");

            // MX 레코드가 없는 경우
            return attribute != null && attribute.size() > 0;
        } catch (NamingException e) {
            // DNS 조회 실패 시 (도메인이 존재하지 않는 경우 등)
            return false;
        }
    }
}
