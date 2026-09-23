package com.minispring2.web.core;

/**
 *
 * @since 2026-09-20 10:05:25
 **/
public interface HandlerMethodArgumentResolver {

    boolean supportsParameter();

    void resolveArgument();
}
