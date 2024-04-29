package com.justdo.plug.auth.domain.blog;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "blog-service",url="${application.config.blogs-url}")
public interface BlogClient {
    @PostMapping
    void createBlog(@RequestParam Long memberId);
}