package com.justdo.plug.auth.domain.blog;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "blog-service",url="${application.config.blogs-url}")
public interface BlogClient {
    @PostMapping
    Long createBlog(@RequestParam Long memberId);

    @GetMapping("/members/{memberId}")
    Long getBlogId(@PathVariable Long memberId);
}