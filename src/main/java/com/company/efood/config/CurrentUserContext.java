package com.company.efood.config;

import com.company.efood.sys.model.CurrentUserInfo;
import com.company.efood.sys.utils.AppUserType;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserContext {
    private static final ThreadLocal<CurrentUserInfo> currentUser = new ThreadLocal<>();

    public static void set(CurrentUserInfo userInfo) {
        currentUser.set(userInfo);
    }

    public static CurrentUserInfo get() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }

    public static Long getUserId() {
        return currentUser.get() != null ? currentUser.get().getUserId() : null;
    }

    public static AppUserType getRole() {
        return currentUser.get() != null ? currentUser.get().getRole() : null;
    }

    public static Long getReferenceId() {
        return currentUser.get() != null ? currentUser.get().getReferenceId() : null;
    }
}
