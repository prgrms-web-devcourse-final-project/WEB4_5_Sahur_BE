package com.team5.backend.global.aspect;

import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.AuthErrorCode;
import com.team5.backend.global.exception.code.MemberErrorCode;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AdminPermissionAspect {

    @Pointcut("@annotation(com.team5.backend.global.annotation.CheckAdmin)")
    public void checkAdminPointcut() {}

    @Before("checkAdminPointcut()")
    public void before(JoinPoint joinPoint) {

        // 현재 로그인된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new CustomException(AuthErrorCode.UNAUTHORIZED);
        }

        // 권한 확인 (ADMIN)
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new CustomException(MemberErrorCode.ADMIN_ONLY);
        }
    }
}
